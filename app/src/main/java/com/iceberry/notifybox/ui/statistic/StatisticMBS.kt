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

package com.iceberry.notifybox.ui.statistic

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.iceberry.notifybox.ui.list.ListColumnContent
import com.iceberry.notifybox.ui.list.NotifyVO
import com.iceberry.notifybox.ui.list.rememberListColumnState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticModelBottomSheet(
    modifier: Modifier = Modifier,
    viewModel: MBSViewModel = hiltViewModel(),
    state: StatisticMBSState = rememberStatisticMBSState(),
    onClickItem: (NotifyVO) -> Unit,
    onDismissRequest: () -> Unit
) {
    val lazyListState: LazyListState = rememberLazyListState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val items = viewModel.pager.collectAsLazyPagingItems()
    val listState = rememberListColumnState<NotifyVO>(
        items = items,
        lazyListState = lazyListState,
        loading = uiState.loading,
        query = "",
        snackbarHostState = SnackbarHostState(),
        canDelete = false,
    )

    ModalBottomSheet(
        modifier = modifier,
        sheetState = state.sheetState,
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.background
    ) {
        val isEmpty by remember {
            derivedStateOf {
                items.itemCount == 0 || items.loadState == LoadState.Loading
            }
        }

        if (!isEmpty) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                text = "${items[0]?.appDisplayName}",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }

        ListColumnContent(
            state = listState,
            onClickItem = onClickItem,
            onDeleteItem = {}
        )
    }
}