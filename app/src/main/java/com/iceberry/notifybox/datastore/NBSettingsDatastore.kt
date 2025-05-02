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

package com.iceberry.notifybox.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class SettingsData(
    val isANLSRunning: Boolean
)

val IS_ANLS_RUNNING_KEY = booleanPreferencesKey("is_anls_running")

class NBSettingsDatastore @Inject constructor(
    private val settingsDatastore: DataStore<Preferences>
) {
    val settingsData = settingsDatastore.data
        .map {
            SettingsData(
                isANLSRunning = it[IS_ANLS_RUNNING_KEY] == true
            )
        }

    suspend fun setANLSRunning(status: Boolean) {
        settingsDatastore.edit {
            it[IS_ANLS_RUNNING_KEY] = status
        }
    }
}