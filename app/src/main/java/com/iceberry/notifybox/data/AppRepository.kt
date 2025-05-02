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

import com.iceberry.notifybox.data.local.database.App
import com.iceberry.notifybox.data.local.database.AppDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface AppRepository {
    val apps: Flow<List<App>>

    suspend fun add(app: App)
}

class DefaultAppRepository @Inject constructor(
    private val appDao: AppDao
) : AppRepository {

    override val apps: Flow<List<App>> = appDao.getApps()

    override suspend fun add(app: App) {
        appDao.insertApp(app)
    }
}
