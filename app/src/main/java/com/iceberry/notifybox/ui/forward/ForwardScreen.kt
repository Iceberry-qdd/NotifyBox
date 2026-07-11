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

package com.iceberry.notifybox.ui.forward

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iceberry.notifybox.R
import com.iceberry.notifybox.ui.NBAppState
import com.iceberry.notifybox.ui.rememberNBAppState
import com.iceberry.notifybox.ui.setting.export.ExportContent
import com.iceberry.notifybox.ui.setting.export.ExportViewModel
import com.iceberry.notifybox.ui.setting.export.NearByPermissionsEffect
import com.iceberry.notifybox.ui.setting.export.NoNearByPermissionsContent
import com.iceberry.notifybox.ui.setting.export.PermissionsState
import com.iceberry.notifybox.ui.theme.NBApplicationTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForwardScreen(
    modifier: Modifier = Modifier,
    viewModel: ForwardViewModel = hiltViewModel(),
    exportViewModel: ExportViewModel = hiltViewModel(),
    appState: NBAppState,
    paddingValues: androidx.compose.foundation.layout.PaddingValues
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val exportUiState by exportViewModel.uiState.collectAsStateWithLifecycle()
    val pager by exportViewModel.pager.collectAsStateWithLifecycle()
    val lazyListState = appState.curRouteLazyListState ?: rememberLazyListState()
    val listState = rememberForwardColumnState(
        lazyListState = lazyListState,
        loading = uiState.loading,
        snackbarHostState = appState.snackbarHostState
    )

    var launchTrigger by remember { mutableIntStateOf(0) }
    NearByPermissionsEffect(
        launchTrigger = launchTrigger,
        onTriggerReset = { launchTrigger = 0 },
        onPermissionsChange = {
            viewModel.changePermissionsState(it)
            exportViewModel.changePermissionsState(it)
        }
    )

    when (uiState.permissionsState) {
        PermissionsState.GRANTED -> {
            DisposableEffect(Unit) {
                exportViewModel.startNearByDiscovery()
                onDispose {
                    exportViewModel.stopNearByDiscovery()
                }
            }
            ExportContent(
                modifier = modifier,
                uiState = exportUiState,
                lazyListState = lazyListState,
                list = pager,
                paddingValues = paddingValues,
                doConnect = { exportViewModel.doConnect(it) },
                doExport = { exportViewModel.doExport(it) }
            )
        }

        else -> {
            NoNearByPermissionsContent(
                modifier = modifier.padding(paddingValues),
                permissionsState = uiState.permissionsState,
                onPermissionsChange = {
                    viewModel.changePermissionsState(it)
                    exportViewModel.changePermissionsState(it)
                },
                onAuthorize = { launchTrigger++ }
            )
        }
    }

}

@Composable
internal fun InternalForwardScreen(
    modifier: Modifier = Modifier
) {
    Text(stringResource(R.string.forward_stats))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForwardTopAppBar(
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: ExportViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()

    TopAppBar(
        modifier = modifier,
        title = { Text(text = stringResource(R.string.forward_title)) },
        windowInsets = windowInsets,
        scrollBehavior = scrollBehavior,
        actions = {
            IconButton(
                onClick = {
                    scope.launch { viewModel.enableScan() }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.forward_rescan)
                )
            }
        }
    )
}

// Previews

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    NBApplicationTheme {
        ForwardScreen(
            appState = rememberNBAppState(),
            paddingValues = androidx.compose.foundation.layout.PaddingValues(0.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 480)
@Composable
private fun PortraitPreview() {
    NBApplicationTheme {
        ForwardScreen(
            appState = rememberNBAppState(),
            paddingValues = androidx.compose.foundation.layout.PaddingValues(0.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun ForwardTopAppBarLightPreview() {
    NBApplicationTheme {
        ForwardTopAppBar(
            scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        )
    }
}
