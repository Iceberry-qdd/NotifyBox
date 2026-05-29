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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.iceberry.notifybox.ui.NBAppState
import com.iceberry.notifybox.ui.forward.ForwardScreen
import com.iceberry.notifybox.ui.groupDetails.GroupDetailsScreen
import com.iceberry.notifybox.ui.list.ListScreen
import com.iceberry.notifybox.ui.list.ListViewModel
import com.iceberry.notifybox.ui.list.NotifyVO
import com.iceberry.notifybox.ui.log.LogScreen
import com.iceberry.notifybox.ui.setting.NavigateData
import com.iceberry.notifybox.ui.setting.SettingOptions
import com.iceberry.notifybox.ui.setting.SettingScreen
import com.iceberry.notifybox.ui.setting.about.AboutScreen
import com.iceberry.notifybox.ui.setting.export.ExportScreen
import com.iceberry.notifybox.ui.setting.imports.ImportsScreen
import com.iceberry.notifybox.ui.statistic.StatisticScreen
import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    object ListRoute : Route

    @Serializable
    object ForwardRoute : Route

    @Serializable
    object SettingRoute : Route

    @Serializable
    data class GroupDetailsRoute(val notifyId: Int? = null, val packageName: String) : Route

    @Serializable
    object StatisticRoute : Route

    @Serializable
    object AboutRoute : Route

    @Serializable
    object LogRoute : Route

    @Serializable
    object ExportRoute : Route

    @Serializable
    data class ImportsRoute(val mode: SettingOptions.ImportMethod) : Route

    @Serializable
    object NopRoute : Route
}

fun NavController.navigateToList(navOptions: NavOptions) = navigate(route = Route.ListRoute, navOptions)

fun NavGraphBuilder.listScreen(
    appState: NBAppState,
    viewModel: ListViewModel,
    onItemClick: (NotifyVO) -> Unit
) {
    composable<Route.ListRoute> {
        ListScreen(
            modifier = Modifier.fillMaxSize(),
            appState = appState,
            viewModel = viewModel,
            onClickItem = onItemClick
        )
    }
}

fun NavController.navigateToForward(navOptions: NavOptions) = navigate(route = Route.ForwardRoute, navOptions)

fun NavGraphBuilder.forwardScreen(
    appState: NBAppState
) {
    composable<Route.ForwardRoute> {
        ForwardScreen(
            modifier = Modifier.fillMaxSize(),
            appState = appState
        )
    }
}

fun NavController.navigateToSettings(navOptions: NavOptions) = navigate(route = Route.SettingRoute, navOptions)

fun NavGraphBuilder.settingsScreen(
    appState: NBAppState,
    onItemClick: (NavigateData) -> Unit
) {
    composable<Route.SettingRoute> {
        SettingScreen(
            modifier = Modifier.fillMaxSize(),
            onClickItem = onItemClick,
            appState = appState
        )
    }
}

fun NavController.navigateToGroupDetails(
    initialNotify: NotifyVO? = null,
    navOptions: NavOptions? = null
) = navigate(route = Route.GroupDetailsRoute(initialNotify?.notifyId, initialNotify?.appPackageName ?: ""), navOptions)

fun NavGraphBuilder.groupDetailsScreen(
    appState: NBAppState,
    onBackClick: () -> Unit
) {
    composable<Route.GroupDetailsRoute> { backStackEntry ->
        val route: Route.GroupDetailsRoute = backStackEntry.toRoute()
        GroupDetailsScreen(
            modifier = Modifier.fillMaxSize(),
            appState = appState,
            notifyId = route.notifyId,
            packageName = route.packageName,
            onBackClick = onBackClick
        )
    }
}

fun NavController.navigateToStatistic(navOptions: NavOptions? = null) = navigate(route = Route.StatisticRoute, navOptions)

fun NavGraphBuilder.statisticScreen(
    onBackClick: () -> Unit,
    onNavToDetails: (NotifyVO) -> Unit
) {
    composable<Route.StatisticRoute> { backStackEntry ->
        StatisticScreen(
            modifier = Modifier.fillMaxSize(),
            onBackClick = onBackClick,
            onNavToDetails = onNavToDetails
        )
    }
}

fun NavGraphBuilder.aboutScreen(
    onBackClick: () -> Unit,
) {
    composable<Route.AboutRoute> { backStackEntry ->
        AboutScreen(
            modifier = Modifier.fillMaxSize(),
            onBackClick = onBackClick
        )
    }
}

fun NavGraphBuilder.logScreen(
    onBackClick: () -> Unit,
) {
    composable<Route.LogRoute> { backStackEntry ->
        LogScreen(
            modifier = Modifier.fillMaxSize(),
            onBackClick = onBackClick
        )
    }
}

fun NavGraphBuilder.exportScreen(
    onBackClick: () -> Unit,
) {
    composable<Route.ExportRoute> { backStackEntry ->
        ExportScreen(
            modifier = Modifier.fillMaxSize(),
            onBackClick = onBackClick
        )
    }
}

fun NavGraphBuilder.importsScreen(
    onBackClick: () -> Unit
) {
    composable<Route.ImportsRoute> { backStackEntry ->
        val route: Route.ImportsRoute = backStackEntry.toRoute()
        ImportsScreen(
            modifier = Modifier.fillMaxSize(),
            onBackClick = onBackClick,
            mode = route.mode
        )
    }
}