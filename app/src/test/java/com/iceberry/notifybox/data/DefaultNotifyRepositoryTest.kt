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

import androidx.core.app.NotificationCompat
import androidx.paging.PagingSource
import androidx.sqlite.db.SupportSQLiteQuery
import com.iceberry.notifybox.data.local.database.BarChartData
import com.iceberry.notifybox.data.local.database.Notify
import com.iceberry.notifybox.data.local.database.NotifyDao
import com.iceberry.notifybox.data.local.database.PieChartData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.LocalDateTime

/**
 * Unit tests for [DefaultNotifyRepository].
 */
@OptIn(ExperimentalCoroutinesApi::class) // TODO: Remove when stable
class DefaultNotifyRepositoryTest {

    @Test
    fun mains_newItemSaved_itemIsReturned() = runTest {
        val repository = DefaultNotifyRepository(FakeNotifyDao())

        repository.add(
            Notify(
                title = null,
                content = null,
                packageName = "",
                createdTime = LocalDateTime.now(),
                smallIcon = null,
                largeIcon = null,
                notifyId = 1,
                channel = "test",
                priority = NotificationCompat.PRIORITY_DEFAULT,
                deletedTime = LocalDateTime.now(),
                count = 1
            )
        )
    }
}

private class FakeNotifyDao : NotifyDao {

    private val data = mutableListOf<Notify>()
    override fun getNotifies(): PagingSource<Int, Notify> {
        TODO("Not yet implemented")
    }

    override fun getNotifies(
        packageName: String,
        startAt: LocalDateTime,
        endAt: LocalDateTime
    ): PagingSource<Int, Notify> {
        TODO("Not yet implemented")
    }

    override fun getDetailByNotifyId(notifyId: Int, packageName: String): PagingSource<Int, Notify> {
        TODO("Not yet implemented")
    }

    override suspend fun insertNotify(item: Notify) {
        data.add(0, item)
    }

    override suspend fun deleteByGroup(notifyId: Int, packageName: String) {
        TODO("Not yet implemented")
    }

    override suspend fun markDeletedByGroup(notifyId: Int, packageName: String) {
        TODO("Not yet implemented")
    }

    override suspend fun undoDeleteByGroup(notifyId: Int, packageName: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteById(id: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun undoDeleteById(id: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun markDeletedById(id: Int) {
        TODO("Not yet implemented")
    }

    override fun searchByQuery(query: String): PagingSource<Int, Notify> {
        TODO("Not yet implemented")
    }

    override fun statisticForPieChart(
        startAt: LocalDateTime,
        endAt: LocalDateTime
    ): Flow<List<PieChartData>> {
        TODO("Not yet implemented")
    }

    override fun statisticForBarChart(query: SupportSQLiteQuery): Flow<List<BarChartData>> {
        TODO("Not yet implemented")
    }
}
