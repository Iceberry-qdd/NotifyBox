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

package com.iceberry.notifybox.datastore

import androidx.datastore.core.DataStore
import com.iceberry.notifybox.ui.setting.SettingOptions
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class SettingsData(
    val darkThemeConfig: SettingOptions.Theme,
    val languageConfig: SettingOptions.Language,
    val shouldHidePolicy: Boolean,
    val isANLSRunning: Boolean
)

class NBSettingsDatasource @Inject constructor(
    private val settings: DataStore<Settings>,
) {
    val settingsData = settings.data
        .map {
            SettingsData(
                darkThemeConfig = when (it.darkThemeConfig) {
                    null,
                    DarkThemeConfigProto.DARK_THEME_CONFIG_UNSPECIFIED,
                    DarkThemeConfigProto.UNRECOGNIZED,
                    DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM,
                        ->
                        SettingOptions.Theme.DEFAULT

                    DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT -> SettingOptions.Theme.LIGHT
                    DarkThemeConfigProto.DARK_THEME_CONFIG_DARK -> SettingOptions.Theme.NIGHT
                },
                languageConfig = when (it.languageConfig) {
                    null,
                    LanguageConfigProto.LANGUAGE_CONFIG_DEFAULT,
                    LanguageConfigProto.UNRECOGNIZED,
                        ->
                        SettingOptions.Language.DEFAULT

                    LanguageConfigProto.LANGUAGE_CONFIG_ZH_CN -> SettingOptions.Language.ZH_CN
                    LanguageConfigProto.LANGUAGE_CONFIG_ZH_TW -> SettingOptions.Language.ZH_TW
                    LanguageConfigProto.LANGUAGE_CONFIG_EN_US -> SettingOptions.Language.EN_US
                },
                shouldHidePolicy = it.shouldHidePolicy,
                isANLSRunning = it.isAnlsRunning
            )
        }

    suspend fun setDarkThemeConfig(darkThemeConfig: SettingOptions.Theme) {
        settings.updateData {
            it.copy {
                this.darkThemeConfig = when (darkThemeConfig) {
                    SettingOptions.Theme.DEFAULT -> DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM
                    SettingOptions.Theme.LIGHT -> DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT
                    SettingOptions.Theme.NIGHT -> DarkThemeConfigProto.DARK_THEME_CONFIG_DARK
                }
            }
        }
    }

    suspend fun setLanguageConfig(languageConfig: SettingOptions.Language) {
        settings.updateData {
            it.copy {
                this.languageConfig = when (languageConfig) {
                    SettingOptions.Language.DEFAULT -> LanguageConfigProto.LANGUAGE_CONFIG_DEFAULT
                    SettingOptions.Language.ZH_CN -> LanguageConfigProto.LANGUAGE_CONFIG_ZH_CN
                    SettingOptions.Language.ZH_TW -> LanguageConfigProto.LANGUAGE_CONFIG_ZH_TW
                    SettingOptions.Language.EN_US -> LanguageConfigProto.LANGUAGE_CONFIG_EN_US
                }
            }
        }
    }

    suspend fun setShouldHideOnboarding(shouldHidePolicy: Boolean) {
        settings.updateData {
            it.copy { this.shouldHidePolicy = shouldHidePolicy }
        }
    }

    suspend fun setANLSRunning(isAnlsRunning: Boolean) {
        settings.updateData {
            it.copy {
                this.isAnlsRunning = isAnlsRunning
            }
        }
    }
}