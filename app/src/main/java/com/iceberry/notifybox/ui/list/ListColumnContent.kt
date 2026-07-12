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

package com.iceberry.notifybox.ui.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.itemKey
import com.iceberry.notifybox.R
import com.iceberry.notifybox.data.local.database.AppIcon
import com.iceberry.notifybox.ui.theme.NBApplicationTheme
import com.iceberry.notifybox.ui.theme.Red80
import com.iceberry.notifybox.util.RelativeTimeParser
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.min

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun ListColumnContent(
    modifier: Modifier = Modifier,
    state: ListColumnState<NotifyVO>,
    paddingValues: androidx.compose.foundation.layout.PaddingValues,
    onClickItem: (NotifyVO) -> Unit,
    onDeleteItem: (NotifyVO) -> Unit
) {
    val context = LocalContext.current
    val verticalArrangement by remember {
        derivedStateOf {
            if ((state.loading && state.query.isEmpty()) || state.items?.itemCount == 0) {
                Arrangement.Center
            } else {
                Arrangement.Top
            }
        }
    }
    // Requirement #3: 当切换页面（组件销毁）时，立即提交当前挂起的删除操作
    DisposableEffect(state.snackbarHostState) {
        onDispose {
            state.snackbarHostState.currentSnackbarData?.dismiss()
        }
    }
    LazyColumn(
        modifier = modifier
            .then(if (!state.query.isEmpty()) Modifier.imeNestedScroll() else Modifier),
        reverseLayout = false,
        verticalArrangement = verticalArrangement,
        state = state.lazyListState,
        contentPadding = paddingValues
    ) {
        if (state.loading && state.query.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .padding(vertical = 24.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    if (state.fromMBS) {
                        ContainedLoadingIndicator()
                    } else {
                        ContainedLoadingIndicator(modifier = Modifier.size(72.dp))
                    }
                }
            }
            return@LazyColumn
        }

//      if (items.loadState.refresh == LoadState.Loading) {
        if (state.items?.itemCount == 0) {
            item {
                Box(
                    modifier = Modifier
                        .padding(vertical = 24.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.list_no_messages),
                        style = MaterialTheme.typography.bodyMedium.merge(color = MaterialTheme.colorScheme.outline)
                    )
                }
            }
            return@LazyColumn
        }
//      }

        items(
            count = state.items?.itemCount ?: 0,
            key = state.items?.itemKey { it.key }
        ) { i ->
            state.items?.get(i)?.let {
                val deleted = remember { mutableStateOf(false) }
                AnimatedVisibility(
                    visible = !deleted.value,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    if (state.canDelete) {
                        SwipeToDeleteItem(
                            modifier = Modifier
                                .animateItem()
                                .fillMaxWidth(),
                            item = it,
                            query = state.query,
                            onClick = { onClickItem(it) },
                            onDelete = {
                                state.coroutineScope.launch {
                                    deleted.value = true
                                    state.snackbarHostState.currentSnackbarData?.dismiss()

                                    val result = state.snackbarHostState.showSnackbar(
                                        message = context.getString(R.string.list_delete_msg, it.count, it.appDisplayName),
                                        actionLabel = context.getString(R.string.recover),
                                        duration = SnackbarDuration.Long
                                    )

                                    when (result) {
                                        SnackbarResult.ActionPerformed -> {
                                            deleted.value = false
                                        }

                                        SnackbarResult.Dismissed -> {
                                            onDeleteItem(it)
                                        }
                                    }
                                }
                            }
                        )
                    } else {
                        NotifyItem(
                            modifier = modifier
                                .clip(CardDefaults.shape)
                                .background(MaterialTheme.colorScheme.background),
                            item = it,
                            query = state.query,
                            onClick = { onClickItem(it) }
                        )
                    }
                }
            }
        }

        if (state.items?.loadState?.append == LoadState.Loading) {
            item {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun SwipeToDeleteItem(
    modifier: Modifier,
    item: NotifyVO,
    query: String = "",
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(positionalThreshold = { it * .35f })

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            Timber.d("220 on delete")
            onDelete()
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color = when (dismissState.dismissDirection) {
                SwipeToDismissBoxValue.Settled -> Color.Transparent
                SwipeToDismissBoxValue.StartToEnd -> Color.Transparent
                SwipeToDismissBoxValue.EndToStart -> Red80
            }
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = MaterialTheme.colorScheme.onError
                )
            }
        },
        enableDismissFromStartToEnd = false
    ) {
        NotifyItem(
            modifier = modifier
                .clip(CardDefaults.shape)
                .background(MaterialTheme.colorScheme.background),
            item = item,
            query = query,
            onClick = onClick
        )
    }
}

@Composable
private fun NotifyItem(
    modifier: Modifier,
    item: NotifyVO,
    query: String = "",
    onClick: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        modifier = modifier.clickable {
            if (item.count <= 1) {
                isExpanded = !isExpanded
            } else {
                onClick()
            }
        }
    ) {
        Row(modifier = modifier.padding(all = 12.dp)) {
            BadgedBox(
                badge = {
                    val showBadge by remember { derivedStateOf { item.count > 1 } }
                    if (showBadge) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ) {
                            val countText by produceState("") {
                                snapshotFlow { item.count }
                                    .map { "${min(it, 99)}${if (it > 99) '+' else ""}" }
                                    .collect { value = it }
                            }

                            Text(countText)
                        }
                    }
                }
            ) {
                AppIcon(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(percent = 25))
                        .aspectRatio(1f),
                    icon = AppIcon(item.icon, item.largeIcon, item.appDisplayName)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Text(
                            text = buildHighlightedText(item.appDisplayName, query),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    val datetimeText by produceState("") {
                        snapshotFlow { item.createdTime }
                            .map { RelativeTimeParser.parse(item.createdTime, context) }
                            .collect { value = it }
                    }
                    Text(
                        text = datetimeText,
                        style = MaterialTheme.typography.bodySmall.merge(color = MaterialTheme.colorScheme.outline),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis

                    )
                }
                item.title?.let {
                    Text(
                        text = buildHighlightedText(it, query),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                item.content?.let {
                    Text(
                        text = buildHighlightedText(it, query),
                        maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.animateContentSize(),
                        style = MaterialTheme
                            .typography
                            .bodyMedium
                            .merge(color = MaterialTheme.colorScheme.outline),
                    )
                }
            }
        }
    }
}

@Composable
private fun buildHighlightedText(
    text: String,
    query: String,
): AnnotatedString {
    val highlightColor = MaterialTheme.colorScheme.secondaryContainer
    val highlightTextColor = MaterialTheme.colorScheme.primary

    return remember(text, query, highlightColor, highlightTextColor) {
        if (query.isEmpty() || !text.contains(query, ignoreCase = true)) {
            AnnotatedString(text)
        } else {
            buildAnnotatedString {
                var start = 0
                while (start < text.length) {
                    val index = text.indexOf(query, start, ignoreCase = true)
                    if (index == -1) {
                        append(text.substring(start))
                        break
                    }
                    append(text.substring(start, index))
                    withStyle(SpanStyle(background = highlightColor, color = highlightTextColor)) {
                        append(text.substring(index, index + query.length))
                    }
                    start = index + query.length
                }
            }
        }
    }
}

@Composable
internal fun AppIcon(
    modifier: Modifier = Modifier,
    icon: AppIcon
) {
    if (icon.icon == null) {
        DefaultAppIcon(modifier = modifier)
    } else {
        AdaptiveIconImage(
            drawable = icon.largeIcon ?: icon.icon,
            modifier = modifier,
            contentDescription = icon.appDisplayName
        )
    }
}

@Composable
internal fun DefaultAppIcon(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val drawable = remember(context) {
        context.packageManager.defaultActivityIcon
    }
    AdaptiveIconImage(
        drawable = drawable,
        modifier = modifier,
        contentDescription = stringResource(R.string.default_app_icon)
    )
}

@PreviewLightDark
@Composable
private fun DefaultAppIconPreview() {
    DefaultAppIcon(
        modifier = Modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(percent = 12))
    )
}

@PreviewLightDark
@Composable
private fun NotifyItemPreview() {
    NBApplicationTheme {
        NotifyItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CardDefaults.shape)
                .background(MaterialTheme.colorScheme.background)
                .wrapContentHeight(),
            item = fakeNotifyVOs.first(),
            onClick = {}
        )
    }
}