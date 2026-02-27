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

package com.iceberry.notifybox.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.ui.graphics.vector.ImageVector
import kotlin.reflect.KClass

enum class TopLevelDestination(
    val icon: ImageVector,
    val iconText: String,
    val titleText: String,
    val route: KClass<*>
) {
    LIST(
        icon = Icons.AutoMirrored.Filled.List,
        iconText = "列表",
        titleText = "列表",
        route = Route.ListRoute::class
    ),
    FORWARD(
        icon = Icons.Filled.SwapHoriz,
        iconText = "转发",
        titleText = "转发",
        route = Route.ForwardRoute::class
    ),
    SETTINGS(
        icon = Icons.Filled.Settings,
        iconText = "设置",
        titleText = "设置",
        route = Route.SettingRoute::class
    )
}