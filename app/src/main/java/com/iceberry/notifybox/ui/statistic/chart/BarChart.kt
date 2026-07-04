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

import android.content.Context
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
import androidx.compose.ui.res.stringResource
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
import com.iceberry.notifybox.R
import com.iceberry.notifybox.data.local.database.AppIcon
import com.iceberry.notifybox.databinding.BarChartBinding
import com.iceberry.notifybox.ui.statistic.chart.renderer.RoundedBarChartRenderer
import com.iceberry.notifybox.ui.statistic.chart.renderer.XAxisRendererBarChart
import com.iceberry.notifybox.ui.theme.NBApplicationTheme
import com.iceberry.notifybox.ui.theme.convertToRGB
import java.text.DecimalFormat
import java.time.DayOfWeek
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
    val centerTextDefault = stringResource(R.string.stat_app_stat)
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.stat_time_stat),
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
            val context = barChart.context
            barChart.xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setDrawAxisLine(false)
                granularity = 1f
                labelCount = 3
                valueFormatter = dimension.formatter(context)
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
                    }

                    override fun onNothingSelected() {}
                })

                animateY(800, Easing.EaseInOutQuad)

                this.data = data
                highlightValue(null)
            }

            barChart.data = data.apply {
                setValueFormatter(BarValueFormatter(0))
                setValueTextSize(10f)
            }

            barChart.renderer = RoundedBarChartRenderer(barChart, barChart.animator, barChart.viewPortHandler).apply {
                setRoundedPositiveDataSetRadius(45f)
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
    colors: List<Int> = ColorTemplate.MATERIAL_COLORS.toList(),
    label: String
): BarData {
    val barEntries = barChartDataList
        .map { BarEntry(it.xValue, it.yValue, it.extra) }

    return BarData().apply {
        addDataSet(
            BarDataSet(barEntries, label).apply {
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
        },
    label = ""
)

enum class TimeDimension(
    val groupBy: Array<String>,
    val formatter: (Context) -> ValueFormatter,
    val iteratorFun: (t: LocalDateTime) -> LocalDateTime
) {
    YEAR(arrayOf("%Y"), { YearAxisValueFormatter(it) }, { it.plusYears(1) }),
    MONTH(arrayOf("%Y", "%m"), { MonthAxisValueFormatter(it) }, { it.plusMonths(1) }),
    WEEK(arrayOf("%Y", "%m", "%d"), { WeekAxisValueFormatter(it) }, { it.plusDays(1) }),
    DAY(arrayOf("%Y", "%m", "%d"), { DayAxisValueFormatter(it) }, { it.plusDays(1) }),
    HOUR(arrayOf("%Y", "%m", "%d", "%H"), { HourAxisValueFormatter(it) }, { it.plusHours(1) })
}

enum class TimeRangeFilter(
    val labelRes: Int,
    val range: () -> Pair<LocalDateTime, LocalDateTime>,
    val dimension: TimeDimension
) {
    ALL(
        R.string.stat_filter_all,
        {
            Pair(
                LocalDateTime.ofInstant(Instant.EPOCH, ZoneId.systemDefault()),
                now()
            )
        },
        TimeDimension.YEAR
    ),
    TODAY(
        R.string.stat_filter_today,
        {
            Pair(
                now().with(LocalTime.MIN),
                now().with(LocalTime.MAX)
            )
        },
        TimeDimension.HOUR
    ),
    YESTERDAY(
        R.string.stat_filter_yesterday,
        {
            Pair(
                now().minusDays(1L).with(LocalTime.MAX).with(LocalTime.MIN),
                now().minusDays(1L).with(LocalTime.MAX)
            )
        },
        TimeDimension.HOUR
    ),
    THIS_WEEK(
        R.string.stat_filter_this_week,
        {
            Pair(
                now().with(LocalTime.MIN).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
                now().with(LocalTime.MAX).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
            )
        },
        TimeDimension.WEEK
    ),
    THIS_MONTH(
        R.string.stat_filter_this_month,
        {
            Pair(
                now().with(LocalTime.MIN).with(TemporalAdjusters.firstDayOfMonth()),
                now().with(LocalTime.MAX).with(TemporalAdjusters.lastDayOfMonth())
            )
        },
        TimeDimension.DAY
    ),
    LAST_MONTH(
        R.string.stat_filter_last_month,
        {
            Pair(
                now().minusMonths(1).with(LocalTime.MIN).with(TemporalAdjusters.firstDayOfMonth()),
                now().minusMonths(1).with(LocalTime.MAX).with(TemporalAdjusters.lastDayOfMonth())
            )
        },
        TimeDimension.DAY
    ),
    THIS_YEAR(
        R.string.stat_filter_this_year,
        {
            Pair(
                now().with(LocalTime.MIN).with(TemporalAdjusters.firstDayOfYear()),
                now().with(LocalTime.MAX).with(TemporalAdjusters.lastDayOfYear())
            )
        },
        TimeDimension.MONTH
    ),
    LAST_YEAR(
        R.string.stat_filter_last_year,
        {
            Pair(
                now().with(LocalTime.MIN).minusYears(1L).with(TemporalAdjusters.firstDayOfYear()),
                now().with(LocalTime.MAX).minusYears(1L).with(TemporalAdjusters.lastDayOfYear())
            )
        },
        TimeDimension.MONTH
    ),
    CUSTOM(
        R.string.stat_filter_custom,
        {
            Pair(
                LocalDateTime.ofInstant(Instant.EPOCH, ZoneId.systemDefault()),
                now()
            )
        },
        TimeDimension.YEAR
    )
}

class BarValueFormatter(digits: Int) : DefaultValueFormatter(digits) {
    override fun getFormattedValue(value: Float): String = if (value == 0f) "" else super.getFormattedValue(value)

    override fun setup(digits: Int) {
        this.mDecimalDigits = digits

        val b = StringBuffer()
        for (i in 0..<digits) {
            if (i == 0) b.append(".")
            b.append("0")
        }

        mFormat = DecimalFormat("#$b")
    }
}

class YearAxisValueFormatter(private val context: Context) : ValueFormatter() {
    /**
     * Use this as getAxisLabel()
     */
    override fun getBarStackedLabel(
        value: Float,
        barEntry: BarEntry?
    ): String? {
        val timestamp = barEntry?.data as Long?
        val yearStr = timestamp?.let {
            val year = LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault()).year
            context.getString(R.string.date_year, year)
        }
        return yearStr
    }
}

class MonthAxisValueFormatter(private val context: Context) : ValueFormatter() {
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
            context.getString(R.string.date_month, month)
        }
    }
}

class WeekAxisValueFormatter(private val context: Context) : ValueFormatter() {
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

class DayAxisValueFormatter(private val context: Context) : ValueFormatter() {
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
            context.getString(R.string.date_day, day)
        }
    }
}

class HourAxisValueFormatter(private val context: Context) : ValueFormatter() {
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