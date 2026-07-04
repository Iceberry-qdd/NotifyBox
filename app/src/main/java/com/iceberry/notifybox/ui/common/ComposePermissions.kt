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

package com.iceberry.notifybox.ui.common

import android.Manifest
import android.widget.Toast
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.iceberry.notifybox.R

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ComposePermissions() {
    val context = LocalContext.current

    // Register and remember the permission state
    val callPermissionState = rememberPermissionState(Manifest.permission.CALL_PHONE)

    val permissionCallTitle = stringResource(R.string.permission_call_title)
    val permissionCallGrant = stringResource(R.string.permission_call_grant)
    val permissionCallGranted = stringResource(R.string.permission_call_granted)
    val permissionCallAction = stringResource(R.string.permission_call_action)

    // Utility function to calculate the state based on the PermissionState
    fun getScreenState(state: PermissionState) = when (state.status) {
        is PermissionStatus.Denied -> PermissionScreenState(
            title = permissionCallTitle, buttonText = permissionCallGrant
        )

        PermissionStatus.Granted -> PermissionScreenState(
            title = permissionCallGranted, buttonText = permissionCallAction
        )
    }

    // Defines the PermissionScreen UI based on the permission state and user interactions
    var screenState by remember(callPermissionState.status) {
        mutableStateOf(getScreenState(callPermissionState))
    }

    PermissionScreen(
        state = screenState,
        onClick = {
            // Always request permissions in-context, provide a rationale if needed and check its status
            // before using an API that requires a permission.
            when (callPermissionState.status) {
                PermissionStatus.Granted -> {
                    Toast.makeText(context, context.getString(R.string.permission_call_faking), Toast.LENGTH_SHORT).show()
                }

                is PermissionStatus.Denied -> {
                    if (callPermissionState.status.shouldShowRationale) {
                        // Update our UI based on the user interaction by showing a rationale
                        screenState = PermissionScreenState(
                            title = permissionCallTitle,
                            buttonText = permissionCallGrant,
                            rationale = context.getString(R.string.permission_call_rationale)
                        )
                    } else {
                        // Directly launch the system permission dialog
                        callPermissionState.launchPermissionRequest()
                    }
                }
            }
        },
        onRationaleReply = { accepted ->
            if (accepted) {
                callPermissionState.launchPermissionRequest()
            }

            // Reset the state after user interaction
            screenState = getScreenState(callPermissionState)
        }
    )
}

@Composable
@PreviewLightDark
fun ComposePermissionsPreview() {
    MaterialTheme() {
        ComposePermissions()
    }
}