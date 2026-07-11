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

package com.iceberry.notifybox.bg

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.iceberry.notifybox.data.local.database.AppDatabase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.io.File
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

@HiltWorker
class PurgeDataWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val database: AppDatabase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val result = try {
            val threshold = LocalDateTime.now().minusDays(30)
            val notifyDao = database.notifyDao()

            // 1. Get icons to delete
            val iconsToDelete = notifyDao.getIconsOfOldDeletedNotifies(threshold)
            var imgCount = 0

            iconsToDelete.forEach { icons ->
                icons.smallIcon?.let {
                    val file = File(applicationContext.filesDir, it)
                    if (file.exists() && file.delete()) imgCount++
                }
                icons.largeIcon?.let {
                    val file = File(applicationContext.filesDir, it)
                    if (file.exists() && file.delete()) imgCount++
                }
            }

            // 2. Hard delete notifies
            val count = notifyDao.deleteOldDeletedNotifies(threshold)

            Timber.tag("task-purge").i("Purge data success. delete $count records, $imgCount images")
            Result.success()
        } catch (e: Exception) {
            Timber.tag("task-purge").e(e, "Purge data failed.")
            Result.failure()
        }

        scheduleNext(applicationContext, ExistingWorkPolicy.APPEND_OR_REPLACE)
        return result
    }

    companion object {
        private const val WORK_NAME = "PurgeDataDailyWork"
        private const val LEGACY_WORK_NAME = "PurgeDataWork"
        private val scheduledLocalTime: LocalTime = LocalTime.of(2, 0)

        fun scheduleNext(
            context: Context,
            existingWorkPolicy: ExistingWorkPolicy = ExistingWorkPolicy.KEEP
        ) {
            val workManager = WorkManager.getInstance(context)
            workManager.cancelUniqueWork(LEGACY_WORK_NAME)

            val now = LocalDateTime.now()
            var scheduledTime = LocalDateTime.of(now.toLocalDate(), scheduledLocalTime)
            if (!scheduledTime.isAfter(now)) {
                scheduledTime = scheduledTime.plusDays(1)
            }

            val delay = Duration.between(now, scheduledTime).coerceAtLeast(Duration.ZERO)
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()
            val request = OneTimeWorkRequestBuilder<PurgeDataWorker>()
                .setInitialDelay(delay.toMillis(), TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .build()

            workManager.enqueueUniqueWork(WORK_NAME, existingWorkPolicy, request)
            Timber.tag("task-purge").i("Next purge scheduled at $scheduledTime")
        }
    }
}
