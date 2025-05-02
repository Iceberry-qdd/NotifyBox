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

@file:OptIn(ExperimentalMaterial3Api::class)

package com.iceberry.notifybox.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.room.FtsOptions
import com.iceberry.notifybox.navigation.ForwardRoute
import com.iceberry.notifybox.navigation.ListRoute
import com.iceberry.notifybox.navigation.SettingRoute
import com.iceberry.notifybox.ui.forward.ForwardTopAppBar
import com.iceberry.notifybox.ui.list.ListTopAppBar
import com.iceberry.notifybox.ui.setting.SettingTopAppBar

@Composable
internal fun TopAppBarDropdownMenu(
    modifier: Modifier = Modifier,
    iconContent: @Composable () -> Unit,
    content: @Composable ColumnScope.(() -> Unit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        IconButton(onClick = { expanded = !expanded }) {
            iconContent()
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = modifier.wrapContentSize(Alignment.TopEnd)
        ) {
            content { expanded = !expanded }
        }
    }
}

@Composable
fun NBTopAppBar(
    currentDestination: NavDestination?,
    routeToStatistic: () -> Unit,
    orderBy: (FtsOptions.Order, String) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets
) {
    when {
        currentDestination?.hasRoute(ListRoute::class) == true -> {
            ListTopAppBar(
                routeToStatistic = routeToStatistic,
                orderBy = orderBy,
                scrollBehavior = scrollBehavior,
                windowInsets = windowInsets
            )
        }

        currentDestination?.hasRoute(ForwardRoute::class) == true -> {
            ForwardTopAppBar(windowInsets = windowInsets)
        }

        currentDestination?.hasRoute(SettingRoute::class) == true -> {
            SettingTopAppBar(windowInsets = windowInsets)
        }

//        currentDestination?.hasRoute(GroupDetailsRoute::class) == true -> {
//            GroupDetailsTopAppBar(
//                onBack = onBack,
//                navController = navController,
//                scrollBehavior = scrollBehavior,
//                windowInsets = windowInsets
//            )
//        }
    }
}







