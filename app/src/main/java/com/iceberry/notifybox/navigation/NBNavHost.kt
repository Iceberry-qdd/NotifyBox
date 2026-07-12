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

import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import com.iceberry.notifybox.ui.NBAppState
import com.iceberry.notifybox.ui.list.ListViewModel


@Composable
fun NBNavHost(
    modifier: Modifier = Modifier,
    appState: NBAppState,
    paddingValues: androidx.compose.foundation.layout.PaddingValues,
    listViewModel: ListViewModel = hiltViewModel() // XXX 此处获取ListScreen的viewmodel是为了保证即时搜索功能可用
) {
    val navController = appState.navController

    NavHost(
        navController = navController,
        startDestination = Route.ListRoute,
        modifier = modifier,
        enterTransition = {
            val initialTopLevelDestination = TopLevelDestination.entries.firstOrNull { initialState.destination.hasRoute(it.route) }
            val targetTopLevelDestination = TopLevelDestination.entries.firstOrNull { targetState.destination.hasRoute(it.route) }

            if (initialTopLevelDestination != null && targetTopLevelDestination != null) {
                val initialIndex = initialTopLevelDestination.ordinal
                val targetIndex = targetTopLevelDestination.ordinal
                val direction = if (targetIndex > initialIndex) 1 else -1
                slideInHorizontally(animationSpec = spring()) { it * direction }
            } else {
                slideInHorizontally(animationSpec = spring()) { it }
            }
        },
        exitTransition = {
            val initialTopLevelDestination = TopLevelDestination.entries.firstOrNull { initialState.destination.hasRoute(it.route) }
            val targetTopLevelDestination = TopLevelDestination.entries.firstOrNull { targetState.destination.hasRoute(it.route) }

            if (initialTopLevelDestination != null && targetTopLevelDestination != null) {
                val initialIndex = initialTopLevelDestination.ordinal
                val targetIndex = targetTopLevelDestination.ordinal
                val direction = if (targetIndex > initialIndex) -1 else 1
                slideOutHorizontally(animationSpec = spring()) { it * direction }
            } else {
                slideOutHorizontally(animationSpec = spring()) { -it }
            }
        },
        popEnterTransition = { slideInHorizontally(animationSpec = spring()) { -it } },
        popExitTransition = { slideOutHorizontally(animationSpec = spring()) { it } }
    ) {
        listScreen(
            appState = appState,
            viewModel = listViewModel,
            paddingValues = paddingValues,
            onItemClick = navController::navigateToGroupDetails
        )

        forwardScreen(
            appState = appState,
            paddingValues = paddingValues
        )

        settingsScreen(
            appState = appState,
            paddingValues = paddingValues,
            onItemClick = { navController.navigate(route = it.route, navOptions = it.navOptions) }
        )

        groupDetailsScreen(
            appState = appState,
            onBackClick = navController::popBackStack,
        )

        statisticScreen(
            onBackClick = navController::popBackStack,
            onNavToDetails = navController::navigateToGroupDetails
        )

        aboutScreen(
            onBackClick = navController::popBackStack,
        )

        logScreen(
            onBackClick = navController::popBackStack,
        )

        exportScreen(
            onBackClick = navController::popBackStack,
        )

        importsScreen(
            onBackClick = navController::popBackStack
        )
    }
}