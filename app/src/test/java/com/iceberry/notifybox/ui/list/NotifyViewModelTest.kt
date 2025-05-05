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

package com.iceberry.notifybox.ui.list


import androidx.paging.PagingSource
import com.iceberry.notifybox.data.NotifyRepository
import com.iceberry.notifybox.data.local.database.Notify
import com.iceberry.notifybox.data.local.database.PieChartData
import com.iceberry.notifybox.util.Async
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@OptIn(ExperimentalCoroutinesApi::class) // TODO: Remove when stable
class NotifyViewModelTest {
    @Test
    fun uiState_initiallyLoading() = runTest {
        val viewModel = ListViewModel(
            FakeNotifyRepository(),
            settingsRepository = TODO(),
            context = TODO()
        )
        assertEquals(viewModel.uiState.first(), Async.Loading)
    }

    @Test
    fun uiState_onItemSaved_isDisplayed() = runTest {
        val viewModel = ListViewModel(
            FakeNotifyRepository(),
            settingsRepository = TODO(),
            context = TODO()
        )
        assertEquals(viewModel.uiState.first(), Async.Loading)
    }
}

private class FakeNotifyRepository : NotifyRepository {

    private val data = mutableListOf<String>()

    override val notifies: PagingSource<Int, Notify>
        get() = NotifyRepository.EmptyPageSource()

    override suspend fun add(notify: Notify) {
        TODO("Not yet implemented")
    }

    override fun queryByPaging(): PagingSource<Int, Notify> {
        TODO("Not yet implemented")
    }

    override fun queryByNotifyId(
        notifyId: Int?,
        packageName: String
    ): PagingSource<Int, Notify> {
        TODO("Not yet implemented")
    }

    override suspend fun markDeletedByGroup(notifyId: Int, packageName: String) {
        TODO("Not yet implemented")
    }

    override suspend fun markDeletedById(notifyId: Int) {
        TODO("Not yet implemented")
    }

    override fun searchByQuery(query: String): PagingSource<Int, Notify> {
        TODO("Not yet implemented")
    }

    override fun statisticByPackageName(dataTime: LocalDateTime): Flow<List<PieChartData>> {
        TODO("Not yet implemented")
    }
}
