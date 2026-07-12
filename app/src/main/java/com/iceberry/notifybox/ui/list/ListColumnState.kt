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

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.paging.compose.LazyPagingItems
import kotlinx.coroutines.CoroutineScope


@Composable
fun <T : Any> rememberListColumnState(
    items: LazyPagingItems<T>? = null,
    lazyListState: LazyListState = rememberLazyListState(),
    loading: Boolean = false,
    query: String = "",
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    canDelete: Boolean = true,
    fromMBS: Boolean = false
): ListColumnState<T> {
    return remember(items, lazyListState, loading, snackbarHostState, coroutineScope) {
        ListColumnState(
            items = items,
            lazyListState = lazyListState,
            loading = loading,
            query = query,
            snackbarHostState = snackbarHostState,
            coroutineScope = coroutineScope,
            canDelete = canDelete,
            fromMBS = fromMBS
        )
    }
}

class ListColumnState<T : Any>(
    internal val items: LazyPagingItems<T>?,
    internal val lazyListState: LazyListState,
    internal val loading: Boolean,
    internal val query: String,
    internal val snackbarHostState: SnackbarHostState,
    internal val coroutineScope: CoroutineScope,
    internal val canDelete: Boolean,
    internal val fromMBS: Boolean
)
