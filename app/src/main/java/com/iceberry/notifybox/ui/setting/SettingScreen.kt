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

package com.iceberry.notifybox.ui.setting

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iceberry.notifybox.R
import com.iceberry.notifybox.ui.NBAppState
import com.iceberry.notifybox.ui.rememberNBAppState
import com.iceberry.notifybox.ui.theme.NBApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = hiltViewModel(),
    appState: NBAppState,
    onClickItem: (NavigateData) -> Unit
) {
    val items by viewModel.pager.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lazyListState = appState.curRouteLazyListState ?: rememberLazyListState()
    val listState = rememberSettingColumnState(
        lazyListState = lazyListState,
        loading = uiState.loading,
        snackbarHostState = appState.snackbarHostState
    )

    SettingContent(
        modifier = modifier,
        state = listState,
        data = items,
        onClickItem = onClickItem
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingTopAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.nav_settings)) },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        windowInsets = windowInsets
    )
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    NBApplicationTheme {
        SettingScreen(
            onClickItem = {},
            appState = rememberNBAppState()
        )
    }
}

@Preview(showBackground = true, widthDp = 480)
@Composable
private fun PortraitPreview() {
    NBApplicationTheme {
        SettingScreen(
            appState = rememberNBAppState(),
            onClickItem = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun SettingTopAppBarLightPreview() {
    NBApplicationTheme {
        SettingTopAppBar(
            scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        )
    }
}