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

package com.iceberry.notifybox.ui.setting.imports

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iceberry.notifybox.R
import com.iceberry.notifybox.icon.P2p
import com.iceberry.notifybox.ui.ScrollToTopFab
import com.iceberry.notifybox.ui.setting.SettingOptions
import com.iceberry.notifybox.ui.setting.export.NearByPermissionsEffect
import com.iceberry.notifybox.ui.setting.export.NoNearByPermissionsContent
import com.iceberry.notifybox.ui.setting.export.PermissionsState
import com.iceberry.notifybox.ui.theme.NBApplicationTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportsScreen(
    modifier: Modifier = Modifier,
    viewModel: ImportsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    mode: SettingOptions.ImportMethod
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lazyListState: LazyListState = rememberLazyListState()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            val density = LocalDensity.current
            ImportsTopAppBar(
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
        floatingActionButton = {
            ScrollToTopFab(
                scrollBehavior = scrollBehavior,
                lazyListState = lazyListState
            )
        }
    ) { paddingValues ->
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val params by rememberUpdatedState(mode)
        LaunchedEffect(params) {
            scope.launch {
                viewModel.getParams(params)
            }
        }

        var launchTrigger by remember { mutableIntStateOf(0) }
        NearByPermissionsEffect(
            launchTrigger = launchTrigger,
            onTriggerReset = { launchTrigger = 0 },
            onPermissionsChange = {
                Log.i("109", "$it")
                viewModel.changePermissionsState(it)
            }
        )

        LaunchedEffect(uiState.error) {
            uiState.error?.let {
                val result = snackbarHostState.showSnackbar(
                    message = it,
                    actionLabel = context.getString(R.string.confirm),
                    duration = SnackbarDuration.Indefinite
                )
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.clearError()
                }
            }
        }

        LaunchedEffect(uiState.permissionsState) {
            if (uiState.permissionsState == PermissionsState.GRANTED) {
                viewModel.startNearByAdvertising()
            } else {
                viewModel.stopNearByAdvertising()
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                viewModel.stopNearByAdvertising()
            }
        }

        when (uiState.permissionsState) {
            PermissionsState.GRANTED -> {
                if (uiState.deviceInfo.endpointId.isEmpty()) {
                    NoNearByPermissionsContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface),
                        permissionsState = PermissionsState.GRANTED, // Reusing component for scanning state
                        title = stringResource(R.string.export_discovering_nearby),
                        icon = Icons.Default.P2p
                    )
                } else {
                    ImportsContent(
                        modifier = modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface),
                        data = uiState.deviceInfo,
                        paddingValues = paddingValues,
                        doAccept = { viewModel.acceptConnection() },
                        doReject = { viewModel.rejectConnection() }
                    )
                }
            }

            else -> {
                NoNearByPermissionsContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface),
                    permissionsState = uiState.permissionsState,
                    onPermissionsChange = { viewModel.changePermissionsState(it) },
                    onAuthorize = { launchTrigger++ }
                )
            }
        }

        if (uiState.showRestartDialog) {
            AlertDialog(
                onDismissRequest = { },
                confirmButton = {
                    TextButton(onClick = { viewModel.restartApp() }) {
                        Text(stringResource(R.string.restart_now))
                    }
                },
                title = { Text(stringResource(R.string.import_success)) },
                text = {
                    val text = if (mode == SettingOptions.ImportMethod.MERGE) stringResource(R.string.import_success_merge)
                    else stringResource(R.string.import_success_overwrite)
                    Text(text)
                },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportsTopAppBar(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets
) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.import_title)) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
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
private fun ImportsTopAppBarLightPreview() {
    NBApplicationTheme {
        ImportsTopAppBar(
            onBack = {},
            scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        )
    }
}