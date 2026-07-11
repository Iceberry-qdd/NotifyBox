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
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.os.LocaleListCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.iceberry.notifybox.bg.NotificationMonitorWorker
import com.iceberry.notifybox.bg.event.NearbyConnectionRequestEvent
import com.iceberry.notifybox.bg.event.NearbyConnectionResponseEvent
import com.iceberry.notifybox.data.SettingsRepository
import com.iceberry.notifybox.ui.NBApp
import com.iceberry.notifybox.ui.rememberNBAppState
import com.iceberry.notifybox.ui.theme.NBApplicationTheme
import com.iceberry.notifybox.util.NotificationUtils
import com.iceberry.notifybox.util.isSystemInDarkTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity() : AppCompatActivity() {
    @Inject
    lateinit var settingsRepository: SettingsRepository
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        var shouldUseDarkTheme by mutableStateOf(resources.configuration.isSystemInDarkTheme)

        NotificationUtils.registerServiceStatusChannel(this)
        NotificationMonitorWorker.startUp(application)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    isSystemInDarkTheme(),
                    viewModel.uiState
                ) { systemDark, uiState ->
                    Pair(
                        uiState.shouldUseDarkTheme(systemDark),
                        uiState.language
                    )
                }
                    .distinctUntilChanged()
                    .collect { (darkTheme, language) ->
                        shouldUseDarkTheme = darkTheme
                        enableEdgeToEdge(
                            statusBarStyle = SystemBarStyle.auto(
                                lightScrim = android.graphics.Color.TRANSPARENT,
                                darkScrim = android.graphics.Color.TRANSPARENT,
                            ) { darkTheme },
                            navigationBarStyle = SystemBarStyle.auto(
                                lightScrim = android.graphics.Color.TRANSPARENT,
                                darkScrim = android.graphics.Color.TRANSPARENT,
                            ) { darkTheme },
                        )
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            window.isNavigationBarContrastEnforced = false
                        }
                        changeLanguage(language)
                        viewModel.setANLSRunning(this@MainActivity)
                    }
            }
        }

        setContent {
            val appState = rememberNBAppState()
            NBApplicationTheme(
                darkTheme = shouldUseDarkTheme
            ) {
                NBApp(appState)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNearbyConnectionRequest(event: NearbyConnectionRequestEvent) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.nearby_connection_title, event.info.endpointName))
            .setMessage(getString(R.string.nearby_connection_message, event.info.authenticationDigits))
            .setPositiveButton(R.string.accept_connection) { dialog, _ ->
                EventBus.getDefault().post(NearbyConnectionResponseEvent(event.endpointId, true))
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                EventBus.getDefault().post(NearbyConnectionResponseEvent(event.endpointId, false))
                dialog.dismiss()
            }
            .setIcon(R.drawable.ic_launcher_foreground)
            .show()
    }

    fun changeLanguage(language: LocaleListCompat) {
        AppCompatDelegate.setApplicationLocales(language)
    }
}


