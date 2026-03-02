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

val Icons.Filled.MoveItem: ImageVector
    get() {
        if (_moveItem != null) {
            return _moveItem!!
        }
        _moveItem = Builder(
            name = "Filled.MoveItem",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 960.0f,
            viewportHeight = 960.0f,
            autoMirror = true
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
                moveTo(806.0f, 520.0f)
                lineTo(320.0f, 520.0f)
                verticalLineToRelative(-80.0f)
                horizontalLineToRelative(486.0f)
                lineToRelative(-62.0f, -62.0f)
                lineToRelative(56.0f, -58.0f)
                lineToRelative(160.0f, 160.0f)
                lineToRelative(-160.0f, 160.0f)
                lineToRelative(-56.0f, -58.0f)
                lineToRelative(62.0f, -62.0f)
                close()
                moveTo(600.0f, 360.0f)
                verticalLineToRelative(-160.0f)
                lineTo(200.0f, 200.0f)
                verticalLineToRelative(560.0f)
                horizontalLineToRelative(400.0f)
                verticalLineToRelative(-160.0f)
                horizontalLineToRelative(80.0f)
                verticalLineToRelative(160.0f)
                quadToRelative(0.0f, 33.0f, -23.5f, 56.5f)
                reflectiveQuadTo(600.0f, 840.0f)
                lineTo(200.0f, 840.0f)
                quadToRelative(-33.0f, 0.0f, -56.5f, -23.5f)
                reflectiveQuadTo(120.0f, 760.0f)
                verticalLineToRelative(-560.0f)
                quadToRelative(0.0f, -33.0f, 23.5f, -56.5f)
                reflectiveQuadTo(200.0f, 120.0f)
                horizontalLineToRelative(400.0f)
                quadToRelative(33.0f, 0.0f, 56.5f, 23.5f)
                reflectiveQuadTo(680.0f, 200.0f)
                verticalLineToRelative(160.0f)
                horizontalLineToRelative(-80.0f)
                close()
            }
        }
            .build()
        return _moveItem!!
    }

private var _moveItem: ImageVector? = null

@Preview
@Composable
private fun Preview(): Unit {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Icons.Filled.MoveItem, contentDescription = "")
    }
}
