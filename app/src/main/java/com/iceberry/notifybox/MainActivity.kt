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

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.iceberry.notifybox.bg.NotificationMonitorWorker
import com.iceberry.notifybox.bg.service.AppNotificationListenerService
import com.iceberry.notifybox.data.SettingsRepository
import com.iceberry.notifybox.ui.NBApp
import com.iceberry.notifybox.ui.rememberNBAppState
import com.iceberry.notifybox.ui.theme.NBApplicationTheme
import com.iceberry.notifybox.util.NotificationUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity() : ComponentActivity() {
    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        NotificationUtils.registerServiceStatusChannel(this)
        NotificationMonitorWorker.Companion.startUp(application)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingsRepository.setANLSRunning(
                    AppNotificationListenerService.isServiceRunning(
                        this@MainActivity
                    )
                )
            }
        }

        setContent {
            val appState = rememberNBAppState()
            NBApplicationTheme {
                NBApp(appState)
            }
        }

    }
}