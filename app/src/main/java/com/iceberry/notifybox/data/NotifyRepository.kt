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
import androidx.sqlite.db.SimpleSQLiteQuery
import com.iceberry.notifybox.data.local.database.BarChartData
import com.iceberry.notifybox.data.local.database.Notify
import com.iceberry.notifybox.data.local.database.NotifyDao
import com.iceberry.notifybox.data.local.database.PieChartData
import com.iceberry.notifybox.ui.statistic.chart.TimeRangeFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

interface NotifyRepository {
    val notifies: PagingSource<Int, Notify>
    suspend fun add(notify: Notify)
    fun queryByPaging(): PagingSource<Int, Notify>
    fun queryByPaging(
        packageName: String,
        timeRangeFilter: TimeRangeFilter = TimeRangeFilter.ALL
    ): PagingSource<Int, Notify>

    fun queryDetailByNotifyId(notifyId: Int?, packageName: String): PagingSource<Int, Notify>
    suspend fun markDeletedByGroup(notifyId: Int, packageName: String)
    suspend fun markDeletedById(notifyId: Int)
    fun searchByQuery(query: String): PagingSource<Int, Notify>
    fun statisticForPieChart(timeRangeFilter: TimeRangeFilter = TimeRangeFilter.ALL): Flow<List<PieChartData>>
    fun statisticForBarChart(timeRangeFilter: TimeRangeFilter = TimeRangeFilter.ALL): Flow<List<BarChartData>>

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

    override fun queryByPaging(
        packageName: String,
        timeRangeFilter: TimeRangeFilter
    ): PagingSource<Int, Notify> {
        val (first, second) = timeRangeFilter.range()
        return notifyDao.getNotifies(packageName, first, second)
    }

    override fun queryDetailByNotifyId(notifyId: Int?, packageName: String): PagingSource<Int, Notify> {
        return if (notifyId == null) NotifyRepository.EmptyPageSource() else notifyDao.getDetailByNotifyId(
            notifyId,
            packageName
        )
    }

    override suspend fun markDeletedByGroup(notifyId: Int, packageName: String) = accessMutex.withLock {
        notifyDao.markDeletedByGroup(notifyId, packageName)
    }

    override suspend fun markDeletedById(notifyId: Int) = accessMutex.withLock {
        notifyDao.markDeletedById(notifyId)
    }

    override fun searchByQuery(query: String): PagingSource<Int, Notify> {
        Log.i("NotifyRepository", "call searchByQuery")
        return if (query.isEmpty()) NotifyRepository.EmptyPageSource() else notifyDao.searchByQuery(query)
    }

    override fun statisticForPieChart(timeRangeFilter: TimeRangeFilter): Flow<List<PieChartData>> {
        val (first, second) = timeRangeFilter.range()
        return notifyDao.statisticForPieChart(first, second)
    }

    override fun statisticForBarChart(timeRangeFilter: TimeRangeFilter): Flow<List<BarChartData>> {
        val groupBy = timeRangeFilter.dimension.groupBy.joinToString(separator = ",") {
            "STRFTIME('$it', created_time / 1000, 'unixepoch', 'localtime')"
        }
        val zoneOffset = ZoneId.systemDefault().rules.getOffset(Instant.now())
        val (first, second) = timeRangeFilter.range()
        val startAt = first.toInstant(zoneOffset).toEpochMilli()
        val endAt = second.toInstant(zoneOffset).toEpochMilli()

        val sqlTemplate =
            "SELECT COUNT(*) AS y, created_time AS x FROM tb_notify WHERE deleted_time IS NULL AND created_time BETWEEN :startAt AND :endAt GROUP BY $groupBy ORDER BY created_time ASC"
        val query = SimpleSQLiteQuery(
            sqlTemplate,
            arrayOf<Any>(startAt, endAt)
        )
        return notifyDao.statisticForBarChart(query)
    }
}
