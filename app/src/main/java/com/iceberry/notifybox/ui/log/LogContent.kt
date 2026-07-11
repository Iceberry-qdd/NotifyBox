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

package com.iceberry.notifybox.ui.log

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iceberry.notifybox.R
import com.iceberry.notifybox.ui.theme.NBApplicationTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogContent(
    modifier: Modifier = Modifier,
    uiState: LogUiState,
    paddingValues: androidx.compose.foundation.layout.PaddingValues,
    lazyListState: LazyListState,
    onToggleLogLevel: (Int?) -> Unit
) {
    LaunchedEffect(uiState.pager.size) {
        if (!lazyListState.isScrollInProgress && uiState.pager.isNotEmpty()) {
            lazyListState.animateScrollToItem(uiState.pager.lastIndex)
        }
    }

    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        contentPadding = paddingValues
    ) {

        if (uiState.isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillParentMaxHeight()
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            return@LazyColumn
        }

        if (uiState.pager.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillParentMaxHeight(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.log_empty),
                        style = MaterialTheme.typography.bodyMedium.merge(color = MaterialTheme.colorScheme.outline)
                    )
                }
            }
            return@LazyColumn
        }

        if (!uiState.isSuccess) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillParentMaxHeight(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.log_load_failed),
                        style = MaterialTheme.typography.bodyMedium.merge(color = MaterialTheme.colorScheme.outline)
                    )
                }
            }
            return@LazyColumn
        }

        items(uiState.pager) { log ->
            LogItem(
                modifier = modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                log = log
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogItem(
    modifier: Modifier = Modifier,
    log: LogEvent
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
            val annotatedString = buildAnnotatedString {
                val monoStyle = SpanStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                pushStyle(monoStyle)
                append("${LogFormatter.formatTimestamp(log.timestamp)} ")

                pushStyle(SpanStyle(color = log.level.color))
                append(log.level.displayName.padEnd(6))
                pop()

                append("${log.tag} ")
                pop()

                append(log.message)
            }

            Text(
                text = annotatedString,
                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium.merge(color = MaterialTheme.colorScheme.outline),
                lineHeight = 20.sp
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun LogItemPreview() {
    NBApplicationTheme {
        LogItem(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(MaterialTheme.colorScheme.background),
            log = LogEvent(
                level = LogLevel.ERROR,
                tag = "tag1",
                message = "这是一条日志消息",
                throwable = null,
                timestamp = Clock.System.now().toEpochMilliseconds()
            ),
        )
    }
}

object LogFormatter {
    // 预读格式化器
    private val logFormatter = DateTimeFormatter
        .ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        .withZone(ZoneId.systemDefault())

    fun formatTimestamp(timestamp: Long): String = logFormatter.format(Instant.ofEpochMilli(timestamp))
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun LogScreenPreview() {
    NBApplicationTheme {
        LogContent(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(MaterialTheme.colorScheme.surface),
            uiState = LogUiState(
                isLoading = false,
                pager = listOf(
                    LogEvent(
                        level = LogLevel.ERROR,
                        tag = "tag1",
                        message = "这是一条日志消息",
                        throwable = null,
                        timestamp = Clock.System.now().toEpochMilliseconds()
                    ),
                    LogEvent(
                        level = LogLevel.WARN,
                        tag = "tag1",
                        message = "这是一条日志消息",
                        throwable = null,
                        timestamp = Clock.System.now().toEpochMilliseconds()
                    ),
                    LogEvent(
                        level = LogLevel.INFO,
                        tag = "tag1",
                        message = "这是一条日志消息",
                        throwable = null,
                        timestamp = Clock.System.now().toEpochMilliseconds()
                    )
                )
            ),
            paddingValues = androidx.compose.foundation.layout.PaddingValues(0.dp),
            lazyListState = rememberLazyListState(),
            onToggleLogLevel = {},
        )
    }
}
//
//@PreviewLightDark
//@Composable
//private fun DateFilterChipsPreview() {
//    NBApplicationTheme {
//        DateFilterChips(
//            modifier = Modifier
//                .fillMaxWidth()
//                .wrapContentHeight()
//                .background(MaterialTheme.colorScheme.surface),
//            selectedChip = TimeRangeFilter.ALL,
//            onToggle = {}
//        )
//    }
//}