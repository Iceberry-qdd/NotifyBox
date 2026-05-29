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

package com.iceberry.notifybox

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.iceberry.notifybox.bg.PurgeDataWorker
import com.iceberry.notifybox.ui.log.TimberReleaseTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class NBApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.INFO)
            .build()

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(TimberReleaseTree)
//            Timber.plant(DebugTree())
        } else {
            Timber.plant(TimberReleaseTree)
        }
        setupPurgeDataWorker()
    }

    private fun setupPurgeDataWorker() {
        val now = LocalDateTime.now()
        var scheduledTime = LocalDateTime.of(now.toLocalDate(), LocalTime.of(2, 0))
        if (now.isAfter(scheduledTime)) {
            scheduledTime = scheduledTime.plusDays(1)
        }
        val initialDelay = Duration.between(now, scheduledTime)

        val purgeRequest = PeriodicWorkRequestBuilder<PurgeDataWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "PurgeDataWork",
            ExistingPeriodicWorkPolicy.KEEP,
            purgeRequest
        )
    }
}
