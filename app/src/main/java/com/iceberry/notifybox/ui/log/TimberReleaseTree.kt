/*
 * Copyright (c) 2026 Iceberry-qdd.
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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

object AppScope {
    val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.Default
    )
}

object TimberFlow {
    private val _logLevel = MutableStateFlow(LogLevel.VERBOSE)
    private val _actions = MutableSharedFlow<LogAction>(
        extraBufferCapacity = 100
    )

    val logLevel = _logLevel.asStateFlow()
    val logs = combine(
        _logLevel, _actions
    ) { logLevel, actions ->
        Pair(logLevel, actions)
    }
        .scan(emptyList<LogEvent>()) { acc, (logLevel, action) ->
//            Log.d("TimberFlow#51", "$logLevel -- ${(action as LogAction.Add).event} -- ${(action as LogAction.Add).event.level >= logLevel}")
            when (action) {
//                is LogAction.Add -> acc
//                    .filter {v -> v.level >= logLevel }
//                    .also {v -> if (action.event.level >= logLevel) v + action.event }
//                    .takeLast(200)

                is LogAction.Add -> (acc + action.event).takeLast(200)
                LogAction.Clear -> emptyList()
            }
        }
        .stateIn(
            scope = AppScope.scope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    fun emit(event: LogEvent) {
        AppScope.scope.launch {
            _logLevel.collectLatest { l ->
                if (event.level >= l) {
                    _actions.tryEmit(LogAction.Add(event))
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun clear() {
        _actions.resetReplayCache()
        _actions.tryEmit(LogAction.Clear)
    }

    fun changeLevel(level: LogLevel) {
        _logLevel.update { level }
    }
}

sealed class LogAction {
    data class Add(val event: LogEvent) : LogAction()
    object Clear : LogAction()
}

/** A tree which logs important information for crash reporting.  */
object TimberReleaseTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        TimberFlow.emit(
            LogEvent(LogLevel.parseFormPriority(priority) ?: LogLevel.DEBUG, tag, message, t)
        )
    }
}