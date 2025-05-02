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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Square
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.room.FtsOptions
import com.iceberry.notifybox.R
import com.iceberry.notifybox.navigation.ListRoute
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
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val items = viewModel.pager.collectAsLazyPagingItems()

    val listState = rememberListColumnState(
        items = null,
        lazyListState = rememberLazyListState(),
        loading = uiState.loading,
        snackbarHostState = appState.snackbarHostState
    )

    ListColumnContent(
        modifier = modifier,
        state = listState,
        items = items,
        onClickItem = onClickItem,
        onDeleteItem = { notify ->
            scope.launch {
                viewModel.markDeletedByGroup(notify.notifyId, notify.appPackageName)
            }
        }
    )
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
fun AdaptiveIconImage(
    drawable: Drawable,
    modifier: Modifier,
    contentDescription: String?
) {
    val bitmap = createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight).applyCanvas {
        drawable.setBounds(0, 0, this.width, this.height)
        drawable.draw(this)
    }

    val imageBitmap = remember { bitmap.asImageBitmap() }
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
                    Icon(Icons.Filled.Search, "搜索")
                }
                IconButton(onClick = routeToStatistic) {
                    Icon(Icons.Filled.PieChart, "统计分析")
                }
                ListTopAppBarMenu(orderBy = orderBy)
            },
            modifier = modifier,
            scrollBehavior = scrollBehavior,
            windowInsets = windowInsets
        )
    }
}

@Composable
fun ListTopAppBarMenu(
    orderBy: (FtsOptions.Order, String) -> Unit
) {
    TopAppBarDropdownMenu(
        iconContent = { Icon(Icons.Filled.MoreVert, "更多选项") }
    ) { closeMenu ->
        DropdownMenuItem(
            text = { Text("按日期正序") },
            onClick = { orderBy(FtsOptions.Order.ASC, "time"); closeMenu() },
        )
        DropdownMenuItem(
            text = { Text("按日期倒序") },
            onClick = { orderBy(FtsOptions.Order.DESC, "time"); closeMenu() },
        )
        DropdownMenuItem(
            text = { Text("按包名正序") },
            onClick = { orderBy(FtsOptions.Order.ASC, "package"); closeMenu() },
        )
        DropdownMenuItem(
            text = { Text("按包名正序") },
            onClick = { orderBy(FtsOptions.Order.DESC, "package"); closeMenu() },
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

    if (currentDestination?.hasRoute(ListRoute::class) == true) {
        FloatingActionButton(
            onClick = {
                if (uiState.isServiceRunning) {
                    viewModel.stopANLService(context)
                } else {
                    viewModel.startANLService(context)
                }
            }
        ) {
            if (uiState.isServiceRunning) {
                Icon(Icons.Filled.Square, "停止监听")
            } else {
                Icon(Icons.Filled.PlayArrow, "启动监听")
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun ListTopAppBarDropdownMenuDarkPreview() {
    NBApplicationTheme {
        ListTopAppBarMenu(
            orderBy = { order, field -> {} }
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