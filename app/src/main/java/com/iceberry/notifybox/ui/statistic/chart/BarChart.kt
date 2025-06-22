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

package com.iceberry.notifybox.ui.statistic.chart

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.iceberry.notifybox.data.local.database.AppIcon
import com.iceberry.notifybox.databinding.BarChartBinding
import com.iceberry.notifybox.ui.statistic.chart.renderer.RoundedBarChartRenderer
import com.iceberry.notifybox.ui.statistic.chart.renderer.XAxisRendererBarChart
import com.iceberry.notifybox.ui.theme.NBApplicationTheme
import com.iceberry.notifybox.ui.theme.convertToRGB
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import kotlin.random.Random


@Composable
fun BarChart(
    modifier: Modifier = Modifier,
    data: BarData = BarData(),
    dimension: TimeDimension = TimeDimension.YEAR,
    onRest: (BarChartBinding.() -> Unit)? = null,
    onRelease: BarChartBinding.() -> Unit = {},
    update: BarChartBinding.() -> Unit = {}
) {
    val lineTextColor = MaterialTheme.colorScheme.outline
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "按时间统计",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        AndroidViewBinding(
            modifier = Modifier,
            factory = BarChartBinding::inflate,
            onReset = onRest,
            onRelease = onRelease,
//        update = {},
        ) {
            barChart.xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setDrawAxisLine(false)
                granularity = 1f
                labelCount = 3
                valueFormatter = dimension.formatter
                textColor = convertToRGB(lineTextColor)
                axisLineColor = convertToRGB(lineTextColor)
            }

            barChart.axisLeft.apply {
                labelCount = 6
                valueFormatter = DefaultAxisValueFormatter(0)
                setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                spaceTop = 15f
                axisMinimum = 0f
                setDrawGridLines(false)
                setDrawZeroLine(false)
                setDrawAxisLine(false)
                setDrawLabels(false)

                textColor = convertToRGB(lineTextColor)
                axisLineColor = convertToRGB(lineTextColor)
            }

            barChart.apply {
                dragDecelerationFrictionCoef = 0.95f
                isHighlightPerTapEnabled = true
                description.isEnabled = false
                axisRight.isEnabled = false
                legend.isEnabled = false

                setDrawBarShadow(false)
                setDrawBorders(false)
                setDrawValueAboveBar(true)
                setMaxVisibleValueCount(60)
                setPinchZoom(false)
                setFitBars(true)

                setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                        if (e == null) return
                        Log.i("BarChart#onValueSelected", "e: $e, h: $h")
                    }

                    override fun onNothingSelected() {
                        Log.i("BarChart#onNothingSelected", "nothing selected")
                    }
                })

                animateY(800, Easing.EaseInOutQuad)

                this.data = data
                highlightValue(null)
            }

            barChart.data = data.apply {
                setValueFormatter(DefaultValueFormatter(0))
                setValueTextSize(10f)
            }

            barChart.renderer = RoundedBarChartRenderer(barChart, barChart.animator, barChart.viewPortHandler).apply {
                setRoundedPositiveDataSetRadius(15f)
                setRoundedNegativeDataSetRadius(45f)
            }
            barChart.setXAxisRenderer(XAxisRendererBarChart(barChart, barChart.viewPortHandler, barChart.xAxis))
            barChart.invalidate()
        }
    }
}

data class BarChartDataVO(
    val xValue: Float,
    val yValue: Float,
    val xLabel: String = "",
    val extra: Any? = null,
    val appIcon: AppIcon = AppIcon()
)

/**
 * @param barChartDataList 要求按照dest从大到小排序
 */
fun handleBarData(
    barChartDataList: List<BarChartDataVO>,
    colors: List<Int> = ColorTemplate.MATERIAL_COLORS.toList()
): BarData {
    val barEntries = barChartDataList
        .map { BarEntry(it.xValue, it.yValue, it.extra) }

    return BarData().apply {
        addDataSet(
            BarDataSet(barEntries, DEFAULT_CENTER_TEXT).apply {
                setDrawIcons(false)
                iconsOffset = MPPointF(0f, 40f)
                this.colors = colors
                setDrawValues(true)
                setValueTextColors(colors)
                barWidth = 0.3f
            }
        )
    }
}

data class BarChartUiState(
    val isSuccess: Boolean = true,
    val barChartData: List<BarChartDataVO> = listOf(INIT_BAR_CHART_DATA),
    val filter: TimeRangeFilter = TimeRangeFilter.ALL
)

val INIT_BAR_CHART_DATA = BarChartDataVO(
    xValue = Random.nextLong(Instant.EPOCH.toEpochMilli(), Instant.now().toEpochMilli()).toFloat(),
    yValue = Random.nextInt(1, 100).toFloat(),
    xLabel = "NaN",
    appIcon = AppIcon()
)
val INIT_BAR_DATA = handleBarData(
    listOf(2018f, 2019f, 2020f, 2021f, 2022f, 2023f, 2024f, 2025f)
        .map {
            BarChartDataVO(
                xValue = it,
                yValue = Random.nextInt(1, 100).toFloat(),
                xLabel = "NaN",
                appIcon = AppIcon()
            )
        }
)

enum class TimeDimension(val groupBy: Array<String>, val formatter: ValueFormatter) {
    YEAR(arrayOf("%Y"), YearAxisValueFormatter()),
    MONTH(arrayOf("%Y", "%m"), MonthAxisValueFormatter()),
    WEEK(arrayOf("%Y", "%m", "%d"), WeekAxisValueFormatter()),
    DAY(arrayOf("%Y", "%m", "%d"), DayAxisValueFormatter()),
    HOUR(arrayOf("%Y", "%m", "%d", "%H"), HourAxisValueFormatter())
}

enum class TimeRangeFilter(
    val label: String,
    val range: () -> Pair<LocalDateTime, LocalDateTime>,
    val dimension: TimeDimension
) {
    ALL(
        "全部",
        {
            Pair(
                LocalDateTime.ofInstant(Instant.EPOCH, ZoneId.systemDefault()),
                now()
            )
        },
        TimeDimension.DAY
    ),
    TODAY(
        "今天",
        {
            Pair(
                now().with(LocalTime.MIN),
                now()
            )
        },
        TimeDimension.HOUR
    ),
    YESTERDAY(
        "昨天",
        {
            Pair(
                now().minusDays(1L).with(LocalTime.MAX).with(LocalTime.MIN),
                now().minusDays(1L).with(LocalTime.MAX)
            )
        },
        TimeDimension.HOUR
    ),
    THIS_WEEK(
        "本周",
        {
            Pair(
                now().minusDays(1L * (now().dayOfWeek.value - 1)).with(LocalTime.MIN),
                now()
            )
        },
        TimeDimension.WEEK
    ),
    THIS_MONTH(
        "本月",
        {
            Pair(
                now().minusDays(1L * (now().dayOfMonth - 1)).with(LocalTime.MIN),
                now()
            )
        },
        TimeDimension.DAY
    ),
    LAST_MONTH(
        "上月",
        {
            Pair(
                now()
                    .minusMonths(1)
                    .with(TemporalAdjusters.firstDayOfMonth())
                    .with(LocalTime.MIN),
                now()
                    .minusMonths(1)
                    .with(TemporalAdjusters.lastDayOfMonth())
                    .with(LocalTime.MAX)
            )
        },
        TimeDimension.DAY
    ),
    THIS_YEAR(
        "今年",
        {
            Pair(
                now().minusDays(1L * (now().dayOfYear - 1)).with(LocalTime.MIN),
                now()
            )
        },
        TimeDimension.MONTH
    ),
    CUSTOM(
        "自定义",
        {
            Pair(
                LocalDateTime.ofInstant(Instant.EPOCH, ZoneId.systemDefault()),
                now()
            )
        },
        TimeDimension.YEAR
    )
}

class YearAxisValueFormatter() : ValueFormatter() {
    /**
     * Use this as getAxisLabel()
     */
    override fun getBarStackedLabel(
        value: Float,
        barEntry: BarEntry?
    ): String? {
        val timestamp = barEntry?.data as Long?
        val year = timestamp?.let {
            val year = LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault()).year
            "${year}年"
        }
        return year
    }
}

class MonthAxisValueFormatter() : ValueFormatter() {
    /**
     * Use this as getAxisLabel()
     */
    override fun getBarStackedLabel(
        value: Float,
        barEntry: BarEntry?
    ): String? {
        val timestamp = barEntry?.data as Long?
        return timestamp?.let {
            val month = LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault()).monthValue
            "${month}月"
        }
    }
}

class WeekAxisValueFormatter() : ValueFormatter() {
    /**
     * Use this as getAxisLabel()
     */
    override fun getBarStackedLabel(
        value: Float,
        barEntry: BarEntry?
    ): String? {
        val timestamp = barEntry?.data as Long?
        return timestamp?.let {
            val week = LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault()).dayOfWeek
            week.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        }
    }
}

class DayAxisValueFormatter() : ValueFormatter() {
    /**
     * Use this as getAxisLabel()
     */
    override fun getBarStackedLabel(
        value: Float,
        barEntry: BarEntry?
    ): String? {
        val timestamp = barEntry?.data as Long?
        return timestamp?.let {
            val day = LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault()).dayOfMonth
            "${day}日"
        }
    }
}

class HourAxisValueFormatter() : ValueFormatter() {
    /**
     * Use this as getAxisLabel()
     */
    override fun getBarStackedLabel(
        value: Float,
        barEntry: BarEntry?
    ): String? {
        val timestamp = barEntry?.data as Long?
        return timestamp?.let {
            val hour = LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault()).hour
            "${hour}:00"
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun BarChartPreview() {
    NBApplicationTheme {
        BarChart(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .height(150.dp)
                .background(MaterialTheme.colorScheme.surface),
            data = INIT_BAR_DATA,
            onRest = {},
            onRelease = {},
            update = {}
        )
    }
}