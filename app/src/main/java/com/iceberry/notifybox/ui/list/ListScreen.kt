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

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Square
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.room.FtsOptions
import com.iceberry.notifybox.R
import com.iceberry.notifybox.navigation.Route
import com.iceberry.notifybox.ui.NBAppState
import com.iceberry.notifybox.ui.SearchTopBar
import com.iceberry.notifybox.ui.TopAppBarDropdownMenu
import com.iceberry.notifybox.ui.rememberNBAppState
import com.iceberry.notifybox.ui.theme.NBApplicationTheme
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    modifier: Modifier = Modifier,
    viewModel: ListViewModel,
    appState: NBAppState,
    onClickItem: (NotifyVO) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val items = viewModel.pager.collectAsLazyPagingItems()
    val lazyListState = appState.curRouteLazyListState ?: rememberLazyListState()

    val listState = rememberListColumnState(
        items = items,
        lazyListState = lazyListState,
        loading = uiState.loading,
        query = uiState.query,
        snackbarHostState = appState.snackbarHostState,
        coroutineScope = appState.coroutineScope
    )

    ListColumnContent(
        modifier = modifier,
        state = listState,
        onClickItem = onClickItem,
        onDeleteItem = { notify ->
            appState.coroutineScope.launch {
                viewModel.markDeletedByGroup(notify.notifyId, notify.appPackageName)
            }
        }
    )
}

@Composable
fun AdaptiveIconImage(
    drawable: Drawable,
    modifier: Modifier,
    contentDescription: String?
) {
    val fallbackSize = with(LocalDensity.current) { 48.dp.roundToPx().coerceAtLeast(1) }
    val width = drawable.intrinsicWidth.takeIf { it > 0 } ?: fallbackSize
    val height = drawable.intrinsicHeight.takeIf { it > 0 } ?: fallbackSize
    val imageBitmap = remember(drawable, width, height) {
        createBitmap(width, height).applyCanvas {
            drawable.setBounds(0, 0, this.width, this.height)
            drawable.draw(this)
        }.asImageBitmap()
    }

    Image(
        bitmap = imageBitmap,
        modifier = modifier,
        contentDescription = contentDescription
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListTopAppBar(
    modifier: Modifier = Modifier,
    viewModel: ListViewModel = hiltViewModel(),
    routeToStatistic: () -> Unit,
    orderBy: (FtsOptions.Order, String) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets
) {
    var showSearchBar by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (showSearchBar) {
        SearchTopBar(
            query = uiState.query,
            loading = uiState.loading,
            onQueryChanged = {
                Log.i("ListScreen", "call onQueryChanged: query = $it")
                viewModel.setQuery(it)
            },
            onBack = {
                viewModel.clearQuery()
                showSearchBar = false
            },
            onClearQuery = viewModel::clearQuery,
        )
    } else {
        TopAppBar(
            title = { Text(text = stringResource(R.string.app_name)) },
            actions = {
                IconButton(onClick = { showSearchBar = true }) {
                    Icon(Icons.Filled.Search, stringResource(R.string.list_search))
                }
                IconButton(onClick = routeToStatistic) {
                    Icon(Icons.Filled.PieChart, stringResource(R.string.list_statistic))
                }
                ListTopAppBarMenu(orderBy = uiState.orderBy)
            },
            modifier = modifier,
            scrollBehavior = scrollBehavior,
            windowInsets = windowInsets
        )
    }
}

@Composable
fun ListTopAppBarMenu(
    viewModel: ListViewModel = hiltViewModel(),
    orderBy: ListOrderBy
) {
    TopAppBarDropdownMenu(
        iconContent = { Icon(Icons.Filled.MoreVert, stringResource(R.string.list_more_options)) }
    ) { closeMenu ->
        DropdownMenuItem(
            leadingIcon = {
                if (orderBy.field == OrderField.TIME && orderBy.direction == OrderDirection.ASC) {
                    Icon(Icons.Default.Check, contentDescription = stringResource(R.string.common_selected))
                }
            },
            text = { Text(stringResource(R.string.sort_date_asc)) },
            onClick = {
                viewModel.setOrderBy(
                    ListOrderBy(
                        direction = OrderDirection.ASC,
                        field = OrderField.TIME
                    )
                ); closeMenu()
            },
        )
        DropdownMenuItem(
            leadingIcon = {
                if (orderBy.field == OrderField.TIME && orderBy.direction == OrderDirection.DESC) {
                    Icon(Icons.Default.Check, contentDescription = stringResource(R.string.common_selected))
                }
            },
            text = { Text(stringResource(R.string.sort_date_desc)) },
            onClick = {
                viewModel.setOrderBy(
                    ListOrderBy(
                        direction = OrderDirection.DESC,
                        field = OrderField.TIME
                    )
                ); closeMenu()
            },
        )
        DropdownMenuItem(
            leadingIcon = {
                if (orderBy.field == OrderField.PACKAGE && orderBy.direction == OrderDirection.ASC) {
                    Icon(Icons.Default.Check, contentDescription = stringResource(R.string.common_selected))
                }
            },
            text = { Text(stringResource(R.string.sort_pkg_asc)) },
            onClick = {
                viewModel.setOrderBy(
                    ListOrderBy(
                        direction = OrderDirection.ASC,
                        field = OrderField.PACKAGE
                    )
                ); closeMenu()
            },
        )
        DropdownMenuItem(
            leadingIcon = {
                if (orderBy.field == OrderField.PACKAGE && orderBy.direction == OrderDirection.DESC) {
                    Icon(Icons.Default.Check, contentDescription = stringResource(R.string.common_selected))
                }
            },
            text = { Text(stringResource(R.string.sort_pkg_desc)) },
            onClick = {
                viewModel.setOrderBy(
                    ListOrderBy(
                        direction = OrderDirection.DESC,
                        field = OrderField.PACKAGE
                    )
                ); closeMenu()
            },
        )
    }
}

@Composable
fun ListFloatActionBar(
    modifier: Modifier = Modifier,
    appState: NBAppState,
    viewModel: ListViewModel = hiltViewModel(),
    context: Context = LocalContext.current
) {
    val currentDestination = appState.currentDestination
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (currentDestination?.hasRoute(Route.ListRoute::class) == true) {
        ExtendedFloatingActionButton(
            onClick = {
                if (uiState.isServiceRunning) {
                    viewModel.stopANLService(context)
                } else {
                    viewModel.startANLService(context)
                }
            }
        ) {
            if (uiState.isServiceRunning) {
                Icon(Icons.Filled.Square, stringResource(R.string.list_stop_listening))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.list_stop_listening))
            } else {
                Icon(Icons.Filled.PlayArrow, stringResource(R.string.list_start_listening))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.list_start_listening))
            }
        }
    }
}

val fakeNotifyVOs = listOf<NotifyVO>(
    NotifyVO(
        id = 1,
        key = "group-1",
        icon = null,
        largeIcon = null,
        appDisplayName = "微信",
        appPackageName = "com.tencent.mm",
        createdTime = LocalDateTime.now().minusDays(1L),
        title = "新消息",
        content = "您有一条新消息",
        channel = "msg",
        notifyId = 1,
        count = 989
    )
)

@Composable
@PreviewLightDark
private fun ListTopAppBarDropdownMenuDarkPreview() {
    NBApplicationTheme {
        ListTopAppBarMenu(
            orderBy = ListOrderBy()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun ListTopAppBarLightPreview() {
    NBApplicationTheme {
        ListTopAppBar(
            routeToStatistic = {},
            orderBy = { a, b -> {} },
            scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
            windowInsets = TopAppBarDefaults.windowInsets
        )
    }
}

@PreviewLightDark
@Composable
private fun ListFloatActionBarPreview() {
    NBApplicationTheme {
        ListFloatActionBar(
            appState = rememberNBAppState()
        )
    }
}
