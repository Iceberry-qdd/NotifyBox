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

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryStd
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.iceberry.notifybox.R
import com.iceberry.notifybox.navigation.Route
import com.iceberry.notifybox.ui.theme.NBApplicationTheme
import kotlinx.coroutines.launch

@Composable
fun SettingContent(
    modifier: Modifier = Modifier,
    state: SettingColumnState,
    data: Map<SettingGroupEnum, List<SettingItemVO<*>>> = mapOf(),
    paddingValues: androidx.compose.foundation.layout.PaddingValues,
    onClickItem: (NavigateData) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background),
        state = state.lazyListState,
        contentPadding = paddingValues
    ) {
        items(
            count = SettingGroupEnum.entries.size,
            key = { SettingGroupEnum.entries[it] },
            contentType = { listOf<SettingItemVO<*>>() }
        ) {
            SettingGroup(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .wrapContentHeight(),
                group = SettingGroupEnum.entries[it],
                items = data[SettingGroupEnum.entries[it]] ?: emptyList(),
                snackbarHostState = state.snackbarHostState,
                onClickItem = onClickItem
            )
        }
    }
}

@Composable
@Preview
private fun SettingContentPreview() {
    NBApplicationTheme {
        SettingContent(
            onClickItem = {},
            state = SettingColumnState(
                lazyListState = rememberLazyListState(),
                loading = false,
                snackbarHostState = SnackbarHostState()
            ),
            paddingValues = androidx.compose.foundation.layout.PaddingValues(0.dp)
        )
    }
}

@Composable
fun SettingGroup(
    modifier: Modifier,
    viewModel: SettingViewModel = hiltViewModel(),
    group: SettingGroupEnum,
    items: List<SettingItemVO<*>>,
    snackbarHostState: SnackbarHostState,
    onClickItem: (NavigateData) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {
        Text(
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp),
            text = stringResource(group.displayNameRes),
            style = MaterialTheme.typography.titleSmall.merge(color = MaterialTheme.colorScheme.primary)
        )
        Column(modifier = modifier) {
            items.forEach {
                when (it.actionType) {
                    SettingActionTypeEnum.SWITCH -> {
                        @Suppress("UNCHECKED_CAST")
                        SwitchSettingItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(CardDefaults.shape)
                                .background(MaterialTheme.colorScheme.background)
                                .wrapContentHeight(),
                            data = it as SettingItemVO<SettingOptions.Bool>,
                            onClick = {
                                when (it.key) {
                                    "log" -> {
                                        if (it.value.bool) {
                                            onClickItem(
                                                NavigateData(route = Route.LogRoute)
                                            )
                                        }
                                    }

                                    else -> {}
                                }
                            },
                            onCheckedChange = { value ->
                                when (it.key) {
                                    "log" -> {
                                        viewModel.setEnableLog(
                                            SettingOptions.Bool.entries.firstOrNull { v -> v.bool == value }
                                                ?: SettingOptions.Bool.FALSE
                                        )
                                    }

                                    else -> {}
                                }
                            }
                        )
                    }

                    SettingActionTypeEnum.DIALOG -> {
                        DialogSettingItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(CardDefaults.shape)
                                .background(MaterialTheme.colorScheme.background)
                                .wrapContentHeight(),
                            data = it,
                            onCheckedChange = { option ->
                                viewModel.setDialogOption(option)
                            }
                        )
                    }

                    SettingActionTypeEnum.LINK -> {
                        LinkSettingItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(CardDefaults.shape)
                                .background(MaterialTheme.colorScheme.background)
                                .wrapContentHeight(),
                            data = it,
                            onClick = { }
                        )
                    }

                    SettingActionTypeEnum.NAVIGATE -> {
                        NavigateSettingItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(CardDefaults.shape)
                                .background(MaterialTheme.colorScheme.background)
                                .wrapContentHeight(),
                            data = it,
                            navigateData = when (it.key) {
                                "about" -> NavigateData(route = Route.AboutRoute)
                                "export" -> NavigateData(route = Route.ExportRoute)
                                else -> NavigateData()
                            },
                            onClickItem = onClickItem
                        )
                    }

                    SettingActionTypeEnum.PLAIN -> {
                        PlainSettingItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(CardDefaults.shape)
                                .background(MaterialTheme.colorScheme.background)
                                .wrapContentHeight(),
                            data = it,
                            onClick = { }
                        )
                    }

                    SettingActionTypeEnum.INTENT -> {
                        IntentSettingItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(CardDefaults.shape)
                                .background(MaterialTheme.colorScheme.background)
                                .wrapContentHeight(),
                            data = it,
                            intent = when (it.key) {
                                "disable_battery_optimization" -> Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                                    data = "package:${context.packageName}".toUri()
                                }

                                "auto_startup" -> Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = "package:${context.packageName}".toUri()
                                }

                                else -> null
                            }
                        )
                    }

                    SettingActionTypeEnum.CUSTOM -> {
                        when (it.key) {
                            "reload_icon" -> {
                                ReloadIconSettingItem(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(CardDefaults.shape)
                                        .background(MaterialTheme.colorScheme.background)
                                        .wrapContentHeight(),
                                    data = it,
                                    onConfirm = {
                                        viewModel.reloadIcons {
                                            scope.launch {
                                                snackbarHostState.showSnackbar(context.getString(R.string.setting_reload_icons_done))
                                            }
                                        }
                                    }
                                )
                            }

//                            "export" -> {
//                                ExportSettingItem(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .clip(CardDefaults.shape)
//                                        .background(MaterialTheme.colorScheme.background)
//                                        .wrapContentHeight(),
//                                    data = it,
//                                    onSelectFormat = {}
//                                )
//                            }

                            "restore" -> {
                                ImportSettingItem(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(CardDefaults.shape)
                                        .background(MaterialTheme.colorScheme.background)
                                        .wrapContentHeight(),
                                    data = it,
                                    onSelectMethod = { mode ->
                                        onClickItem(NavigateData(route = Route.ImportsRoute(mode)))
                                    }
                                )
                            }

                            else -> {
                                CustomSettingItem(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(CardDefaults.shape)
                                        .background(MaterialTheme.colorScheme.background)
                                        .wrapContentHeight(),
                                    data = it
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun SettingGroupPreview() {
    val fakeSettingItemVOS = listOf(
        SettingItemVO(
            id = 1L,
            key = "theme",
            value = SettingOptions.Theme.DEFAULT,
            name = "主题模式",
            description = "切换主题外观",
            actionType = SettingActionTypeEnum.DIALOG,
            group = SettingGroupEnum.DISPLAY,
            icon = Icons.Default.Palette
        ),
        SettingItemVO(
            id = 2L,
            key = "language",
            value = SettingOptions.Language.DEFAULT,
            name = "语言",
            description = "切换显示语言",
            actionType = SettingActionTypeEnum.DIALOG,
            group = SettingGroupEnum.DISPLAY,
            icon = Icons.Default.Translate
        ),
        SettingItemVO(
            id = 3L,
            key = "disable_battery_optimization",
            value = SettingOptions.Bool.FALSE,
            name = "忽略电池优化",
            description = "忽略电池优化，常驻后台",
            actionType = SettingActionTypeEnum.SWITCH,
            group = SettingGroupEnum.DATA_PERMISSION,
            icon = Icons.Default.BatteryStd
        )
    )

    NBApplicationTheme {
        SettingGroup(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .wrapContentHeight(),
            group = SettingGroupEnum.DISPLAY,
            items = fakeSettingItemVOS,
            snackbarHostState = SnackbarHostState(),
            onClickItem = {}
        )
    }
}

