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
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.MPPointF
import com.iceberry.notifybox.data.NotifyRepository
import com.iceberry.notifybox.data.local.database.AppIcon
import com.iceberry.notifybox.data.local.database.PieChartData
import com.iceberry.notifybox.ui.statistic.chart.DEFAULT_CENTER_TEXT
import com.iceberry.notifybox.ui.theme.COLOR_PALETTE
import com.iceberry.notifybox.util.Async
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StatisticViewModel @Inject constructor(
    notifyRepository: NotifyRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    private val _pieChartDataAsync = notifyRepository.statisticByPackageName()
        .map { handlePieChartData(it) }
        .catch { emit(Async.Error(Throwable("暂无数据"))) }

    val uiState: StateFlow<StatisticUiState> = combine(
        _pieChartDataAsync, _isLoading
    ) { pieChartDataAsync, isLoading ->
        when (pieChartDataAsync) {
            Async.Loading -> {
                StatisticUiState(isLoading = true)
            }

            is Async.Error -> {
                StatisticUiState(pieChartUiState = PieChartUiState(isSuccess = false))
            }

            is Async.Success -> {
                StatisticUiState(
                    isLoading = isLoading,
                    pieChartUiState = PieChartUiState(
                        pieChartData = pieChartDataAsync.data.map { it.toVO(context) },
                    )
                )
            }
        }

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = StatisticUiState()
    )

    private fun handlePieChartData(pieChartDataList: List<PieChartData>): Async<List<PieChartData>> {
        if (pieChartDataList.isEmpty()) {
            return Async.Error(Throwable("暂无数据"))
        }

        return Async.Success(pieChartDataList)
    }

    companion object {
        /**
         * @param pieChartDataList 要求按照dest从大到小排序
         */
        fun handlePieData(pieChartDataList: List<PieChartDataVO>): PieData {
            val otherPieEntry = pieChartDataList
                .takeLastWhile { it.dest < 0.01 }
                .map { PieEntry(it.value.toFloat(), it.label) }
                .reduceOrNull { agg, e -> PieEntry(agg.value + e.value, "其它") }

            Log.i("otherPieEntry", otherPieEntry.toString())

            val pieEntries = pieChartDataList
                .dropLastWhile { it.dest < 0.01 }
                .map { PieEntry(it.value.toFloat(), it.label) }
                .toMutableList()
                .apply { otherPieEntry?.let { this.add(it) } }

            return PieData().apply {
                dataSet = PieDataSet(pieEntries, DEFAULT_CENTER_TEXT).apply {
                    setDrawIcons(false)
                    sliceSpace = 3f
                    iconsOffset = MPPointF(0f, 40f)
                    selectionShift = 8f
                    colors = COLOR_PALETTE

                    setDrawValues(true)
                    isUsingSliceColorAsValueLineColor = true
                    xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
                    yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
                    setValueTextColors(COLOR_PALETTE)
                }
            }
        }
    }
}

data class StatisticUiState(
    val isLoading: Boolean = true,
    val pieChartUiState: PieChartUiState = PieChartUiState()
)

data class PieChartUiState(
    val isSuccess: Boolean = true,
    val pieChartData: List<PieChartDataVO> = listOf(INIT_PIE_CHART_DATA),
)

val INIT_PIE_CHART_DATA = PieChartDataVO(
    value = 1,
    label = "NaN",
    dest = 1.0f,
    appIcon = AppIcon(icon = null, largeIcon = null, appDisplayName = "Unknown")
)
val INIT_PIE_DATA = StatisticViewModel.handlePieData(listOf(INIT_PIE_CHART_DATA))

data class PieChartDataVO(
    val value: Long,
    val label: String,
    val appIcon: AppIcon,
    val dest: Float
)