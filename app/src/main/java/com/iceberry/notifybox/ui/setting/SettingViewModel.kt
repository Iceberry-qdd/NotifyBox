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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.BatteryStd
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material.icons.filled.Translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.iceberry.notifybox.BuildConfig
import com.iceberry.notifybox.data.SettingsRepository
import com.iceberry.notifybox.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _loading = MutableStateFlow(true)
    private val _state2 = MutableStateFlow(true)

    @OptIn(ExperimentalCoroutinesApi::class)
    val pager = settingsRepository.settings
        .mapLatest {
            listOf(
                SettingItemVO(
                    id = 1L,
                    key = "theme",
                    value = it.darkThemeConfig,
                    name = "主题模式",
                    actionType = SettingActionTypeEnum.DIALOG,
                    group = SettingGroupEnum.DISPLAY,
                    icon = Icons.Default.Palette
                ),
                SettingItemVO(
                    id = 2L,
                    key = "language",
                    value = it.languageConfig,
                    name = "语言",
                    actionType = SettingActionTypeEnum.DIALOG,
                    group = SettingGroupEnum.DISPLAY,
                    icon = Icons.Default.Translate
                ),
                SettingItemVO(
                    id = 3L,
                    key = "disable_battery_optimization",
                    value = SettingOptions.Bool.FALSE,
                    name = "忽略电池优化",
                    description = "请设置为无限制，常驻后台运行",
                    actionType = SettingActionTypeEnum.INTENT,
                    group = SettingGroupEnum.DATA_PERMISSION,
                    icon = Icons.Default.BatteryStd
                ),
                SettingItemVO(
                    id = 4L,
                    key = "auto_startup",
                    value = SettingOptions.Bool.FALSE,
                    name = "允许自启动",
                    description = "请设置为允许自启动，以确保正常运行",
                    actionType = SettingActionTypeEnum.INTENT,
                    group = SettingGroupEnum.DATA_PERMISSION,
                    icon = Icons.Default.Autorenew
                ),
                SettingItemVO(
                    id = 5L,
                    key = "reload_icon",
                    value = SettingOptions.Nop.NOP,
                    name = "重载应用图标",
                    description = "重新加载应用图标",
                    actionType = SettingActionTypeEnum.CUSTOM,
                    group = SettingGroupEnum.DATA_PERMISSION,
                    icon = Icons.Default.Refresh
                ),
                SettingItemVO(
                    id = 6L,
                    key = "log",
                    value = SettingOptions.Bool.FALSE,
                    name = "日志",
                    description = "记录应用日志",
                    actionType = SettingActionTypeEnum.SWITCH,
                    group = SettingGroupEnum.DATA_PERMISSION,
                    icon = Icons.AutoMirrored.Filled.Article
                ),
                SettingItemVO(
                    id = 7L,
                    key = "export",
                    value = SettingOptions.Nop.NOP,
                    name = "导出数据",
                    description = "导出数据用于备份",
                    actionType = SettingActionTypeEnum.CUSTOM,
                    group = SettingGroupEnum.BACKUP,
                    icon = Icons.Default.Backup
                ),
                SettingItemVO(
                    id = 8L,
                    key = "restore",
                    value = SettingOptions.Nop.NOP,
                    name = "导入数据",
                    description = "导入已有备份数据",
                    actionType = SettingActionTypeEnum.CUSTOM,
                    group = SettingGroupEnum.BACKUP,
                    icon = Icons.Default.SettingsBackupRestore
                ),
                SettingItemVO(
                    id = 9L,
                    key = "about",
                    value = SettingOptions.Nop.NOP,
                    name = "关于",
                    description = BuildConfig.VERSION_NAME,
                    actionType = SettingActionTypeEnum.NAVIGATE,
                    group = SettingGroupEnum.ABOUT,
                    icon = Icons.Default.Info
                )
            )
        }
        .map { it.groupBy { item -> item.group } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = mapOf(Pair(SettingGroupEnum.ABOUT, listOf()))
        )

    val uiState: StateFlow<SettingUiState> = combine(
        _loading, _state2
    ) { loading, state2 ->
        SettingUiState(
            loading = loading,
            state2 = state2
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = SettingUiState()
    )

    fun setDialogOption(option: Enumeration) {
        when (option) {
            is SettingOptions.Theme -> {
                viewModelScope.launch {
                    settingsRepository.setDarkThemeConfig(option)
                }
            }

            is SettingOptions.Language -> {
                viewModelScope.launch {
                    settingsRepository.setLanguageConfig(option)
                }
            }

            else -> {}
        }
    }
}

data class SettingUiState(
    val loading: Boolean = false,
    val state2: Boolean = false,
)

data class SettingItemVO<T>(
    val id: Long,
    val key: String,
    val name: String = key,
    val value: T,
    val description: String = value.displayName,
    val actionType: SettingActionTypeEnum,
    val actionOptions: List<T> = value.javaClass.enumConstants?.toList() ?: listOf(),
    val group: SettingGroupEnum,
    val icon: ImageVector = Icons.Default.Settings
) where T : Enum<T>, T : Enumeration

data class NavigateData(
    val route: Route = Route.NopRoute,
    val navOptions: NavOptions? = null
)

interface Enumeration {
    val displayName: String
}

enum class SettingGroupEnum(override val displayName: String) : Enumeration {
    DISPLAY("界面与显示"), DATA_PERMISSION("数据与权限"), BACKUP("备份与恢复"), ABOUT("关于")
}

enum class SettingActionTypeEnum { SWITCH, DIALOG, LINK, NAVIGATE, INTENT, PLAIN, CUSTOM }

object SettingOptions {
    enum class Nop(override val displayName: String) : Enumeration { NOP(""); }

    enum class Bool(val bool: Boolean, override val displayName: String) : Enumeration {
        TRUE(true, "已启用"), FALSE(false, "已禁用");
    }

    enum class Theme(override val displayName: String) : Enumeration {
        DEFAULT("跟随系统"), LIGHT("浅色模式"), NIGHT("深色模式");
    }

    enum class Language(override val displayName: String) : Enumeration {
        DEFAULT("跟随系统"), ZH_CN("简体中文"), ZH_TW("繁體中文"), EN_US("English");
    }

    enum class ExportFormat(override val displayName: String) : Enumeration {
        CSV("CSV"), ZIP("ZIP")
    }

    enum class ImportMethod(override val displayName: String) : Enumeration {
        OVERRIDE("覆盖"), MERGE("合并")
    }
}
