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

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.iceberry.notifybox.R
import com.iceberry.notifybox.ui.setting.export.ConnectState
import com.iceberry.notifybox.ui.setting.export.DeviceInfo
import com.iceberry.notifybox.ui.setting.export.TransferState
import com.iceberry.notifybox.ui.theme.Green500
import com.iceberry.notifybox.ui.theme.NBApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportsContent(
    modifier: Modifier = Modifier,
    data: DeviceInfo,
    doAccept: () -> Unit = {},
    doReject: () -> Unit = {},
    doImports: () -> Unit = {}
) {
    Column(
        modifier = modifier
    ) {
        if (data.endpointId.isNotEmpty()) {
            ImportsItemContent(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .clip(CardDefaults.shape)
                    .wrapContentHeight()
                    .background(MaterialTheme.colorScheme.background),
                data = data,
                doAccept = doAccept,
                doReject = doReject,
                doImports = doImports
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ImportsContentPreview() {
    NBApplicationTheme {
        ImportsContent(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surface),
            data = DeviceInfo(
                endpointId = "2",
                name = "Iceberry的手机",
                description = "测试描述测试描述测试描述测试描述测试描述",
                icon = Icons.Filled.PhoneIphone,
                connectState = ConnectState.NOT_CONNECT,
                transferState = TransferState.TRANSFER_INIT,
                transferProgress = 0.0f
            )
        )
    }
}

@Composable
fun ImportsItemContent(
    modifier: Modifier = Modifier,
    data: DeviceInfo,
    doAccept: () -> Unit = {},
    doReject: () -> Unit = {},
    doImports: () -> Unit = {}
) {
    val animatedProgress by animateFloatAsState(
        targetValue = data.transferProgress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "transferProgress"
    )
    val transferring = data.connectState == ConnectState.CONNECTED && data.transferState == TransferState.TRANSFERRING
    val connecting = data.connectState == ConnectState.CONNECTING
    val connected = data.connectState == ConnectState.CONNECTED

    OutlinedCard(
        modifier = modifier
            .clickable(enabled = true, onClick = {})
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
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
                        text = data.description,
                        style = MaterialTheme.typography.bodySmall.merge(color = MaterialTheme.colorScheme.outline)
                    )
                }

                if (data.transferState == TransferState.TRANSFERRED) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(R.string.import_success),
                        tint = Green500
                    )
                }
            }

            if (data.connectState == ConnectState.CONNECTING) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(space = 16.dp, alignment = Alignment.End)
                ) {
                    FilledTonalButton(onClick = doReject) { Text(stringResource(R.string.reject_connection)) }
                    Button(onClick = doAccept) { Text(stringResource(R.string.accept_connection)) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun ImportsItemContentPreview() {
    NBApplicationTheme {
        ImportsItemContent(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CardDefaults.shape)
                .wrapContentHeight()
                .background(MaterialTheme.colorScheme.background),
            data = DeviceInfo(endpointId = "1")
        )
    }
}