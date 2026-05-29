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

import android.graphics.Color
import android.graphics.Paint
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
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.MPPointF
import com.iceberry.notifybox.data.local.database.AppIcon
import com.iceberry.notifybox.databinding.PieChartBinding
import com.iceberry.notifybox.ui.theme.COLOR_PALETTE
import com.iceberry.notifybox.ui.theme.NBApplicationTheme
import com.iceberry.notifybox.ui.theme.convertToRGB

const val DEFAULT_CENTER_TEXT = "按应用统计"

@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    data: PieData = PieData(),
    onClickEntry: (Int) -> Unit,
    onRest: (PieChartBinding.() -> Unit)? = null,
    onRelease: PieChartBinding.() -> Unit = {},
    update: PieChartBinding.() -> Unit = {}
) {
    val descTextColor = MaterialTheme.colorScheme.outline
    val centerTextColor = MaterialTheme.colorScheme.onSurface
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = DEFAULT_CENTER_TEXT,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        AndroidViewBinding(
            modifier = Modifier,
            factory = PieChartBinding::inflate,
            onReset = onRest,
            onRelease = onRelease,
//        update = update,
        ) {
            pieChart.apply {
                dragDecelerationFrictionCoef = 0.95f
                isHighlightPerTapEnabled = true

                setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                        if (e == null) return
                        Log.i("PieChart#onValueSelected", "e: $e, h: $h")
                        h?.let {
                            val entry = e as PieEntry
                            centerText = "${entry.label} ${entry.value.toInt()}"
                            if (entry.data as Boolean == true) {
                                onClickEntry(it.x.toInt())
                            }
                        }
                    }

                    override fun onNothingSelected() {
                        Log.i("PieChart#onNothingSelected", "nothing selected")
                        centerText = DEFAULT_CENTER_TEXT
                    }
                })

                animateY(800, Easing.EaseInOutQuad)

                this.data = data
                highlightValue(null)

                legend.isEnabled = false

                centerText = DEFAULT_CENTER_TEXT
                setDrawCenterText(true)
                setCenterTextColor(convertToRGB(centerTextColor))
                setCenterTextSize(11f)
                setHoleColor(Color.TRANSPARENT)

                setDrawEntryLabels(false)
                setUsePercentValues(true)
            }

            pieChart.description.apply {
                isEnabled = true
                text = "*其它为小于1%的数据"
                textAlign = Paint.Align.RIGHT
                textSize = 10f
                textColor = convertToRGB(descTextColor)
                xOffset = 10f
                yOffset = 10f
            }

            pieChart.data = data.apply {
                setValueFormatter(NBPercentFormatter(pieChart))
                setValueTextSize(10f)
            }

            pieChart.invalidate()
        }
    }
}

class NBPercentFormatter(private val pieChart: PieChart) : PercentFormatter(pieChart) {
    fun getFormattedValue(value: Float, pieEntry: PieEntry?): String? {
        val formattedValue = super.getFormattedValue(value)
        return if (pieEntry == null) {
            formattedValue
        } else {
            "${pieEntry.label} $formattedValue"
        }
    }

    override fun getPieLabel(value: Float, pieEntry: PieEntry?): String? {
        return if (pieChart.isUsePercentValuesEnabled) {
            // Converted to percent
            getFormattedValue(value, pieEntry)
        } else {
            // raw value, skip percent sign
            mFormat.format(value.toDouble())
        }
    }
}

data class PieChartUiState(
    val isSuccess: Boolean = true,
    val pieChartData: List<PieChartDataVO> = listOf(INIT_PIE_CHART_DATA),
)

data class PieChartDataVO(
    val value: Float = 0f,
    val label: String? = "",
    val appIcon: AppIcon = AppIcon(),
    val dest: Float = 0f,
    val packageName: String = ""
)

/**
 * @param pieChartDataList 要求按照dest从大到小排序
 */
internal fun handlePieData(pieChartDataList: List<PieChartDataVO>): PieData {
    val otherPieEntry = pieChartDataList
        .takeLastWhile { it.dest < 0.01 }
        .map { PieEntry(it.value, it.label) }
        .reduceOrNull { agg, e -> PieEntry(agg.value + e.value, "其它", false) }

    val pieEntries = pieChartDataList
        .dropLastWhile { it.dest < 0.01 }
        .map { PieEntry(it.value, it.label, true) }
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

val INIT_PIE_CHART_DATA = PieChartDataVO(
    value = 1f,
    label = "NaN",
    dest = 1.0f,
    appIcon = AppIcon(icon = null, largeIcon = null, appDisplayName = "Unknown"),
    packageName = ""
)
val INIT_PIE_DATA = handlePieData(listOf(INIT_PIE_CHART_DATA))


@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun PieChartPreview() {
    NBApplicationTheme {
        PieChart(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .height(250.dp)
                .background(MaterialTheme.colorScheme.surface),
            data = INIT_PIE_DATA,
            onClickEntry = {},
            onRest = {},
            onRelease = {},
            update = {}
        )
    }
}