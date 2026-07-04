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

package com.iceberry.notifybox.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.iceberry.notifybox.MainActivity
import com.iceberry.notifybox.R

class NotificationUtils {
    companion object {
        const val LISTEN_SERVICE_STATUS_ID = 0x10000000
        const val LISTEN_SERVICE_STATUS_CHANNEL_ID = "monitor"

        fun registerServiceStatusChannel(context: Context) {
            val channel = NotificationChannel(
                LISTEN_SERVICE_STATUS_CHANNEL_ID,
                context.getString(R.string.notification_monitor_channel),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.notification_monitor_description)
            }
            val notificationManager: NotificationManager? =
                getSystemService(context, NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }

        @Suppress("SameReturnValue")
        fun createServiceStatusNotify(context: Context): Int {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent: PendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val builder =
                NotificationCompat
                    .Builder(context, LISTEN_SERVICE_STATUS_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentTitle(context.getString(R.string.notification_listening_title))
                    .setContentText(context.getString(R.string.click_for_details))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .setAutoCancel(false)

            with(NotificationManagerCompat.from(context)) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    notify(LISTEN_SERVICE_STATUS_ID, builder.build())
                    return@with
                }
            }
            return LISTEN_SERVICE_STATUS_ID
        }

        fun removeServiceStatusNotify(context: Context) {
            NotificationManagerCompat.from(context).cancel(LISTEN_SERVICE_STATUS_ID)
        }
    }
}