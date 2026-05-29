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

package com.iceberry.notifybox.ui.log

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iceberry.notifybox.ui.log.TimberFlow.logs
import com.iceberry.notifybox.ui.theme.Green500
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LogViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    private val _logLevel = MutableStateFlow<Int?>(null)

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val uiState: StateFlow<LogUiState> = combine(
        logs, _isLoading, _logLevel
    ) { logs, isLoading, logLevel ->
        val filteredLogs = logs.filter { logLevel == null || it.level.priority >= logLevel }
        
        if (isLoading) {
            return@combine LogUiState(isLoading = true, level = logLevel)
        }

        if (filteredLogs.isEmpty()) {
            return@combine LogUiState(isLoading = false, level = logLevel, isSuccess = true)
        }

        return@combine LogUiState(isLoading = false, level = logLevel, pager = filteredLogs)
    }
        .onEach { _isLoading.update { false } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = LogUiState()
        )

    fun toggleLogLevel(filter: Int?) = _logLevel.update { filter }
}

data class LogUiState(
    val isLoading: Boolean = true,
    val level: Int? = null,
    val isSuccess: Boolean = true,
    val pager: List<LogEvent> = listOf()
)

data class LogEvent(
    val level: LogLevel,
    val tag: String?,
    val message: String,
    val throwable: Throwable?,
    val timestamp: Long = System.currentTimeMillis()
)

enum class LogLevel(val priority: Int, val displayName: String, val color: Color) {
    VERBOSE(Log.VERBOSE, "VERBOSE", Color.Unspecified),
    DEBUG(Log.DEBUG, "DEBUG", Color.Unspecified),
    INFO(Log.INFO, "INFO", Green500),
    WARN(Log.WARN, "WARN", Color(0xFFFF9800)),
    ERROR(Log.ERROR, "ERROR", Color.Red),
    ASSERT(Log.ASSERT, "ASSERT", Color.Unspecified);

    companion object {
        fun parseFormPriority(priority: Int): LogLevel? = entries.firstOrNull { it.priority == priority }
    }
}