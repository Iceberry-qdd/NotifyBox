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

package com.iceberry.notifybox.bg

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.iceberry.notifybox.network.di.AppDispatchers.IO
import com.iceberry.notifybox.network.di.Dispatcher
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

@HiltWorker
class NotificationMonitorWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        Log.i("NotificationMonitorWorker::doWork", "Work done successfully.")
        return@withContext Result.success()
    }

    companion object {
        const val WORK_NAME = "NOTIFICATION_MONITOR_WORK"
        fun startUp(context: Context) {
            val workManager = WorkManager.getInstance(context)

            val constraints = Constraints.Builder()
                .setRequiresCharging(false)
                .build()

            val request =
                PeriodicWorkRequestBuilder<NotificationMonitorWorker>(5L, TimeUnit.SECONDS, 5L, TimeUnit.SECONDS)
                    .setConstraints(constraints)
                    .build()

            workManager
                .enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
        }
    }
}