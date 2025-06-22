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

package com.iceberry.notifybox.ui.statistic.chart.renderer

import android.graphics.Canvas
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

class XAxisRendererBarChart(
    private val mChart: BarDataProvider,
    viewPortHandler: ViewPortHandler,
    xAxis: XAxis,
    trans: Transformer = Transformer(viewPortHandler)
) : XAxisRenderer(viewPortHandler, xAxis, trans) {
    protected override fun drawLabels(c: Canvas, pos: Float, anchor: MPPointF) {
        val labelRotationAngleDegrees = mXAxis.labelRotationAngle
        val centeringEnabled = mXAxis.isCenterAxisLabelsEnabled

        val positions = FloatArray(mXAxis.mEntryCount * 2)

        for (i in 0 until positions.size step 2) {
            // only fill x values
            if (centeringEnabled) {
                positions[i] = mXAxis.mCenteredEntries[i / 2]
            } else {
                positions[i] = mXAxis.mEntries[i / 2]
            }
        }

        mTrans.pointValuesToPixel(positions)

        for (i in 0 until positions.size step 2) {
            var x = positions[i]

            if (mViewPortHandler.isInBoundsX(x)) {
                val barEntry = mChart.barData.getDataSetByIndex(0).getEntryForIndex(i / 2)
                val label = mXAxis.getValueFormatter().getBarStackedLabel(mXAxis.mEntries[i / 2], barEntry)

                if (mXAxis.isAvoidFirstLastClippingEnabled) {
                    // avoid clipping of the last

                    if (i / 2 == mXAxis.mEntryCount - 1 && mXAxis.mEntryCount > 1) {
                        val width = Utils.calcTextWidth(mAxisLabelPaint, label).toFloat()

                        if (width > mViewPortHandler.offsetRight() * 2
                            && x + width > mViewPortHandler.chartWidth
                        ) x -= width / 2

                        // avoid clipping of the first
                    } else if (i == 0) {
                        val width = Utils.calcTextWidth(mAxisLabelPaint, label).toFloat()
                        x += width / 2
                    }
                }

                drawLabel(c, label, x, pos, anchor, labelRotationAngleDegrees)
            }
        }
    }
}