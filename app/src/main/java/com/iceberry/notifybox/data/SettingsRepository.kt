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

package com.iceberry.notifybox.data

import com.iceberry.notifybox.datastore.NBSettingsDatasource
import com.iceberry.notifybox.datastore.SettingsData
import com.iceberry.notifybox.network.di.AppDispatchers.IO
import com.iceberry.notifybox.network.di.Dispatcher
import com.iceberry.notifybox.ui.setting.SettingOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface SettingsRepository {
    val settings: Flow<SettingsData>
    suspend fun setANLSRunning(status: Boolean)
    suspend fun setDarkThemeConfig(darkThemeConfig: SettingOptions.Theme)
    suspend fun setLanguageConfig(languageConfig: SettingOptions.Language)
    suspend fun setShouldHideOnboarding(shouldHidePolicy: Boolean)
}

class DefaultSettingsRepository @Inject constructor(
    private val settingsDatastore: NBSettingsDatasource,
    @param:Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher
) : SettingsRepository {
    override val settings = settingsDatastore.settingsData

    override suspend fun setANLSRunning(status: Boolean) =
        settingsDatastore.setANLSRunning(status)

    override suspend fun setShouldHideOnboarding(shouldHidePolicy: Boolean) =
        settingsDatastore.setShouldHideOnboarding(shouldHidePolicy)

    override suspend fun setDarkThemeConfig(darkThemeConfig: SettingOptions.Theme) =
        settingsDatastore.setDarkThemeConfig(darkThemeConfig)

    override suspend fun setLanguageConfig(languageConfig: SettingOptions.Language) =
        settingsDatastore.setLanguageConfig(languageConfig)
}