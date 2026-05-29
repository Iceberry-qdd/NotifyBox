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

package com.iceberry.notifybox.ui.forward

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember


@Composable
fun rememberForwardColumnState(
    lazyListState: LazyListState = rememberLazyListState(),
    loading: Boolean = false,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
): ForwardColumnState {
    return remember(lazyListState, loading, snackbarHostState) {
        ForwardColumnState(
            lazyListState = lazyListState,
            loading = loading,
            snackbarHostState = snackbarHostState
        )
    }
}

class ForwardColumnState(
    internal val lazyListState: LazyListState,
    internal val loading: Boolean,
    internal val snackbarHostState: SnackbarHostState
)