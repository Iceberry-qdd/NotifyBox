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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.iceberry.notifybox.ui.theme.NBApplicationTheme

@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val items by viewModel.uiState.collectAsStateWithLifecycle()
    if (items) {
        SettingScreen(
            modifier = modifier
        )
    }
}

@Composable
internal fun SettingScreen(
    modifier: Modifier = Modifier
) {
    Text("设置")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingTopAppBar(
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets
) {
    TopAppBar(
        title = { Text(text = "设置") },
        modifier = modifier,
        windowInsets = windowInsets
    )
}

// Previews

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    NBApplicationTheme {
        SettingScreen()
    }
}

@Preview(showBackground = true, widthDp = 480)
@Composable
private fun PortraitPreview() {
    NBApplicationTheme {
        SettingScreen()
    }
}

@PreviewLightDark
@Composable
private fun SettingTopAppBarLightPreview() {
    NBApplicationTheme {
        SettingTopAppBar()
    }
}