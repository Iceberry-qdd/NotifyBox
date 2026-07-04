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

package com.iceberry.notifybox.ui.groupDetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.iceberry.notifybox.R
import com.iceberry.notifybox.ui.NBAppState
import com.iceberry.notifybox.ui.ScrollToTopFab
import com.iceberry.notifybox.ui.list.ListColumnContent
import com.iceberry.notifybox.ui.list.rememberListColumnState
import com.iceberry.notifybox.ui.theme.NBApplicationTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: GroupDetailsViewModel = hiltViewModel(),
    appState: NBAppState,
    onBackClick: () -> Unit,
    notifyId: Int?,
    packageName: String
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lazyListState: LazyListState = rememberLazyListState()

    Scaffold(
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            val density = LocalDensity.current
            GroupDetailsTopAppBar(
                count = uiState.count,
                onBack = onBackClick,
                scrollBehavior = scrollBehavior,
                windowInsets = TopAppBarDefaults.windowInsets.exclude(
                    WindowInsets(
                        top = (TopAppBarDefaults.windowInsets.getTop(density) * scrollBehavior.state.collapsedFraction).toInt()
                    )
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            ScrollToTopFab(
                scrollBehavior = scrollBehavior,
                lazyListState = lazyListState
            )
        }
    ) { paddingValues ->
        if (notifyId == null) {
            Box(
                modifier = modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.group_details_empty),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        } else {
            val scope = rememberCoroutineScope()
            val params by rememberUpdatedState(Pair(notifyId, packageName))
            LaunchedEffect(params) {
                scope.launch {
                    viewModel.getGroupData(params.first, params.second)
                }
            }

            val items = viewModel.pager.collectAsLazyPagingItems()
            LaunchedEffect(items.itemCount) {
                scope.launch {
                    viewModel.updateCount(items.itemCount)
                }
            }

            val listState = rememberListColumnState(
                items = items,
                lazyListState = lazyListState,
                loading = uiState.loading,
                query = "",
                snackbarHostState = snackbarHostState,
                coroutineScope = appState.coroutineScope
            )

            ListColumnContent(
                modifier = Modifier
                    .padding(
                        start = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
                        end = paddingValues.calculateEndPadding(LocalLayoutDirection.current),
                        top = paddingValues.calculateTopPadding()
                    )
                    .fillMaxSize(),
                state = listState,
                onClickItem = {},
                onDeleteItem = { notify ->
                    appState.coroutineScope.launch {
                        viewModel.markDeletedById(notify.id)
                    }
                }
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailsTopAppBar(
    modifier: Modifier = Modifier,
    count: Int = 0,
    onBack: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets
) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.group_details_title, count)) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
            }
        },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        windowInsets = windowInsets
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun GroupDetailsTopAppBarLightPreview() {
    NBApplicationTheme {
        GroupDetailsTopAppBar(
            onBack = {},
            scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        )
    }
}