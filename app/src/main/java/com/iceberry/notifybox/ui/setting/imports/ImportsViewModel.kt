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

package com.iceberry.notifybox.ui.setting.imports

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iceberry.notifybox.MainActivity
import com.iceberry.notifybox.R
import com.iceberry.notifybox.bg.event.NearbyConnectionRequestEvent
import com.iceberry.notifybox.bg.event.NearbyConnectionResponseEvent
import com.iceberry.notifybox.bg.event.NearbyConnectionResultEvent
import com.iceberry.notifybox.bg.event.NearbyPayloadEvent
import com.iceberry.notifybox.bg.event.NearbyPayloadTransferUpdateEvent
import com.iceberry.notifybox.bg.service.NearByAdvertisingService
import com.iceberry.notifybox.data.NearbyTransferManager
import com.iceberry.notifybox.data.NotifyRepository
import com.iceberry.notifybox.ui.setting.SettingOptions
import com.iceberry.notifybox.ui.setting.export.ConnectState
import com.iceberry.notifybox.ui.setting.export.DeviceInfo
import com.iceberry.notifybox.ui.setting.export.PermissionsState
import com.iceberry.notifybox.ui.setting.export.TransferState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject
import kotlin.system.exitProcess

@HiltViewModel
class ImportsViewModel @Inject constructor(
    private val notifyRepository: NotifyRepository,
    @param:ApplicationContext private val context: Context,
    private val transferManager: NearbyTransferManager
) : ViewModel() {
    private val _mode = MutableStateFlow(SettingOptions.ImportMethod.MERGE)
    private val _loading = MutableStateFlow(false)
    private val _permissionsState = MutableStateFlow(PermissionsState.RATIONALE)
    private val _deviceInfo = MutableStateFlow(DeviceInfo())
    private val _error = MutableStateFlow<String?>(null)
    private val _showRestartDialog = MutableStateFlow(false)

    val uiState: StateFlow<ImportsUiState> = combine(
        combine(_loading, _deviceInfo, _permissionsState) { loading, deviceInfo, permissionsState ->
            Triple(loading, deviceInfo, permissionsState)
        },
        combine(transferManager.transferProgress, _error, _showRestartDialog) { progress, error, showRestartDialog ->
            Triple(progress, error, showRestartDialog)
        }
    ) { (loading, deviceInfo, permissionsState), (progress, error, showRestartDialog) ->

        val updatedDeviceInfo = if (progress != null && progress.endpointId == deviceInfo.endpointId) {
            deviceInfo.copy(
                transferState = if (progress.isCompleted) {
                    if (progress.error != null) TransferState.TRANSFER_FAILED else TransferState.TRANSFERRED
                } else TransferState.TRANSFERRING,
                transferProgress = progress.progress,
                description = progress.error ?: deviceInfo.description
            )
        } else deviceInfo

        val isImportSuccess =
            progress?.isCompleted == true && progress.error == null && updatedDeviceInfo.transferState == TransferState.TRANSFERRED

        ImportsUiState(
            loading = loading,
            deviceInfo = updatedDeviceInfo,
            permissionsState = permissionsState,
            error = error ?: progress?.error,
            showRestartDialog = showRestartDialog || isImportSuccess
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = ImportsUiState()
    )

    init {
        EventBus.getDefault().register(this)
    }

    override fun onCleared() {
        super.onCleared()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNearbyConnectionRequest(event: NearbyConnectionRequestEvent) {
        _deviceInfo.update {
            DeviceInfo(
                endpointId = event.endpointId,
                name = event.info.endpointName,
                description = context.getString(R.string.export_connecting),
                connectState = ConnectState.CONNECTING
            )
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNearbyConnectionResult(event: NearbyConnectionResultEvent) {
        _deviceInfo.update { current ->
            if (current.endpointId == event.endpointId) {
                current.copy(
                    connectState = if (event.isConnected) ConnectState.CONNECTED else ConnectState.CONNECT_FAILED,
                    description = if (event.isConnected) context.getString(R.string.export_connect_success) else context.getString(
                        R.string.export_connect_failed
                    )
                )
            } else current
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNearbyPayload(event: NearbyPayloadEvent) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNearbyPayloadTransferUpdate(event: NearbyPayloadTransferUpdateEvent) {
    }

    fun getParams(mode: SettingOptions.ImportMethod) {
        _mode.update { mode }
        transferManager.isMergeMode = mode == SettingOptions.ImportMethod.MERGE
    }

    fun clearError() = _error.update { null }
    fun changePermissionsState(state: PermissionsState) = _permissionsState.update { state }

    fun acceptConnection() {
        val endpointId = _deviceInfo.value.endpointId
        if (endpointId.isNotEmpty()) {
            EventBus.getDefault().post(NearbyConnectionResponseEvent(endpointId, true))
        }
    }

    fun rejectConnection() {
        val endpointId = _deviceInfo.value.endpointId
        if (endpointId.isNotEmpty()) {
            EventBus.getDefault().post(NearbyConnectionResponseEvent(endpointId, false))
            _deviceInfo.update { DeviceInfo() }
        }
    }

    fun startNearByAdvertising() {
        val intent = Intent(context, NearByAdvertisingService::class.java)
        context.startForegroundService(intent)
    }

    fun stopNearByAdvertising() {
        val intent = Intent(context, NearByAdvertisingService::class.java)
        context.stopService(intent)
    }

    fun restartApp() {
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)
        exitProcess(0)
    }

    fun dismissRestartDialog() {
        _showRestartDialog.update { false }
    }
}

data class ImportsUiState(
    val loading: Boolean = false,
    val deviceInfo: DeviceInfo = DeviceInfo(),
    val permissionsState: PermissionsState = PermissionsState.RATIONALE,
    val error: String? = null,
    val showRestartDialog: Boolean = false
)
