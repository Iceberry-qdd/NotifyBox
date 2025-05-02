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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iceberry.notifybox.data.NotifyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ForwardViewModel @Inject constructor(
    private val notifyRepository: NotifyRepository
) : ViewModel() {

    val uiState: StateFlow<Boolean> = MutableStateFlow(true)
//        .map{ it -> it}
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            false
        )

//    fun addStatistics(name: String) {
//        viewModelScope.launch {
//            notifyRepository.add(name)
//        }
//    }
}

//sealed interface StatisticsUiState {
//    object Loading : StatisticsUiState
//    data class Error(val throwable: Throwable) : StatisticsUiState
//    data class Success(val data: List<Notify>) : StatisticsUiState
//}