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

package com.iceberry.notifybox.data.di

import androidx.paging.PagingSource
import com.iceberry.notifybox.data.AppRepository
import com.iceberry.notifybox.data.DefaultAppRepository
import com.iceberry.notifybox.data.DefaultNotifyRepository
import com.iceberry.notifybox.data.DefaultSettingsRepository
import com.iceberry.notifybox.data.NotifyRepository
import com.iceberry.notifybox.data.SettingsRepository
import com.iceberry.notifybox.data.local.database.App
import com.iceberry.notifybox.data.local.database.Notify
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindsNotifyRepository(
        notifyRepository: DefaultNotifyRepository
    ): NotifyRepository

    @Singleton
    @Binds
    fun bindsAppRepository(
        appRepository: DefaultAppRepository
    ): AppRepository

    @Singleton
    @Binds
    fun bindsSettingsRepository(
        settingsRepository: DefaultSettingsRepository
    ): SettingsRepository
}

class FakeNotifyRepository @Inject constructor() : NotifyRepository {
    override val notifies: PagingSource<Int, Notify> = TODO()

    override suspend fun add(notify: Notify) {
        throw NotImplementedError()
    }

    override fun queryByPaging(): PagingSource<Int, Notify> {
        throw NotImplementedError()
    }

    override fun queryByNotifyId(notifyId: Int?, packageName: String): PagingSource<Int, Notify> {
        throw NotImplementedError()
    }

    override suspend fun markDeletedByGroup(notifyId: Int, packageName: String) {
        throw NotImplementedError()
    }

    override suspend fun markDeletedById(notifyId: Int) {
        throw NotImplementedError()
    }

    override fun searchByQuery(query: String): PagingSource<Int, Notify> {
        throw NotImplementedError()
    }
}

val fakeNotifies = listOf<Notify>()

class FakeAppRepository @Inject constructor() : AppRepository {
    override val apps: Flow<List<App>> = flowOf(fakeApps)

    override suspend fun add(app: App) {
        throw NotImplementedError()
    }
}

val fakeApps = listOf<App>()
