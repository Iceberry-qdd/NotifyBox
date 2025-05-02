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

import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun NBAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    confirmButtonText: String = "确定",
    dismissButtonText: String = "取消",
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector? = null,
) {
    AlertDialog(
        icon = { icon?.also { Icon(it, contentDescription = "Example Icon") } },
        title = { Text(text = dialogTitle) },
        text = { Text(text = dialogText) },
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(dismissButtonText)
            }
        }
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun NBAlertDialogLightPreview() {
    NBAlertDialog(
        onConfirmation = {},
        onDismissRequest = {},
        dialogTitle = "测试",
        dialogText = "Lorem ipsum dolor sit amet gubergren ut facilisi ut lorem sed et at stet nostrud. No clita invidunt sit duo velit dolor sit te laoreet et enim invidunt et iriure.",
        icon = Icons.Filled.Celebration,
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun NBAlertDialogDarkPreview() {
    NBAlertDialog(
        onConfirmation = {},
        onDismissRequest = {},
        dialogTitle = "测试",
        dialogText = "Lorem ipsum dolor sit amet gubergren ut facilisi ut lorem sed et at stet nostrud. No clita invidunt sit duo velit dolor sit te laoreet et enim invidunt et iriure.",
        icon = Icons.Filled.Celebration,
    )
}