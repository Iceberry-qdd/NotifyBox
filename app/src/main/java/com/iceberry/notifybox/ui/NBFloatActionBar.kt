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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import com.iceberry.notifybox.R
import com.iceberry.notifybox.navigation.TopLevelDestination
import com.iceberry.notifybox.ui.list.ListFloatActionBar
import kotlinx.coroutines.launch

@Composable
fun NBFloatActionBar(
    modifier: Modifier = Modifier,
    appState: NBAppState
) {
    val currentDestination = appState.currentDestination
    when {
        currentDestination?.hasRoute(TopLevelDestination.LIST.route) == true -> {
            ListFloatActionBar(appState = appState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrollToTopFab(
    scrollBehavior: TopAppBarScrollBehavior,
    lazyListState: LazyListState
) {
    val visible by remember {
        derivedStateOf {
            scrollBehavior.state.collapsedFraction < 1.0f && lazyListState.canScrollBackward
        }
    }
    val scope = rememberCoroutineScope()
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(),
        exit = scaleOut()
    ) {
        FloatingActionButton(
            onClick = {
                scope.launch {
                    lazyListState.animateScrollToItem(index = 0, scrollOffset = 0)
                }
            }
        ) {
            Icon(Icons.Filled.ArrowUpward, stringResource(R.string.back_to_top))
        }
    }
}