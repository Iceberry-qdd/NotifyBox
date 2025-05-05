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
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.iceberry.notifybox.bg.service.AppNotificationListenerService
import com.iceberry.notifybox.data.NotifyRepository
import com.iceberry.notifybox.data.SettingsRepository
import com.iceberry.notifybox.data.local.database.Notify
import com.iceberry.notifybox.util.IconUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class ListViewModel @Inject constructor(
    private val notifyRepository: NotifyRepository,
    settingsRepository: SettingsRepository,
    @ApplicationContext context: Context
) : ViewModel() {
    private val _showANLSDialog = MutableStateFlow(false)
    private val _serviceState = settingsRepository.settings
    private val _query = MutableStateFlow("")
    private val _loading = MutableStateFlow(true)
    private val pageConfig = PagingConfig(
        pageSize = 10,
        initialLoadSize = 30,
        enablePlaceholders = true,
        maxSize = 500
    )

    init {
        viewModelScope.launch {
            _serviceState
                .map { it -> it.isANLSRunning }
                .collectLatest {
                    if (it) {
                        AppNotificationListenerService.showNotify(context)
                    } else {
                        AppNotificationListenerService.removeNotify(context)
                    }
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val pager = _query
        .debounce(300)
        .flatMapLatest { query ->
            _loading.update { true }
            Pager(pageConfig) {
                if (query.isEmpty()) notifyRepository.queryByPaging() else notifyRepository.searchByQuery(query)
            }.flow.map { notifies ->
                notifies.map(
                    mappingNotifies(
                        context,
                        if (query.isEmpty()) KeyPrefix.GROUP else KeyPrefix.ITEM,
                        if (query.isEmpty()) KeySuffix.NOTIFY_ID else KeySuffix.ID
                    )
                )
            }
        }
        .onEach {
            Log.i("ListViewModel#onEach", "_loading = ${_loading.value}, _query = [${_query.value}]")
            delay(500L)
            _loading.update { false }
        }
        .cachedIn(viewModelScope)

    val uiState: StateFlow<ListUiState> = combine(
        _showANLSDialog, _serviceState, _query, _loading
    ) { showANLSDialog, serviceState, query, loading ->
        ListUiState(
            isServiceRunning = serviceState.isANLSRunning,
            showANLSDialog = showANLSDialog,
            query = query,
            loading = loading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = ListUiState()
    )

    fun startANLService(context: Context) {
        if (!AppNotificationListenerService.hasPermission(context)) {
            Log.i("permission", "No notification listen permission, show dialog.")
            _showANLSDialog.update { true }
            return
        }

        if (!AppNotificationListenerService.isServiceRunning(context)) {
            val intent = Intent(context, AppNotificationListenerService::class.java)
            viewModelScope.launch {
                context.startService(intent)
//                AppNotificationListenerService.showNotify(context as Activity)
            }
        }
    }

    fun stopANLService(context: Context) {
        if (AppNotificationListenerService.isServiceRunning(context)) {
            val intent = Intent(context, AppNotificationListenerService::class.java)
            viewModelScope.launch {
                context.stopService(intent)
//                AppNotificationListenerService.removeNotify(context as Activity)
            }
        }
    }

    fun cancelANLSSettingsDialog() {
        _showANLSDialog.update { false }
    }

    fun confirmANSSettingsDialog(context: Context) {
        viewModelScope.launch {
            AppNotificationListenerService.openSystemSettings(context)
            _showANLSDialog.update { false }
        }
    }

    suspend fun markDeletedByGroup(notifyId: Int, packageName: String) {
        notifyRepository.markDeletedByGroup(notifyId, packageName)
    }

    fun setQuery(query: String) {
        _query.update { query }
    }

    fun clearQuery() {
        _query.update { "" }
    }
}

data class NotifyVO(
    val id: Int,
    val key: String, // LazyColumn key
    val icon: Drawable?,
    val largeIcon: Drawable?, // file name
    val appDisplayName: String,
    val appPackageName: String,
    val createdTime: LocalDateTime,
    val title: String?,
    val content: String?,
    val channel: String?,
    val notifyId: Int,
    val count: Int
)

data class ListUiState(
    val isServiceRunning: Boolean = false,
    val showANLSDialog: Boolean = false,
    val query: String = "",
    val loading: Boolean = false,
    val pager: PagingData<NotifyVO> = PagingData.empty<NotifyVO>()
)

//enum class ListType { GROUP, SEARCH }
enum class KeyPrefix { GROUP, ITEM }
enum class KeySuffix { NOTIFY_ID, ID }

internal fun mappingNotifies(context: Context, keyPrefix: KeyPrefix, keySuffix: KeySuffix): suspend (Notify) -> NotifyVO =
    { notify ->
        val largeIcon = notify.largeIcon
            ?.let { IconUtils.loadIconFormPath(it, context) }
            ?.let { IconUtils.convertToDrawable(it, context) }

        val smallIcon = notify.smallIcon
            ?.let { IconUtils.loadIconFormPath(it, context) }
            ?.let { IconUtils.convertToDrawable(it, context) }

        val packageIcon = smallIcon?.let {
            return@let IconUtils.getPackageIcon(notify.packageName, context)
        }

        @Suppress("LocalVariableName")
        val _keySuffix = when (keySuffix) {
            KeySuffix.ID -> notify.id
            KeySuffix.NOTIFY_ID -> notify.notifyId
        }

        NotifyVO(
            id = notify.id,
            key = "${keyPrefix}-${notify.packageName}-${_keySuffix}",
            icon = packageIcon ?: smallIcon,
            appDisplayName = notify.displayName ?: notify.packageName,
            appPackageName = notify.packageName,
            createdTime = notify.createdTime,
            title = notify.title,
            content = notify.content,
            largeIcon = largeIcon,
            channel = notify.channel,
            notifyId = notify.notifyId,
            count = notify.count
        )
    }
