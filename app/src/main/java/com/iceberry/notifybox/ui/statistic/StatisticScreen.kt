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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.navigation.compose.hiltViewModel
import com.iceberry.notifybox.ui.theme.NBApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticScreen(
    modifier: Modifier = Modifier,
    viewModel: StatisticViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val snackbarHostState = remember { SnackbarHostState() }
//    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            val density = LocalDensity.current
            StatisticTopAppBar(
                onBack = onBackClick,
                scrollBehavior = scrollBehavior,
                windowInsets = TopAppBarDefaults.windowInsets.exclude(
                    WindowInsets(
                        top = (TopAppBarDefaults.windowInsets.getTop(density) * scrollBehavior.state.collapsedFraction).toInt()
                    )
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "暂无新消息",
                style = MaterialTheme.typography.bodyMedium.merge(color = MaterialTheme.colorScheme.outline)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticTopAppBar(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets
) {
    TopAppBar(
        title = { Text(text = "统计详情") },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回")
            }
        },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        windowInsets = windowInsets
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun StatisticTopAppBarLightPreview() {
    NBApplicationTheme {
        StatisticTopAppBar(
            onBack = {},
            scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        )
    }
}