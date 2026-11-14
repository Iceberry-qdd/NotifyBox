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

package com.iceberry.notifybox.ui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.util.trace
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.iceberry.notifybox.navigation.Route
import com.iceberry.notifybox.navigation.TopLevelDestination
import com.iceberry.notifybox.navigation.TopLevelDestination.LIST
import com.iceberry.notifybox.navigation.TopLevelDestination.SETTINGS
import com.iceberry.notifybox.navigation.navigateToList
import com.iceberry.notifybox.navigation.navigateToSettings
import kotlinx.coroutines.CoroutineScope
import kotlin.reflect.KClass

@Composable
fun rememberNBAppState(
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    lazyListStates: Map<KClass<*>, LazyListState> = mapOf(Pair(Route.ListRoute::class, rememberLazyListState()))
): NBAppState {
    return remember(
        navController,
        coroutineScope,
        snackbarHostState
    ) {
        NBAppState(
            navController = navController,
            coroutineScope = coroutineScope,
            snackbarHostState = snackbarHostState,
            lazyListStates = lazyListStates
        )
    }
}

class NBAppState(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope,
    val snackbarHostState: SnackbarHostState,
    val lazyListStates: Map<KClass<*>, LazyListState>
) {
    private val previousDestination = mutableStateOf<NavDestination?>(null)

    val isTopLevelDestination: Boolean
        @Composable get() {
            return TopLevelDestination.entries.any { dest ->
                currentDestination?.hasRoute(dest.route) == true
            }
        }

    val isGroupDetailsRoute: Boolean
        @Composable get() {
            return currentDestination?.hasRoute(Route.GroupDetailsRoute::class) == true
        }

    val currentDestination: NavDestination?
        @Composable get() {
            // Collect the currentBackStackEntryFlow as a state
            val currentEntry = navController.currentBackStackEntryFlow.collectAsState(initial = null)

            // Fallback to previousDestination if currentEntry is null
            return currentEntry.value?.destination.also { destination ->
                if (destination != null) {
                    previousDestination.value = destination
                }
            } ?: previousDestination.value
        }

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() {
            return TopLevelDestination.entries.firstOrNull { topLevelDestination ->
                currentDestination?.hasRoute(route = topLevelDestination.route) == true
            }
        }

    val curRouteLazyListState: LazyListState?
        @Composable get() {
            return lazyListStates.entries
                .firstOrNull { (k, v) -> currentDestination?.hasRoute(k) == true }
                ?.value
        }

    /**
     * Map of top level destinations to be used in the TopBar, BottomBar and NavRail. The key is the
     * route.
     */
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

    /**
     * UI logic for navigating to a top level destination in the app. Top level destinations have
     * only one copy of the destination of the back stack, and save and restore state whenever you
     * navigate to and from it.
     *
     * @param topLevelDestination: The destination the app needs to navigate to.
     */
    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        trace("Navigation: ${topLevelDestination.name}") {
            val topLevelNavOptions = navOptions {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
            }

            when (topLevelDestination) {
                LIST -> navController.navigateToList(topLevelNavOptions)
                SETTINGS -> navController.navigateToSettings(topLevelNavOptions)
            }
        }
    }
}