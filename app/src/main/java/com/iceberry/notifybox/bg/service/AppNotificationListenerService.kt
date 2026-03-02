/*
 * Copyright (c) 2025 Iceberry-qdd.
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

package com.iceberry.notifybox.bg.service

import android.app.ActivityManager
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.iceberry.notifybox.data.DefaultNotifyRepository
import com.iceberry.notifybox.data.DefaultSettingsRepository
import com.iceberry.notifybox.data.local.database.Notify
import com.iceberry.notifybox.network.di.AppDispatchers.IO
import com.iceberry.notifybox.network.di.ApplicationScope
import com.iceberry.notifybox.network.di.Dispatcher
import com.iceberry.notifybox.util.IconUtils
import com.iceberry.notifybox.util.NotificationUtils
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@AndroidEntryPoint
class AppNotificationListenerService() : NotificationListenerService() {
    @Inject
    lateinit var notifyRepository: DefaultNotifyRepository

    @Inject
    lateinit var settingsRepository: DefaultSettingsRepository

    @Inject
    @Dispatcher(IO)
    lateinit var dispatcher: CoroutineDispatcher

    @Inject
    @ApplicationScope
    lateinit var scope: CoroutineScope

    @Inject
    @ApplicationContext
    lateinit var context: Context

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val title = sbn?.notification?.extras?.getString(Notification.EXTRA_TITLE)
        val content = sbn?.notification?.extras?.getString(Notification.EXTRA_TEXT)
        val packageName = sbn?.packageName
        val displayName = getDisplayName(context, packageName)

        if (packageName == null){
            Timber.tag("sbn-${sbn?.id}").w("The packageName is null, skipped")
            return
        }
        if (title == null && content == null){
            Timber.tag("sbn-${packageName}").w("[${sbn.id}]The title or content is null, skipped")
            return
        }

        Timber.tag("sbn-${packageName}").i("${sbn.id}")

        scope.launch {
            val notify = withContext(dispatcher) {
                val notification = sbn.notification
                val largeIcon = notification.getLargeIcon()?.let {
                    IconUtils.smartSaveToStorage(it, context)
                }
                val smallIcon = notification?.smallIcon.let {
                    IconUtils.smartSaveToStorage(it, context)
                }

                Notify(
                    title = title,
                    content = content,
                    smallIcon = smallIcon,
                    largeIcon = largeIcon,
                    packageName = packageName,
                    displayName = displayName,
                    createdTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(sbn.postTime), ZoneId.systemDefault()),
                    notifyId = sbn.id,
                    channel = notification.channelId,
                    priority = notification.priority
                )
            }

            notifyRepository.add(notify)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scope.launch {
            settingsRepository.setANLSRunning(true)
        }
        Timber.tag("NLService").i("Start notify listener service...")
        Toast.makeText(context, "启动监听服务...", Toast.LENGTH_SHORT).show()
        return START_STICKY
    }

    override fun onDestroy() {
        scope.launch {
            settingsRepository.setANLSRunning(false)
        }
        Timber.tag("NLService").i("Stop notify listener service...")
        Toast.makeText(context, "监听服务已停止", Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun hasPermission(context: Context) =
            NotificationManagerCompat.getEnabledListenerPackages(context)
                .contains(context.packageName)

        suspend fun openSystemSettings(context: Context) = withContext(Dispatchers.Default) {
            try {
                val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                context.startActivity(intent)
            } catch (e: Exception) {
                Timber.tag("openNotificationListenSettings").e(e)
            }
        }

        fun showNotify(activity: Context): Int = NotificationUtils.createServiceStatusNotify(activity)

        fun removeNotify(activity: Context) = NotificationUtils.removeServiceStatusNotify(activity)

        fun isServiceRunning(context: Context): Boolean {
            val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            val runningServices = activityManager.getRunningServices(Int.MAX_VALUE)

            if (runningServices.isEmpty()) {
                Timber.tag("NLService").i("NLService not found, so it's not running!")
                return false
            }

            for (serviceInfo in runningServices) {
                if (AppNotificationListenerService::class.java.name == serviceInfo.service.className) {
                    Timber.tag("NLService").i("NLService found, so it's running!")
                    return true
                }
            }
            Timber.tag("NLService").i("NLService not found, so it's not running!")
            return false
        }

        fun getDisplayName(context: Context, packageName: String?): String? {
            return packageName?.let {
                runCatching {
                    val applicationInfo = context.packageManager.getApplicationInfo(it, ApplicationInfo.FLAG_INSTALLED)
                    context.packageManager.getApplicationLabel(applicationInfo).toString()
                }
                    .onFailure { e -> Timber.tag("NLService").e(e) }
                    .getOrDefault(null)
            }
        }
    }
}