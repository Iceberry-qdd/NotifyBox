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
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import com.iceberry.notifybox.ui.setting.SettingScreen
import com.iceberry.notifybox.ui.statistic.StatisticScreen
import kotlinx.serialization.Serializable

@Serializable
object ListRoute

@Serializable
object ForwardRoute

@Serializable
object SettingRoute

@Serializable
data class GroupDetailsRoute(val notifyId: Int? = null, val packageName: String)

@Serializable
object StatisticRoute

fun NavController.navigateToList(navOptions: NavOptions) = navigate(route = ListRoute, navOptions)

fun NavGraphBuilder.listScreen(
    appState: NBAppState,
    viewModel: ListViewModel,
    onItemClick: (NotifyVO) -> Unit
) {
    composable<ListRoute> {
        ListScreen(
            modifier = Modifier.fillMaxSize(),
            appState = appState,
            viewModel = viewModel,
            onClickItem = onItemClick
        )
    }
}

fun NavController.navigateToForward(navOptions: NavOptions) = navigate(route = ForwardRoute, navOptions)

fun NavGraphBuilder.forwardScreen() {
    composable<ForwardRoute> {
        ForwardScreen(
            modifier = Modifier.padding(16.dp)
        )
    }
}

fun NavController.navigateToSettings(navOptions: NavOptions) = navigate(route = SettingRoute, navOptions)

fun NavGraphBuilder.settingsScreen() {
    composable<SettingRoute> {
        SettingScreen(
            modifier = Modifier.padding(16.dp)
        )
    }
}

fun NavController.navigateToGroupDetails(
    initialNotify: NotifyVO? = null,
    navOptions: NavOptions? = null
) = navigate(route = GroupDetailsRoute(initialNotify?.notifyId, initialNotify?.appPackageName ?: ""), navOptions)

fun NavGraphBuilder.groupDetailsScreen(
    appState: NBAppState,
    onBackClick: () -> Unit
) {
    composable<GroupDetailsRoute> { backStackEntry ->
        val route: GroupDetailsRoute = backStackEntry.toRoute()
        GroupDetailsScreen(
            modifier = Modifier.fillMaxSize(),
            appState = appState,
            notifyId = route.notifyId,
            packageName = route.packageName,
            onBackClick = onBackClick
        )
    }
}

fun NavController.navigateToStatistic(navOptions: NavOptions? = null) = navigate(route = StatisticRoute, navOptions)

fun NavGraphBuilder.statisticScreen(
    onBackClick: () -> Unit,
    onNavToDetails: (NotifyVO) -> Unit
) {
    composable<StatisticRoute> { backStackEntry ->
        StatisticScreen(
            modifier = Modifier.fillMaxSize(),
            onBackClick = onBackClick,
            onNavToDetails = onNavToDetails
        )
    }
}