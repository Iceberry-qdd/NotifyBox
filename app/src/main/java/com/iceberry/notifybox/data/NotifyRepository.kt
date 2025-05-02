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

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room.Transaction
import com.iceberry.notifybox.data.local.database.Notify
import com.iceberry.notifybox.data.local.database.NotifyDao
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

interface NotifyRepository {
    val notifies: PagingSource<Int, Notify>
    suspend fun add(notify: Notify)
    fun queryByPaging(): PagingSource<Int, Notify>
    fun queryByNotifyId(notifyId: Int?, packageName: String): PagingSource<Int, Notify>
    suspend fun markDeletedByGroup(notifyId: Int, packageName: String)
    suspend fun markDeletedById(notifyId: Int)
    fun searchByQuery(query: String): PagingSource<Int, Notify>

    class EmptyPageSource : PagingSource<Int, Notify>() {
        override fun getRefreshKey(state: PagingState<Int, Notify>): Int? = null
        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Notify> = LoadResult.Invalid<Int, Notify>()
    }
}

class DefaultNotifyRepository @Inject constructor(
    private val notifyDao: NotifyDao
) : NotifyRepository {
    private val accessMutex = Mutex()

    override val notifies: PagingSource<Int, Notify> = notifyDao.getNotifies()

    @Transaction
    override suspend fun add(notify: Notify) = accessMutex.withLock {
        notifyDao.insertNotify(
            notify
        )
    }

    override fun queryByPaging(): PagingSource<Int, Notify> = notifyDao.getNotifies()

    override fun queryByNotifyId(notifyId: Int?, packageName: String): PagingSource<Int, Notify> {
        return if (notifyId == null) NotifyRepository.EmptyPageSource() else notifyDao.getByNotifyId(notifyId, packageName)
    }

    override suspend fun markDeletedByGroup(notifyId: Int, packageName: String) = accessMutex.withLock {
        notifyDao.markDeletedByGroup(notifyId, packageName)
    }

    override suspend fun markDeletedById(id: Int) = accessMutex.withLock {
        notifyDao.markDeletedById(id)
    }

    override fun searchByQuery(query: String): PagingSource<Int, Notify> {
        Log.i("NotifyRepository", "call searchByQuery")
        return if (query.isEmpty()) NotifyRepository.EmptyPageSource() else notifyDao.searchByQuery(query)
    }
}
