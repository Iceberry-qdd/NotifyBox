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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iceberry.notifybox.data.NotifyRepository
import com.iceberry.notifybox.ui.statistic.chart.BarChartUiState
import com.iceberry.notifybox.ui.statistic.chart.PieChartUiState
import com.iceberry.notifybox.ui.statistic.chart.TimeRangeFilter
import com.iceberry.notifybox.util.Async
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class StatisticViewModel @Inject constructor(
    notifyRepository: NotifyRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    private val _timeRangeFilter = MutableStateFlow(TimeRangeFilter.ALL)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _pieChartDataAsync = _timeRangeFilter
        .flatMapLatest { filter ->
            notifyRepository.statisticForPieChart(filter)
                .map { handleChartData(it) }
                .catch { e -> emit(Async.Error(e)) }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _barChartDataAsync = _timeRangeFilter
        .flatMapLatest { filter ->
            notifyRepository.statisticForBarChart(filter)
                .map { handleChartData(it) }
                .catch { e -> emit(Async.Error(e)) }
        }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val uiState: StateFlow<StatisticUiState> = combine(
        _pieChartDataAsync, _barChartDataAsync, _isLoading
    ) { pieChartDataAsync, barChartDataAsync, isLoading ->
        StatisticUiState(
            isLoading = pieChartDataAsync is Async.Loading || barChartDataAsync is Async.Loading || isLoading,
            selectedFilter = _timeRangeFilter.value,
            pieChartUiState = when (pieChartDataAsync) {
                Async.Loading -> PieChartUiState()
                is Async.Error -> PieChartUiState(isSuccess = false)
                is Async.Success -> PieChartUiState(
                    pieChartData = pieChartDataAsync.data.map { it.toVO(context) },
                )
            },
            barChartUiState = when (barChartDataAsync) {
                Async.Loading -> BarChartUiState()
                is Async.Error -> BarChartUiState(isSuccess = false)
                is Async.Success -> BarChartUiState(
                    barChartData = barChartDataAsync.data.mapIndexed { index, it -> it.toVO(index + 1) }
                )
            }
        )
    }.debounce(150)
        .onEach {
            delay(500)
            _isLoading.update { false }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = StatisticUiState()
        )

    fun toggleFilter(filter: TimeRangeFilter) = _timeRangeFilter.update { filter }

    private suspend fun <T> handleChartData(chartDataList: List<T>): Async<List<T>> {
        if (chartDataList.isEmpty()) {
            return Async.Error(Throwable("暂无统计数据"))
        }

        Async.Loading

        return Async.Success(chartDataList)
    }
}

data class StatisticUiState(
    val isLoading: Boolean = true,
    val selectedFilter: TimeRangeFilter = TimeRangeFilter.ALL,
    val pieChartUiState: PieChartUiState = PieChartUiState(),
    val barChartUiState: BarChartUiState = BarChartUiState()
)