/*
 * Copyright (c) 2026 Iceberry-qdd.
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

import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.iceberry.notifybox.R
import com.iceberry.notifybox.icon.P2p
import com.iceberry.notifybox.ui.theme.NBApplicationTheme

@Composable
fun NoNearByPermissionsContent(
    modifier: Modifier = Modifier,
    permissionsState: PermissionsState,
    title: String? = null,
    icon: ImageVector? = null,
    onPermissionsChange: (PermissionsState) -> Unit = {},
    onAuthorize: () -> Unit = {}
) {
    val context = LocalContext.current

    val (defaultIcon, defaultTitle, settingIntent) = when (permissionsState) {
        PermissionsState.LOCATION_DISABLED -> Triple(
            Icons.Default.LocationOff,
            stringResource(R.string.permission_location_disabled),
            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        )

        PermissionsState.BLUETOOTH_DISABLED -> Triple(
            Icons.Default.BluetoothDisabled,
            stringResource(R.string.permission_bluetooth_disabled),
            Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
        )

        else -> Triple(
            Icons.Default.P2p,
            stringResource(R.string.permission_nearby_needed),
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = "package:${context.packageName}".toUri()
            }
        )
    }

    val displayIcon = icon ?: defaultIcon
    val displayTitle = title ?: defaultTitle

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(space = 16.dp, alignment = Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(64.dp),
            imageVector = displayIcon,
            contentDescription = displayTitle
        )
        Text(
            text = displayTitle,
            style = MaterialTheme.typography.titleSmall.merge(color = MaterialTheme.colorScheme.outline)
        )
        if (permissionsState != PermissionsState.GRANTED) {
            val isPermissionIssue =
                permissionsState == PermissionsState.DENIED || permissionsState == PermissionsState.RATIONALE
            Button(
                onClick = {
                    Log.i("71", permissionsState.toString())
                    when (permissionsState) {
                        PermissionsState.GRANTED -> {}
                        PermissionsState.DENIED -> onAuthorize()
                        PermissionsState.RATIONALE -> onAuthorize()
                        PermissionsState.LOCATION_DISABLED -> context.startActivity(settingIntent)
                        PermissionsState.BLUETOOTH_DISABLED -> context.startActivity(settingIntent)
                    }
                }
            ) {
                Text(if (isPermissionIssue) stringResource(R.string.permission_authorize_now) else stringResource(R.string.permission_go_to_enable))
            }
            if (permissionsState == PermissionsState.DENIED) {
                Text(
                    modifier = Modifier.clickable { context.startActivity(settingIntent) },
                    text = stringResource(R.string.permission_manual_fix),
                    style = MaterialTheme.typography.labelSmall.merge(color = MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun NoNearByPermissionsContentPreview() {
    NBApplicationTheme {
        NoNearByPermissionsContent(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            permissionsState = PermissionsState.RATIONALE,
            onPermissionsChange = {},
        )
    }
}