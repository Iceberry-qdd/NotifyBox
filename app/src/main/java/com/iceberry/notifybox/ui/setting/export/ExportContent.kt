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

package com.iceberry.notifybox.ui.setting.export

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.iceberry.notifybox.R
import com.iceberry.notifybox.icon.P2p
import com.iceberry.notifybox.ui.theme.Green500
import com.iceberry.notifybox.ui.theme.NBApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportContent(
    modifier: Modifier = Modifier,
    list: List<DeviceInfo>,
    uiState: ExportUiState,
    lazyListState: LazyListState,
    paddingValues: androidx.compose.foundation.layout.PaddingValues,
    doConnect: (String) -> Unit = {},
    doExport: (String) -> Unit = {}
) {
    if (!uiState.loading && list.isEmpty()) {
        NoNearByPermissionsContent(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize(),
            permissionsState = PermissionsState.GRANTED,
            title = stringResource(R.string.export_discovering_nearby),
            icon = Icons.Default.P2p
        )
        return
    }

    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        contentPadding = paddingValues
    ) {
        if (uiState.scanning) {
            item {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }

        if (uiState.loading) {
            item {
                Box(
                    modifier = Modifier
                        .padding(vertical = 24.dp)
                        .fillParentMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
            return@LazyColumn
        }

        items(
            count = list.size,
            key = { list[it].deviceUniqueId },
            contentType = { DeviceInfo("") }
        ) { i ->
            ExportItemContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CardDefaults.shape)
                    .wrapContentHeight()
                    .background(MaterialTheme.colorScheme.background),
                data = list[i],
                doConnect = { doConnect(list[i].deviceUniqueId) },
                doExport = { doExport(list[i].deviceUniqueId) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun ExportContentPreview() {
    NBApplicationTheme {
        ExportContent(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surface),
            uiState = ExportUiState(
                loading = false,
                scanning = true
            ),
            paddingValues = androidx.compose.foundation.layout.PaddingValues(0.dp),
            lazyListState = rememberLazyListState(),
            list = listOf(
                DeviceInfo(endpointId = "1"),
                DeviceInfo(
                    endpointId = "2",
                    name = "Iceberry的手机",
                    description = "测试描述测试描述测试描述测试描述测试描述",
                    icon = Icons.Filled.PhoneIphone,
                    connectState = ConnectState.NOT_CONNECT,
                    transferState = TransferState.TRANSFER_INIT,
                    transferProgress = 0.0f
                )
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportItemContent(
    modifier: Modifier = Modifier,
    data: DeviceInfo,
    doConnect: () -> Unit = {},
    doExport: () -> Unit = {}
) {
    val animatedProgress by animateFloatAsState(
        targetValue = data.transferProgress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "transferProgress"
    )
    val transferring =
        (data.connectState == ConnectState.CONNECTED || data.endpointId.isNotEmpty()) && data.transferState == TransferState.TRANSFERRING
    val connecting = data.connectState == ConnectState.CONNECTING
    val connected = data.connectState == ConnectState.CONNECTED
    val isHistory = !data.isDiscovered

    Card(
        modifier = modifier
            .alpha(if (isHistory) 0.6f else 1f)
            .clickable(enabled = !isHistory, onClick = {})
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(space = 24.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = CircleShape
                        )
                        .padding(16.dp),
                    imageVector = data.icon,
                    contentDescription = stringResource(R.string.export_nearby_devices)
                )
                if (transferring) {
                    if (data.transferProgress > 0f && data.transferProgress < 1f) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(56.dp),
                            progress = { animatedProgress }
                        )
                    } else {
                        CircularProgressIndicator(
                            modifier = Modifier.size(56.dp)
                        )
                    }
                }
                if (connecting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(56.dp)
                    )
                }
                if (connected) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .align(Alignment.BottomEnd)
                            .offset(x = (-4).dp, y = (-4).dp)
                            .border(
                                width = 2.dp,
                                color = Color.White,
                                shape = CircleShape
                            )
                            .padding(2.dp)
                            .background(Green500, CircleShape)
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = data.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (isHistory) stringResource(R.string.export_history_devices) else data.description,
                    style = MaterialTheme.typography.bodySmall.merge(color = MaterialTheme.colorScheme.outline)
                )
            }
            if (!isHistory) {
                when (data.connectState) {
                    ConnectState.NOT_CONNECT -> Button(onClick = doConnect) { Text(stringResource(R.string.export_connect)) }
                    ConnectState.CONNECTING -> {}
                    ConnectState.CONNECTED -> when (data.transferState) {
                        TransferState.TRANSFER_INIT -> Button(onClick = doExport) { Text(stringResource(R.string.export_button)) }
                        TransferState.TRANSFERRING -> {}
                        TransferState.TRANSFERRED -> IconButton(onClick = {}, enabled = false) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = stringResource(R.string.export_success),
                                tint = Green500
                            )
                        }

                        TransferState.TRANSFER_FAILED -> IconButton(onClick = doExport) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = stringResource(R.string.export_retry)
                            )
                        }
                    }

                    ConnectState.CONNECT_FAILED -> IconButton(onClick = doConnect) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.export_reconnect)
                        )
                    }
                }
            } else {
                // For historical devices, maybe just show last seen or nothing
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun ExportItemContentPreview() {
    NBApplicationTheme {
        ExportItemContent(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CardDefaults.shape)
                .wrapContentHeight()
                .background(MaterialTheme.colorScheme.background),
            data = DeviceInfo(endpointId = "1")
        )
    }
}