/*
 * Copyright (c) 2025-2026 Iceberry-qdd.
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

package com.iceberry.notifybox.ui.setting.about

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.iceberry.notifybox.BuildConfig
import com.iceberry.notifybox.ui.list.AppIcon
import com.iceberry.notifybox.ui.list.DefaultAppIcon
import com.iceberry.notifybox.ui.theme.NBApplicationTheme
import com.iceberry.notifybox.util.IconUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutContent(
    modifier: Modifier = Modifier,
    uiState: AboutUiState,
    lazyListState: LazyListState
) {
    val context = LocalContext.current
    val buildTime = BuildConfig.BUILD_TIME.toString()
    val buildType = BuildConfig.BUILD_TYPE

    val infoItems = remember(buildTime, uiState) {
        listOf(
            "应用版本" to "${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})",
            "构建时间" to buildTime,
            "构建版本" to BuildConfig.GIT_HASH,
            "构建类型" to buildType,
            "so库版本" to uiState.abi,
            "安装来源" to uiState.installSource,
            "db版本" to uiState.dbVersion
        )
    }

    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        state = lazyListState
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val appIcon = remember { IconUtils.getPackageIcon(context.packageName, context) }
                if (appIcon != null) {
                    AppIcon(
                        modifier = Modifier
                            .requiredSize(80.dp)
                            .clip(RoundedCornerShape(percent = 25)),
                        icon = com.iceberry.notifybox.data.local.database.AppIcon(icon = appIcon)
                    )
                } else {
                    DefaultAppIcon(
                        modifier = Modifier
                            .requiredSize(80.dp)
                            .clip(RoundedCornerShape(percent = 25))
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "通知盒子",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        items(infoItems.size) { index ->
            AboutInfoCard(
                label = infoItems[index].first,
                value = infoItems[index].second
            )
            if (index < infoItems.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AboutInfoCard(label: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* 点击反馈 */ },
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun AboutContentPreview() {
    NBApplicationTheme {
        AboutContent(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surface),
            uiState = AboutUiState(
                isLoading = false,
                dbVersion = "5",
                abi = "arm64-v8a",
                installSource = "play"
            ),
            lazyListState = rememberLazyListState()
        )
    }
}
