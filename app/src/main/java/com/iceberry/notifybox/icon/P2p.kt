/*
 * Copyright (c) 2026 Iceberry-qdd.
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

package com.iceberry.notifybox.icon

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val Icons.Filled.P2p: ImageVector
    get() {
        if (_p2p != null) {
            return _p2p!!
        }
        _p2p = Builder(
            name = "Filled.P2p",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 960.0f,
            viewportHeight = 960.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF5f6368)),
                stroke = null,
                strokeLineWidth = 0.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(120.0f, 720.0f)
                verticalLineToRelative(-560.0f)
                verticalLineToRelative(560.0f)
                close()
                moveTo(840.0f, 800.0f)
                verticalLineToRelative(-560.0f)
                verticalLineToRelative(560.0f)
                close()
                moveTo(291.5f, 508.5f)
                quadTo(280.0f, 497.0f, 280.0f, 480.0f)
                reflectiveQuadToRelative(11.5f, -28.5f)
                quadTo(303.0f, 440.0f, 320.0f, 440.0f)
                reflectiveQuadToRelative(28.5f, 11.5f)
                quadTo(360.0f, 463.0f, 360.0f, 480.0f)
                reflectiveQuadToRelative(-11.5f, 28.5f)
                quadTo(337.0f, 520.0f, 320.0f, 520.0f)
                reflectiveQuadToRelative(-28.5f, -11.5f)
                close()
                moveTo(451.5f, 508.5f)
                quadTo(440.0f, 497.0f, 440.0f, 480.0f)
                reflectiveQuadToRelative(11.5f, -28.5f)
                quadTo(463.0f, 440.0f, 480.0f, 440.0f)
                reflectiveQuadToRelative(28.5f, 11.5f)
                quadTo(520.0f, 463.0f, 520.0f, 480.0f)
                reflectiveQuadToRelative(-11.5f, 28.5f)
                quadTo(497.0f, 520.0f, 480.0f, 520.0f)
                reflectiveQuadToRelative(-28.5f, -11.5f)
                close()
                moveTo(611.5f, 508.5f)
                quadTo(600.0f, 497.0f, 600.0f, 480.0f)
                reflectiveQuadToRelative(11.5f, -28.5f)
                quadTo(623.0f, 440.0f, 640.0f, 440.0f)
                reflectiveQuadToRelative(28.5f, 11.5f)
                quadTo(680.0f, 463.0f, 680.0f, 480.0f)
                reflectiveQuadToRelative(-11.5f, 28.5f)
                quadTo(657.0f, 520.0f, 640.0f, 520.0f)
                reflectiveQuadToRelative(-28.5f, -11.5f)
                close()
                moveTo(600.0f, 880.0f)
                quadToRelative(-33.0f, 0.0f, -56.5f, -23.5f)
                reflectiveQuadTo(520.0f, 800.0f)
                verticalLineToRelative(-200.0f)
                horizontalLineToRelative(80.0f)
                verticalLineToRelative(200.0f)
                horizontalLineToRelative(240.0f)
                verticalLineToRelative(-560.0f)
                lineTo(600.0f, 240.0f)
                verticalLineToRelative(120.0f)
                horizontalLineToRelative(-80.0f)
                verticalLineToRelative(-120.0f)
                quadToRelative(0.0f, -33.0f, 23.5f, -56.5f)
                reflectiveQuadTo(600.0f, 160.0f)
                horizontalLineToRelative(240.0f)
                quadToRelative(33.0f, 0.0f, 56.5f, 23.5f)
                reflectiveQuadTo(920.0f, 240.0f)
                verticalLineToRelative(560.0f)
                quadToRelative(0.0f, 33.0f, -23.5f, 56.5f)
                reflectiveQuadTo(840.0f, 880.0f)
                lineTo(600.0f, 880.0f)
                close()
                moveTo(120.0f, 800.0f)
                quadToRelative(-33.0f, 0.0f, -56.5f, -23.5f)
                reflectiveQuadTo(40.0f, 720.0f)
                verticalLineToRelative(-560.0f)
                quadToRelative(0.0f, -33.0f, 23.5f, -56.5f)
                reflectiveQuadTo(120.0f, 80.0f)
                horizontalLineToRelative(240.0f)
                quadToRelative(33.0f, 0.0f, 56.5f, 23.5f)
                reflectiveQuadTo(440.0f, 160.0f)
                verticalLineToRelative(200.0f)
                horizontalLineToRelative(-80.0f)
                verticalLineToRelative(-200.0f)
                lineTo(120.0f, 160.0f)
                verticalLineToRelative(560.0f)
                horizontalLineToRelative(240.0f)
                verticalLineToRelative(-120.0f)
                horizontalLineToRelative(80.0f)
                verticalLineToRelative(120.0f)
                quadToRelative(0.0f, 33.0f, -23.5f, 56.5f)
                reflectiveQuadTo(360.0f, 800.0f)
                lineTo(120.0f, 800.0f)
                close()
                moveTo(741.5f, 331.5f)
                quadTo(750.0f, 323.0f, 750.0f, 310.0f)
                reflectiveQuadToRelative(-8.5f, -21.5f)
                quadTo(733.0f, 280.0f, 720.0f, 280.0f)
                reflectiveQuadToRelative(-21.5f, 8.5f)
                quadTo(690.0f, 297.0f, 690.0f, 310.0f)
                reflectiveQuadToRelative(8.5f, 21.5f)
                quadTo(707.0f, 340.0f, 720.0f, 340.0f)
                reflectiveQuadToRelative(21.5f, -8.5f)
                close()
                moveTo(261.5f, 251.5f)
                quadTo(270.0f, 243.0f, 270.0f, 230.0f)
                reflectiveQuadToRelative(-8.5f, -21.5f)
                quadTo(253.0f, 200.0f, 240.0f, 200.0f)
                reflectiveQuadToRelative(-21.5f, 8.5f)
                quadTo(210.0f, 217.0f, 210.0f, 230.0f)
                reflectiveQuadToRelative(8.5f, 21.5f)
                quadTo(227.0f, 260.0f, 240.0f, 260.0f)
                reflectiveQuadToRelative(21.5f, -8.5f)
                close()
            }
        }
            .build()
        return _p2p!!
    }

private var _p2p: ImageVector? = null

@Preview
@Composable
private fun Preview(): Unit {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Icons.Filled.P2p, contentDescription = "")
    }
}
