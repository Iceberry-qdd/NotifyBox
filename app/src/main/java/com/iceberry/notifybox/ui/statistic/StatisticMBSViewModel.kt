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

package com.iceberry.notifybox.ui.statistic

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.iceberry.notifybox.data.NotifyRepository
import com.iceberry.notifybox.ui.list.KeyPrefix
import com.iceberry.notifybox.ui.list.KeySuffix
import com.iceberry.notifybox.ui.list.mappingNotifies
import com.iceberry.notifybox.ui.statistic.chart.TimeRangeFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class MBSViewModel @Inject constructor(
    notifyRepository: NotifyRepository,
    @param:ApplicationContext private val context: Context
) : ViewModel() {
    private val _packageName = MutableStateFlow("")
    private val _loading = MutableStateFlow(false)
    private val _timeRangeFilter = MutableStateFlow(TimeRangeFilter.ALL)
    private val pageConfig = PagingConfig(
        pageSize = 10,
        initialLoadSize = 30,
        enablePlaceholders = true,
        maxSize = 500
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val pager = combine(_packageName, _timeRangeFilter) { packageName, timeRangeFilter ->
        _loading.update { true }
        Pair(packageName, timeRangeFilter)
    }.flatMapLatest { it ->
        val (packageName, timeRangeFilter) = it
        Log.i("MBSViewModel#pager", "packageName = $packageName, timeRangeFilter = $timeRangeFilter")
        Pager(pageConfig) {
            if (packageName.isEmpty()) {
                NotifyRepository.EmptyPageSource()
            } else {
                notifyRepository.queryByPaging(packageName, timeRangeFilter)
            }
        }.flow.map { notifies ->
            notifies.map(
                mappingNotifies(
                    context,
                    KeyPrefix.GROUP,
                    KeySuffix.NOTIFY_ID
                )
            )
        }.cachedIn(viewModelScope)
    }.onEach {
        delay(1000L.milliseconds)
        _loading.update { false }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<MBSUiState> = combine(_loading, _packageName) { loading, packageName ->
        MBSUiState(
            loading = loading,
            packageName = packageName
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = MBSUiState()
    )

    fun setPagerParams(packageName: String, filter: TimeRangeFilter) {
        _packageName.update { packageName }
        _timeRangeFilter.update { filter }
    }
}

data class MBSUiState(
    val loading: Boolean = false,
    val packageName: String = ""
)