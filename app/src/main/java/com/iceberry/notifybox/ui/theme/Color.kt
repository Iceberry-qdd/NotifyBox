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

package com.iceberry.notifybox.ui.theme

import androidx.compose.ui.graphics.Color
import com.github.mikephil.charting.utils.ColorTemplate

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Red80 = Color(0xFFEF4444)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val Pink500 = Color(0xFFE91E63)
val Purple500 = Color(0xFF9C27B0)
val Lime500 = Color(0xFFCDDC39)
val LimeA200 = Color(0xFFEEFF41)
val Orange500 = Color(0xFFFF9800)
val CyanA200 = Color(0xFF18FFFF)
val Teal500 = Color(0xFF009688)
val Green500 = Color(0xFF34A853)

val COLOR_PALETTE = ColorTemplate.MATERIAL_COLORS.plus(
    intArrayOf(
        convertToRGB(Orange500),
        convertToRGB(Purple40),
        convertToRGB(Lime500),
        convertToRGB(Pink80)
    )
).toList()

fun convertToRGB(color: Color): Int {
    return android.graphics.Color.rgb(color.red, color.green, color.blue)
}