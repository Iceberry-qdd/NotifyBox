/*
 * Copyright (c) 2026 Iceberry-qdd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iceberry.notifybox.data

import android.content.Context
import android.os.ParcelFileDescriptor
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.iceberry.notifybox.R
import com.iceberry.notifybox.bg.event.NearbyPayloadEvent
import com.iceberry.notifybox.bg.event.NearbyPayloadTransferUpdateEvent
import com.iceberry.notifybox.data.local.database.AppDatabase
import com.iceberry.notifybox.util.EventBus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NearbyTransferManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val database: AppDatabase
) {
    private val TAG = "NearbyTransferManager"
    private val connectionsClient = Nearby.getConnectionsClient(context)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val eventScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val incomingPayloads = mutableMapOf<Long, Payload>()
    var isMergeMode: Boolean = true

    data class ProgressInfo(
        val endpointId: String,
        val payloadId: Long,
        val progress: Float,
        val isCompleted: Boolean = false,
        val error: String? = null
    )

    val transferProgress = MutableStateFlow<ProgressInfo?>(null)

    init {
        eventScope.launch {
            EventBus.subscribe<NearbyPayloadEvent> { onNearbyPayload(it) }
        }
        eventScope.launch {
            EventBus.subscribe<NearbyPayloadTransferUpdateEvent> { onNearbyPayloadTransferUpdate(it) }
        }
    }

    fun onNearbyPayload(event: NearbyPayloadEvent) {
        incomingPayloads[event.payload.id] = event.payload
    }

    fun onNearbyPayloadTransferUpdate(event: NearbyPayloadTransferUpdateEvent) {
        val progress = if (event.update.totalBytes > 0) {
            event.update.bytesTransferred.toFloat() / event.update.totalBytes.toFloat()
        } else 0f

        when (event.update.status) {
            PayloadTransferUpdate.Status.SUCCESS -> {
                val payload = incomingPayloads.remove(event.update.payloadId)
                if (payload != null) {
                    transferProgress.value = ProgressInfo(event.endpointId, event.update.payloadId, 1f, false)
                    handleIncomingPayload(event.endpointId, payload)
                } else {
                    transferProgress.value = ProgressInfo(event.endpointId, event.update.payloadId, 1f, true)
                }

                val exportFile = File(context.cacheDir, "export_package.zip")
                if (exportFile.exists()) exportFile.delete()
            }

            PayloadTransferUpdate.Status.FAILURE, PayloadTransferUpdate.Status.CANCELED -> {
                incomingPayloads.remove(event.update.payloadId)
                transferProgress.value = ProgressInfo(
                    event.endpointId,
                    event.update.payloadId,
                    progress,
                    true,
                    context.getString(R.string.transfer_interrupted)
                )
            }

            else -> {
                transferProgress.value = ProgressInfo(event.endpointId, event.update.payloadId, progress, false)
            }
        }
    }

    private fun handleIncomingPayload(endpointId: String, payload: Payload) {
        if (payload.type == Payload.Type.FILE) {
            scope.launch {
                try {
                    val pfd = payload.asFile()?.asParcelFileDescriptor()
                    if (pfd != null) {
                        ParcelFileDescriptor.AutoCloseInputStream(pfd).use { inputStream ->
                            processImport(endpointId, payload.id, inputStream, isMerge = isMergeMode)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Import failed: ${e.message}", e)
                    transferProgress.value = ProgressInfo(
                        endpointId, payload.id, 0f, isCompleted = true, error = e.message
                    )
                } finally {
                    payload.asFile()?.asUri()?.let { uri ->
                        try {
                            context.contentResolver.delete(uri, null, null)
                        } catch (e: Exception) {
                            Timber.tag(TAG).e(e, "Failed to delete payload file")
                        }
                    }
                }
            }
        }
    }

    suspend fun createExportPackage(): File = withContext(Dispatchers.IO) {
        val exportFile = File(context.cacheDir, "export_package.zip")
        var iconCount = 0
        ZipOutputStream(FileOutputStream(exportFile)).use { zos ->
            zos.putNextEntry(ZipEntry(".db_version"))
            zos.write(database.openHelper.readableDatabase.version.toString().toByteArray())
            zos.closeEntry()

            val dbFile = context.getDatabasePath("Notifies.db")
            if (dbFile.exists()) addToZip(zos, dbFile, "Notifies.db")

            context.filesDir.listFiles { _, name -> name.endsWith(".webp") }?.forEach { file ->
                addToZip(zos, file, "icons/${file.name}")
                iconCount++
            }
        }
        Timber.tag(TAG).i("Export package created. Notifies: ${database.notifyDao().getNotifiesCount()}, Icons: $iconCount")
        exportFile
    }

    private fun addToZip(zos: ZipOutputStream, file: File, entryName: String) {
        FileInputStream(file).use { fis ->
            zos.putNextEntry(ZipEntry(entryName))
            fis.copyTo(zos)
            zos.closeEntry()
        }
    }

    suspend fun sendData(endpointId: String) {
        val file = createExportPackage()
        connectionsClient.sendPayload(endpointId, Payload.fromFile(file))
    }

    suspend fun processImport(endpointId: String, payloadId: Long, inputStream: InputStream, isMerge: Boolean) =
        withContext(Dispatchers.IO) {
            if (isMerge) mergeImport(endpointId, payloadId, inputStream)
            else overwriteImport(endpointId, payloadId, inputStream)
        }

    private suspend fun overwriteImport(endpointId: String, payloadId: Long, inputStream: InputStream) {
        val currentVersion = database.openHelper.readableDatabase.version
        val tempZip = File(context.cacheDir, "temp_import_overwrite.zip")
        FileOutputStream(tempZip).use { fos -> inputStream.copyTo(fos) }

        try {
            transferProgress.value = ProgressInfo(endpointId, payloadId, 0f, false)

            var dbFound = false
            var versionMatched = false

            ZipInputStream(FileInputStream(tempZip)).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    when (entry.name) {
                        ".db_version" -> {
                            if (zis.readBytes().decodeToString().toIntOrNull() != currentVersion) {
                                throw Exception(context.getString(R.string.import_error_version_mismatch))
                            }
                            versionMatched = true
                        }

                        "Notifies.db" -> dbFound = true
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }

            if (!versionMatched) throw Exception(context.getString(R.string.import_error_unknown_version))
            if (!dbFound) throw Exception(context.getString(R.string.import_error_no_db))

            // 1. Close and Replace Database
            database.close()
            val dbFile = context.getDatabasePath("Notifies.db")
            File(dbFile.path + "-shm").delete()
            File(dbFile.path + "-wal").delete()
            File(dbFile.path + "-journal").delete()

            ZipInputStream(FileInputStream(tempZip)).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    if (entry.name == "Notifies.db") {
                        FileOutputStream(dbFile).use { fos -> zis.copyTo(fos) }
                        break
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }

            // 2. Clear and Extract Icons
            context.filesDir.listFiles { _, name -> name.endsWith(".webp") }?.forEach { it.delete() }

            ZipInputStream(FileInputStream(tempZip)).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    if (entry.name.startsWith("icons/")) {
                        val iconFile = File(context.filesDir, entry.name.substring("icons/".length))
                        FileOutputStream(iconFile).use { fos -> zis.copyTo(fos) }
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }

            transferProgress.value = ProgressInfo(endpointId, payloadId, 1f, true)
        } catch (e: Exception) {
            Timber.tag(TAG).e("Overwrite failed: ${e.message}")
            throw e
        } finally {
            tempZip.delete()
        }
    }

    private suspend fun mergeImport(endpointId: String, payloadId: Long, inputStream: InputStream) {
        val tempDbFile = File(context.cacheDir, "temp_import.db")
        val currentVersion = database.openHelper.readableDatabase.version
        val tempZip = File(context.cacheDir, "temp_import.zip")
        FileOutputStream(tempZip).use { fos -> inputStream.copyTo(fos) }

        try {
            transferProgress.value = ProgressInfo(endpointId, payloadId, 0f, false)

            ZipInputStream(FileInputStream(tempZip)).use { zis ->
                var entry = zis.nextEntry
                val iconFiles = mutableListOf<Pair<String, File>>()
                var dbFound = false
                var versionMatched = false

                while (entry != null) {
                    when {
                        entry.name == ".db_version" -> {
                            if (zis.readBytes().decodeToString().toIntOrNull() != currentVersion) {
                                throw Exception(context.getString(R.string.import_error_version_mismatch))
                            }
                            versionMatched = true
                        }

                        entry.name == "Notifies.db" -> {
                            FileOutputStream(tempDbFile).use { fos -> zis.copyTo(fos) }
                            dbFound = true
                        }

                        entry.name.startsWith("icons/") -> {
                            val iconName = entry.name.substring("icons/".length)
                            val iconFile = File(context.filesDir, iconName)
                            if (!iconFile.exists()) iconFiles.add(entry.name to iconFile)
                        }
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }

                if (!versionMatched) throw Exception(context.getString(R.string.import_error_unknown_version))
                if (!dbFound) throw Exception(context.getString(R.string.import_error_no_db))

                // DB Merge WITH ATTACH (Reverted to before separate connection fix)
                try {
                    val db = database.openHelper.writableDatabase
                    db.execSQL("PRAGMA foreign_keys = OFF")
                    db.execSQL("ATTACH DATABASE '${tempDbFile.absolutePath}' AS import_db")
                    try {
                        db.execSQL("INSERT OR IGNORE INTO main.tb_notify SELECT * FROM import_db.tb_notify")
                    } finally {
                        db.execSQL("DETACH DATABASE import_db")
                        db.execSQL("PRAGMA foreign_keys = ON")
                    }
                } catch (e: Exception) {
                    Timber.tag(TAG).e("Merge failed: ${e.message}")
                    throw Exception(context.getString(R.string.import_error_merge_failed, e.message))
                }

                ZipInputStream(FileInputStream(tempZip)).use { zis2 ->
                    var entry2 = zis2.nextEntry
                    while (entry2 != null) {
                        if (entry2.name.startsWith("icons/")) {
                            val iconFile = File(context.filesDir, entry2.name.substring("icons/".length))
                            if (!iconFile.exists()) FileOutputStream(iconFile).use { fos -> zis2.copyTo(fos) }
                        }
                        zis2.closeEntry()
                        entry2 = zis2.nextEntry
                    }
                }

                database.invalidationTracker.refreshVersionsAsync()
                transferProgress.value = ProgressInfo(endpointId, payloadId, 1f, true)
            }
        } finally {
            tempDbFile.delete()
            tempZip.delete()
        }
    }
}
