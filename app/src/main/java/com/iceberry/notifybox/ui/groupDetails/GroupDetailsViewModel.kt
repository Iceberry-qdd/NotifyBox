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

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.iceberry.notifybox.data.NotifyRepository
import com.iceberry.notifybox.ui.list.KeyPrefix
import com.iceberry.notifybox.ui.list.KeySuffix
import com.iceberry.notifybox.ui.list.mappingNotifies
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class GroupDetailsViewModel @Inject constructor(
    private val notifyRepository: NotifyRepository,
    @ApplicationContext context: Context
) : ViewModel() {
    private val _notifyId = MutableStateFlow<Int?>(null)
    private val _packageName = MutableStateFlow("")
    private val _loading = MutableStateFlow(false)
    private val _count = MutableStateFlow(0)
    private val pageConfig = PagingConfig(
        pageSize = 10,
        initialLoadSize = 30,
        enablePlaceholders = true,
        maxSize = 500
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val pager = combine(_notifyId, _packageName) { notifyId, packageName ->
        _loading.update { true }
        if (notifyId != null && !packageName.isEmpty()) {
            Pager(pageConfig) { notifyRepository.queryDetailByNotifyId(_notifyId.value, _packageName.value) }.flow
        } else {
            Pager(pageConfig) { NotifyRepository.EmptyPageSource() }.flow
        }
    }.flattenConcat()
        .map { notifies -> notifies.map(mappingNotifies(context, KeyPrefix.ITEM, KeySuffix.ID)) }
        .onEach {
            delay(500L)
            _loading.update { false }
        }
        .cachedIn(viewModelScope)

    val uiState: StateFlow<GroupDetailsUiState> = combine(
        _loading, _count
    ) { loading, count ->
        GroupDetailsUiState(
            loading = loading,
            count = count
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = GroupDetailsUiState()
    )

    fun getGroupData(notifyId: Int, packageName: String) {
        _notifyId.update { notifyId }
        _packageName.update { packageName }
    }

    suspend fun markDeletedById(id: Int) {
        notifyRepository.markDeletedById(id)
    }

    fun updateCount(count: Int) {
        _count.update { count }
    }
}

data class GroupDetailsUiState(
    val loading: Boolean = false,
    val count: Int = 0
)