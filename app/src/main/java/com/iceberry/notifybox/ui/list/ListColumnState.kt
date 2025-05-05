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
import androidx.paging.compose.LazyPagingItems


@Composable
fun <T : Any> rememberListColumnState(
    items: LazyPagingItems<T>?,
    lazyListState: LazyListState = rememberLazyListState(),
    loading: Boolean,
    snackbarHostState: SnackbarHostState
): ListColumnState<T> {
    return remember(items, lazyListState, loading, snackbarHostState) {
        ListColumnState(
            items = items,
            lazyListState = lazyListState,
            loading = loading,
            snackbarHostState = snackbarHostState
        )
    }
}

class ListColumnState<T : Any>(
    internal val items: LazyPagingItems<T>?,
    internal val lazyListState: LazyListState,
    internal val loading: Boolean,
    internal val snackbarHostState: SnackbarHostState
)