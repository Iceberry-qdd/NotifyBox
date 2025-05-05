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

import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.iceberry.notifybox.data.local.database.AppIcon

@Composable
fun rememberStatisticItemState(
    progress: Float = 0f,
    title: String = "Unknown",
    value: String = "NaN",
    indicatorColor: Color = ProgressIndicatorDefaults.linearColor,
    appIcon: AppIcon = AppIcon()
): StatisticItemState {
    return remember(progress, title, value, indicatorColor) {
        StatisticItemState(
            progress = progress,
            title = title,
            value = value,
            indicatorColor = indicatorColor,
            appIcon = appIcon
        )
    }
}

/**
 * @param progress value range: [0.0f - 1.0f]
 */
class StatisticItemState(
    internal val progress: Float,
    internal val title: String,
    internal val value: String,
    internal val indicatorColor: Color,
    internal val appIcon: AppIcon
)