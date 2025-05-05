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

import android.content.Context
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iceberry.notifybox.navigation.NBNavHost
import com.iceberry.notifybox.ui.list.ListViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NBApp(
    appState: NBAppState,
    viewModel: ListViewModel = hiltViewModel(),
    context: Context = LocalContext.current,
) {
    BottomAppBarDefaults.exitAlwaysScrollBehavior()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val lazyListState = appState.curRouteLazyListState

    NBScaffold(
        modifier = Modifier.fillMaxSize(),
        appState = appState,
        scrollBehavior = scrollBehavior,
        scrollToTop = {
            scope.launch {
                lazyListState?.animateScrollToItem(index = 0, scrollOffset = 0)
            }
        }
    ) { paddingValues ->
        NBNavHost(
            modifier = Modifier
                .padding(
                    start = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
                    end = paddingValues.calculateEndPadding(LocalLayoutDirection.current),
                    top = if (appState.isTopLevelDestination) paddingValues.calculateTopPadding() else 0.dp
                ),
            appState = appState
        )

        if (uiState.showANLSDialog) {
            NBAlertDialog(
                onDismissRequest = { viewModel.cancelANLSSettingsDialog() },
                onConfirmation = { viewModel.confirmANSSettingsDialog(context) },
                confirmButtonText = "打开设置",
                dialogTitle = "需要开启通知监听权限",
                dialogText = "请转到设置界面并将此应用的通知监听权限打开。"
            )
        }
    }
}

