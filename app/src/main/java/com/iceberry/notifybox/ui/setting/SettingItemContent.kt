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

package com.iceberry.notifybox.ui.setting

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Merge
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.TableView
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.RadioButton
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.iceberry.notifybox.ui.theme.NBApplicationTheme
import kotlinx.coroutines.launch

@Composable
fun SwitchSettingItem(
    modifier: Modifier,
    data: SettingItemVO<SettingOptions.Bool>,
    onClick: () -> Unit = {},
    onCheckedChange: (Boolean) -> Unit
) {
    SettingItem(
        modifier = modifier,
        data = data,
        onClick = onClick
    ) {
        Switch(
            checked = data.value.bool,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
//@PreviewLightDark
private fun SwitchSettingItemPreview() {
    val fakeSettingItemVO = SettingItemVO(
        id = 1L,
        key = "theme",
        value = SettingOptions.Bool.FALSE,
        name = "主题模式",
        description = "切换主题外观",
        actionType = SettingActionTypeEnum.SWITCH,
        group = SettingGroupEnum.DISPLAY,
        icon = Icons.Default.Palette
    )

    NBApplicationTheme {
        SwitchSettingItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CardDefaults.shape)
                .background(MaterialTheme.colorScheme.background)
                .wrapContentHeight(),
            data = fakeSettingItemVO,
            onCheckedChange = {}
        )
    }
}

@Composable
fun DialogSettingItem(
    modifier: Modifier,
    data: SettingItemVO<*>,
    onClick: () -> Unit = {},
    onCheckedChange: (Enumeration) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    SettingItem(
        modifier = modifier,
        data = data,
        onClick = {
            showDialog = true
            onClick()
        }
    ) {}

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {},
            title = { Text(data.name) },
            text = {
                Column {
                    data.actionOptions.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(ShapeDefaults.Medium)
                                .selectable(
                                    selected = (option == data.value),
                                    onClick = {
                                        onCheckedChange(option)
                                        showDialog = false
                                    }
                                )
                        ) {
                            RadioButton(
                                selected = (option == data.value),
                                onClick = {
                                    onCheckedChange(option)
                                    showDialog = false
                                }
                            )
                            Text(text = option.displayName, modifier = Modifier.padding(start = 16.dp))
                        }
                    }
                }
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        )
    }
}

@Composable
//@PreviewLightDark
private fun DialogSettingItemPreview() {
    val fakeSettingItemVO = SettingItemVO(
        id = 1L,
        key = "theme",
        value = SettingOptions.Theme.DEFAULT,
        name = "主题模式",
        actionType = SettingActionTypeEnum.SWITCH,
        group = SettingGroupEnum.DISPLAY,
        icon = Icons.Default.Palette
    )

    NBApplicationTheme {
        DialogSettingItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CardDefaults.shape)
                .background(MaterialTheme.colorScheme.background)
                .wrapContentHeight(),
            data = fakeSettingItemVO,
            onCheckedChange = {}
        )
    }
}

@Composable
fun LinkSettingItem(
    modifier: Modifier,
    data: SettingItemVO<*>,
    onClick: () -> Unit = {}
) {
    SettingItem(
        modifier = modifier,
        data = data,
        onClick = {
            onClick()
        }
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
            contentDescription = "打开链接"
        )
    }
}

@Composable
//@PreviewLightDark
private fun LinkSettingItemPreview() {
    val fakeSettingItemVO = SettingItemVO(
        id = 1L,
        key = "theme",
        value = SettingOptions.Theme.DEFAULT,
        name = "主题模式",
        actionType = SettingActionTypeEnum.SWITCH,
        group = SettingGroupEnum.DISPLAY,
        icon = Icons.Default.Palette
    )

    NBApplicationTheme {
        LinkSettingItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CardDefaults.shape)
                .background(MaterialTheme.colorScheme.background)
                .wrapContentHeight(),
            data = fakeSettingItemVO
        )
    }
}

@Composable
fun NavigateSettingItem(
    modifier: Modifier,
    data: SettingItemVO<*>,
    navigateData: NavigateData,
    onClickItem: (NavigateData) -> Unit
) {
    SettingItem(
        modifier = modifier,
        data = data,
        onClick = {
            onClickItem(navigateData)
        }
    ) {}
}

@Composable
//@PreviewLightDark
private fun NavigateSettingItemPreview() {
    val fakeSettingItemVO = SettingItemVO(
        id = 1L,
        key = "theme",
        value = SettingOptions.Theme.DEFAULT,
        name = "主题模式",
        actionType = SettingActionTypeEnum.SWITCH,
        group = SettingGroupEnum.DISPLAY,
        icon = Icons.Default.Palette
    )

    NBApplicationTheme {
        NavigateSettingItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CardDefaults.shape)
                .background(MaterialTheme.colorScheme.background)
                .wrapContentHeight(),
            data = fakeSettingItemVO,
            navigateData = NavigateData(),
            onClickItem = {}
        )
    }
}

@Composable
fun PlainSettingItem(
    modifier: Modifier,
    data: SettingItemVO<*>,
    onClick: () -> Unit = {}
) {
    SettingItem(
        modifier = modifier,
        data = data,
        onClick = {
            onClick()
        }
    ) {}
}

@Composable
//@PreviewLightDark
private fun PlainSettingItemPreview() {
    val fakeSettingItemVO = SettingItemVO(
        id = 1L,
        key = "theme",
        value = SettingOptions.Theme.DEFAULT,
        name = "主题模式",
        actionType = SettingActionTypeEnum.SWITCH,
        group = SettingGroupEnum.DISPLAY,
        icon = Icons.Default.Palette
    )

    NBApplicationTheme {
        PlainSettingItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CardDefaults.shape)
                .background(MaterialTheme.colorScheme.background)
                .wrapContentHeight(),
            data = fakeSettingItemVO
        )
    }
}

@Composable
fun IntentSettingItem(
    modifier: Modifier,
    data: SettingItemVO<*>,
    intent: Intent? = null
) {
    val context = LocalContext.current

    SettingItem(
        modifier = modifier,
        data = data,
        onClick = {
            intent
                ?.runCatching { context.startActivity(this) }
                ?.onFailure { Toast.makeText(context, "无法跳转至指定界面", Toast.LENGTH_SHORT).show() }
        }
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "跳转到"
        )
    }
}

@Composable
//@PreviewLightDark
private fun IntentSettingItemPreview() {
    val fakeSettingItemVO = SettingItemVO(
        id = 1L,
        key = "theme",
        value = SettingOptions.Theme.DEFAULT,
        name = "主题模式",
        actionType = SettingActionTypeEnum.SWITCH,
        group = SettingGroupEnum.DISPLAY,
        icon = Icons.Default.Palette
    )

    NBApplicationTheme {
        IntentSettingItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CardDefaults.shape)
                .background(MaterialTheme.colorScheme.background)
                .wrapContentHeight(),
            data = fakeSettingItemVO
        )
    }
}

@Composable
fun CustomSettingItem(
    modifier: Modifier,
    data: SettingItemVO<*>,
    onClick: () -> Unit = {}
) {
    SettingItem(
        modifier = modifier,
        data = data,
        onClick = {
            onClick()
        }
    ) {}
}

@Composable
//@PreviewLightDark
private fun CustomSettingItemPreview() {
    val fakeSettingItemVO = SettingItemVO(
        id = 1L,
        key = "theme",
        value = SettingOptions.Theme.DEFAULT,
        name = "主题模式",
        actionType = SettingActionTypeEnum.SWITCH,
        group = SettingGroupEnum.DISPLAY,
        icon = Icons.Default.Palette
    )

    NBApplicationTheme {
        CustomSettingItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CardDefaults.shape)
                .background(MaterialTheme.colorScheme.background)
                .wrapContentHeight(),
            data = fakeSettingItemVO
        )
    }
}

@Composable
fun ReloadIconSettingItem(
    modifier: Modifier,
    data: SettingItemVO<*>,
    onClick: () -> Unit = {},
    onConfirm: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    SettingItem(
        modifier = modifier,
        data = data,
        onClick = {
            showDialog = true
            onClick()
        }
    ) {}

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm()
                        showDialog = false
                    },
                    content = { Text("确定") }
                )
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false },
                    content = { Text("取消") }
                )
            },
            title = { Text(data.description) },
            text = { Text("重载通知图标后，获取到的通知将统一显示为所属应用的图标。（非必要请勿执行此操作）") },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        )
    }
}

@Composable
//@PreviewLightDark
private fun ReloadIconSettingItemPreview() {
    val fakeSettingItemVO = SettingItemVO(
        id = 1L,
        key = "theme",
        value = SettingOptions.Theme.DEFAULT,
        name = "主题模式",
        actionType = SettingActionTypeEnum.SWITCH,
        group = SettingGroupEnum.DISPLAY,
        icon = Icons.Default.Palette
    )

    NBApplicationTheme {
        ReloadIconSettingItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CardDefaults.shape)
                .background(MaterialTheme.colorScheme.background)
                .wrapContentHeight(),
            data = fakeSettingItemVO,
            onConfirm = {}
        )
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ExportSettingItem(
//    modifier: Modifier,
//    data: SettingItemVO<*>,
//    onClick: () -> Unit = {},
//    onSelectFormat: (SettingOptions.ExportFormat) -> Unit
//) {
//    var showBottomSheet by remember { mutableStateOf(false) }
//    val sheetState = rememberModalBottomSheetState(
//        skipPartiallyExpanded = false
//    )
//    val scope = rememberCoroutineScope()
//
//    SettingItem(
//        modifier = modifier,
//        iconModifier = Modifier.rotate(180f),
//        data = data,
//        onClick = {
//            showBottomSheet = true
//            onClick()
//        }
//    ) {}
//
//    if (showBottomSheet) {
//        ModalBottomSheet(
//            onDismissRequest = { showBottomSheet = false },
//            sheetState = sheetState,
//            properties = ModalBottomSheetProperties(),
//            containerColor = MaterialTheme.colorScheme.background
//        ) {
//            SettingItem(
//                modifier = modifier,
//                data = SettingItemVO(
//                    id = 1L,
//                    key = "export_csv",
//                    name = "导出为CSV",
//                    value = SettingOptions.Nop.NOP,
//                    description = "将数据导出为CSV文件，不包含应用图标",
//                    actionType = SettingActionTypeEnum.CUSTOM,
//                    group = SettingGroupEnum.BACKUP,
//                    icon = Icons.Default.TableView
//                ),
//                onClick = {
//                    scope.launch {
//                        onSelectFormat(SettingOptions.ExportFormat.CSV)
//                        sheetState.hide()
//                    }
//                }
//            ) {}
//            SettingItem(
//                modifier = modifier,
//                data = SettingItemVO(
//                    id = 2L,
//                    key = "export_zip",
//                    name = "导出为ZIP",
//                    value = SettingOptions.Nop.NOP,
//                    description = "将数据导出为ZIP文件，包含应用图标",
//                    actionType = SettingActionTypeEnum.CUSTOM,
//                    group = SettingGroupEnum.BACKUP,
//                    icon = Icons.Default.FolderZip
//                ),
//                onClick = {
//                    scope.launch {
//                        onSelectFormat(SettingOptions.ExportFormat.ZIP)
//                        sheetState.hide()
//                    }
//                }
//            ) {}
//        }
//    }
//}

//@Composable
////@PreviewLightDark
//private fun ExportSettingItemPreview() {
//    val fakeSettingItemVO = SettingItemVO(
//        id = 1L,
//        key = "theme",
//        value = SettingOptions.Theme.DEFAULT,
//        name = "主题模式",
//        actionType = SettingActionTypeEnum.SWITCH,
//        group = SettingGroupEnum.DISPLAY,
//        icon = Icons.Default.Palette
//    )
//
//    NBApplicationTheme {
//        ExportSettingItem(
//            modifier = Modifier
//                .fillMaxWidth()
//                .clip(CardDefaults.shape)
//                .background(MaterialTheme.colorScheme.background)
//                .wrapContentHeight(),
//            data = fakeSettingItemVO,
//            onSelectFormat = {}
//        )
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportSettingItem(
    modifier: Modifier,
    data: SettingItemVO<*>,
    onClick: () -> Unit = {},
    onSelectMethod: (SettingOptions.ImportMethod) -> Unit
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )
    val scope = rememberCoroutineScope()

    SettingItem(
        modifier = modifier,
        data = data,
        onClick = {
            showBottomSheet = true
            onClick()
        }
    ) {}

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            properties = ModalBottomSheetProperties(),
            containerColor = MaterialTheme.colorScheme.background
        ) {
            SettingItem(
                modifier = modifier,
                data = SettingItemVO(
                    id = 1L,
                    key = "override_import",
                    name = "覆盖导入",
                    value = SettingOptions.Nop.NOP,
                    description = "删除所有数据后导入",
                    actionType = SettingActionTypeEnum.CUSTOM,
                    group = SettingGroupEnum.BACKUP,
                    icon = Icons.Default.Draw
                ),
                onClick = {
                    scope.launch {
                        onSelectMethod(SettingOptions.ImportMethod.OVERRIDE)
                        sheetState.hide()
                    }
                }
            ) {}
            SettingItem(
                modifier = modifier,
                data = SettingItemVO(
                    id = 2L,
                    key = "merge_import",
                    name = "合并导入",
                    value = SettingOptions.Nop.NOP,
                    description = "导入数据与当前数据合并",
                    actionType = SettingActionTypeEnum.CUSTOM,
                    group = SettingGroupEnum.BACKUP,
                    icon = Icons.Default.Merge
                ),
                onClick = {
                    scope.launch {
                        onSelectMethod(SettingOptions.ImportMethod.MERGE)
                        sheetState.hide()
                    }
                }
            ) {}
        }
    }
}

@Composable
//@PreviewLightDark
private fun ImportSettingItemPreview() {
    val fakeSettingItemVO = SettingItemVO(
        id = 1L,
        key = "theme",
        value = SettingOptions.Theme.DEFAULT,
        name = "主题模式",
        actionType = SettingActionTypeEnum.SWITCH,
        group = SettingGroupEnum.DISPLAY,
        icon = Icons.Default.Palette
    )

    NBApplicationTheme {
        ImportSettingItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CardDefaults.shape)
                .background(MaterialTheme.colorScheme.background)
                .wrapContentHeight(),
            data = fakeSettingItemVO,
            onSelectMethod = {}
        )
    }
}

@Composable
private fun SettingItem(
    modifier: Modifier,
    data: SettingItemVO<*>,
    iconModifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable (() -> Unit)
) {
    Card(
        modifier = modifier
            .clickable(
                enabled = true,
                onClick = onClick
            )
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(space = 24.dp)
        ) {
            Icon(
                modifier = iconModifier,
                imageVector = data.icon,
                contentDescription = data.icon.name
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = data.name)
                Text(
                    text = data.description,
                    style = MaterialTheme.typography.bodyMedium.merge(color = MaterialTheme.colorScheme.outline)
                )
            }
            content()
        }
    }
}

@Composable
@PreviewLightDark
private fun SettingItemPreview() {
    val fakeSettingItemVO = SettingItemVO(
        id = 1L,
        key = "theme",
        value = SettingOptions.Theme.DEFAULT,
        name = "主题模式",
        description = "切换主题外观",
        actionType = SettingActionTypeEnum.DIALOG,
        group = SettingGroupEnum.DISPLAY,
        icon = Icons.Default.Palette
    )

    NBApplicationTheme {
        SettingItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CardDefaults.shape)
                .background(MaterialTheme.colorScheme.background)
                .wrapContentHeight(),
            data = fakeSettingItemVO
        ) {}
    }
}
