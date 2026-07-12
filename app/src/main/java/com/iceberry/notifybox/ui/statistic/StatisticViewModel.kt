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
import com.iceberry.notifybox.data.local.database.BarChartData
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
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class StatisticViewModel @Inject constructor(
    notifyRepository: NotifyRepository,
    @param:ApplicationContext private val context: Context
) : ViewModel() {
    private val _timeRangeFilter = MutableStateFlow(TimeRangeFilter.ALL)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _pieChartDataAsync = _timeRangeFilter
        .flatMapLatest { filter ->
            notifyRepository.statisticForPieChart(filter)
                .map { if (it.isEmpty()) Async.Empty else Async.Success(it) }
                .onStart {
                    emit(Async.Loading)
                    delay(1000.milliseconds)
                }
                .catch { e -> emit(Async.Error(e)) }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _barChartDataAsync = _timeRangeFilter
        .flatMapLatest { filter ->
            notifyRepository.statisticForBarChart(filter)
                .map {
                    val barData = handleBarChartData(it, filter)
                    if (barData.isEmpty()) Async.Empty else Async.Success(barData)
                }
                .onStart {
                    emit(Async.Loading)
                    delay(1000.milliseconds)
                }
                .catch { e -> emit(Async.Error(e)) }
        }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val uiState: StateFlow<StatisticUiState> = combine(
        _pieChartDataAsync, _barChartDataAsync
    ) { pieChartDataAsync, barChartDataAsync ->
        StatisticUiState(
            isLoading = pieChartDataAsync is Async.Loading || barChartDataAsync is Async.Loading,
            selectedFilter = _timeRangeFilter.value,
            pieChartUiState = when (pieChartDataAsync) {
                Async.Loading -> PieChartUiState()
                is Async.Error -> PieChartUiState(isSuccess = false)
                is Async.Empty -> PieChartUiState(isSuccess = true, pieChartData = listOf())
                is Async.Success -> PieChartUiState(
                    pieChartData = pieChartDataAsync.data.map { it.toVO(context) },
                )

            },
            barChartUiState = when (barChartDataAsync) {
                Async.Loading -> BarChartUiState()
                is Async.Error -> BarChartUiState(isSuccess = false)
                is Async.Empty -> BarChartUiState(isSuccess = true, barChartData = listOf())
                is Async.Success -> BarChartUiState(
                    barChartData = barChartDataAsync.data.mapIndexed { index, it -> it.toVO(index + 1) }
                )
            }
        )
    }.debounce(150.milliseconds)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = StatisticUiState()
        )

    fun toggleFilter(filter: TimeRangeFilter) = _timeRangeFilter.update { filter }

    private suspend fun <T> handleChartData(chartDataList: List<T>): Async<List<T>> {
        if (chartDataList.isEmpty()) {
            return Async.Empty
        }

        Async.Loading

        return Async.Success(chartDataList)
    }

    private fun handleBarChartData(notifyDataList: List<BarChartData>, filter: TimeRangeFilter): List<BarChartData> {
        val (first, second) = filter.range()
        val zoneOffset = ZoneOffset.systemDefault().rules.getOffset(Instant.now())
        val startAt = if (filter == TimeRangeFilter.ALL) {
            LocalDateTime
                .ofInstant(
                    notifyDataList.firstOrNull()?.let { Instant.ofEpochMilli(it.x) } ?: getAppInstalledTime(),
                    zoneOffset
                )
                .with(LocalTime.MIN)
                .with(TemporalAdjusters.firstDayOfYear())
        } else {
            first
        }
        val endAt = if (filter == TimeRangeFilter.ALL) {
            second
                .with(LocalTime.MAX)
                .with(TemporalAdjusters.lastDayOfYear())
        } else {
            second
        }

        val allBarItemList = generateSequence(startAt) { cur ->
            filter.dimension.iteratorFun(cur)
        }
            .takeWhile { !it.isAfter(endAt) }
            .map { BarChartData(y = 0, x = it.toInstant(zoneOffset).toEpochMilli()) }
            .toMutableList()

        do {
            allBarItemList.add(
                BarChartData(
                    y = 0,
                    x = (allBarItemList.lastOrNull()?.x ?: 0)
                        .let { filter.dimension.iteratorFun(LocalDateTime.ofInstant(Instant.ofEpochMilli(it), zoneOffset)) }
                        .toInstant(zoneOffset)
                        .toEpochMilli()
                )
            )
        } while (allBarItemList.size < 7)

        var k = 0
        for (i in 0..<allBarItemList.size - 1) {
            val lt = Instant.ofEpochMilli(allBarItemList[i].x)
            val gt = Instant.ofEpochMilli(allBarItemList[i + 1].x)

            for (j in k..<notifyDataList.size) {
                val data = notifyDataList[j]
                val t = Instant.ofEpochMilli(data.x)
                if (t.isBefore(lt) || t.isAfter(gt)) {
                    continue
                } else {
                    allBarItemList[i] = BarChartData(x = allBarItemList[i].x, y = data.y)
                    k++
                    break
                }
            }
        }

        allBarItemList.removeLastOrNull()

        return allBarItemList
    }

    private fun getAppInstalledTime(): Instant {
        return Instant.ofEpochMilli(context.packageManager.getPackageInfo(context.packageName, 0).firstInstallTime)
    }
}

data class StatisticUiState(
    val isLoading: Boolean = true,
    val selectedFilter: TimeRangeFilter = TimeRangeFilter.ALL,
    val pieChartUiState: PieChartUiState = PieChartUiState(),
    val barChartUiState: BarChartUiState = BarChartUiState()
)
