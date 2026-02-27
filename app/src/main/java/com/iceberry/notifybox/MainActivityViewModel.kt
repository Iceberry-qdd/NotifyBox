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

package com.iceberry.notifybox

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.LocaleManagerCompat
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iceberry.notifybox.MainActivityUiState.Loading
import com.iceberry.notifybox.MainActivityUiState.Success
import com.iceberry.notifybox.bg.service.AppNotificationListenerService
import com.iceberry.notifybox.data.SettingsRepository
import com.iceberry.notifybox.datastore.SettingsData
import com.iceberry.notifybox.ui.setting.SettingOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    @param:ApplicationContext private val context: Context
) : ViewModel() {
    val uiState: StateFlow<MainActivityUiState> = settingsRepository.settings.map {
        Success(it, context)
    }.stateIn(
        scope = viewModelScope,
        initialValue = Loading,
        started = SharingStarted.WhileSubscribed(5_000),
    )

    suspend fun setANLSRunning(context: Context) {
        settingsRepository.setANLSRunning(
            AppNotificationListenerService.isServiceRunning(context)
        )
    }
}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState

    data class Success(val settingsData: SettingsData, val context: Context) : MainActivityUiState {
        override fun shouldUseDarkTheme(isSystemDarkTheme: Boolean) =
            when (settingsData.darkThemeConfig) {
                SettingOptions.Theme.DEFAULT -> isSystemDarkTheme
                SettingOptions.Theme.LIGHT -> false
                SettingOptions.Theme.NIGHT -> true
            }

        override val language: LocaleListCompat
            get() = when (settingsData.languageConfig) {
                SettingOptions.Language.DEFAULT -> LocaleManagerCompat.getSystemLocales(context)
                SettingOptions.Language.ZH_CN -> LocaleListCompat.forLanguageTags("zh-Hans")
                SettingOptions.Language.ZH_TW -> LocaleListCompat.forLanguageTags("zh-Hant-TW")
                SettingOptions.Language.EN_US -> LocaleListCompat.forLanguageTags("en-US")
            }
    }

    val language: LocaleListCompat
        get() = AppCompatDelegate.getApplicationLocales()

    /**
     * Returns `true` if dark theme should be used.
     */
    fun shouldUseDarkTheme(isSystemDarkTheme: Boolean) = isSystemDarkTheme
}