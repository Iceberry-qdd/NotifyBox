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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import com.iceberry.notifybox.ui.NBAppState
import com.iceberry.notifybox.ui.list.ListViewModel


@Composable
fun NBNavHost(
    modifier: Modifier = Modifier,
    appState: NBAppState,
    listViewModel: ListViewModel = hiltViewModel() // XXX 此处获取ListScreen的viewmodel是为了保证即时搜索功能可用
) {
    val navController = appState.navController
    val isTopLevelDestination = appState.isTopLevelDestination

    NavHost(
        navController = navController,
        startDestination = Route.ListRoute,
        modifier = modifier,
        enterTransition = { slideInHorizontally(animationSpec = spring()) { fullWidth -> fullWidth * if (isTopLevelDestination) -1 else 1 } },
        exitTransition = { slideOutHorizontally(animationSpec = spring()) { fullWidth -> fullWidth * if (isTopLevelDestination) -1 else 1 } }
    ) {
        listScreen(
            appState = appState,
            viewModel = listViewModel,
            onItemClick = navController::navigateToGroupDetails
        )

        forwardScreen()

        settingsScreen(
            appState = appState,
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

    }
}