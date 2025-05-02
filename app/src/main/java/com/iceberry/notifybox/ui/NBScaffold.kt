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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import com.iceberry.notifybox.navigation.navigateToStatistic

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NBScaffold(
    modifier: Modifier = Modifier,
    appState: NBAppState,
    scrollBehavior: TopAppBarScrollBehavior,
    content: @Composable ((PaddingValues) -> Unit)
) {
    val navController = appState.navController
    val snackbarHostState = appState.snackbarHostState

    Scaffold(
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            val density = LocalDensity.current
            NBTopAppBar(
                currentDestination = appState.currentDestination,
                scrollBehavior = scrollBehavior,
                routeToStatistic = navController::navigateToStatistic,
                orderBy = { order, field -> {} },
                windowInsets = TopAppBarDefaults.windowInsets.exclude(
                    WindowInsets(
                        top = (TopAppBarDefaults.windowInsets.getTop(density) * scrollBehavior.state.collapsedFraction).toInt()
                    )
                )
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = scrollBehavior.state.collapsedFraction < 1.0f,
                enter = slideInVertically { fullHeight -> fullHeight },
                exit = slideOutVertically { fullHeight -> fullHeight }
            ) {
                NBNavigationBar(appState = appState)
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            AnimatedVisibility(
                visible = scrollBehavior.state.collapsedFraction < 1.0f,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                NBFloatActionBar(appState = appState)
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        content(paddingValues)
    }
}

