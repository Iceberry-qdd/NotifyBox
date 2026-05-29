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

package com.iceberry.notifybox.ui.setting.export

import android.content.Context
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.PhonelinkSetup
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iceberry.notifybox.bg.event.NearbyConnectAction
import com.iceberry.notifybox.bg.event.NearbyConnectionRequestEvent
import com.iceberry.notifybox.bg.event.NearbyConnectionResponseEvent
import com.iceberry.notifybox.bg.event.NearbyConnectionResultEvent
import com.iceberry.notifybox.bg.event.NearbyEndpointFoundEvent
import com.iceberry.notifybox.bg.event.NearbyEndpointLostEvent
import com.iceberry.notifybox.bg.event.NearbyPayloadEvent
import com.iceberry.notifybox.bg.event.NearbyPayloadTransferUpdateEvent
import com.iceberry.notifybox.bg.service.NearByAdvertisingService
import com.iceberry.notifybox.bg.service.NearByDiscoverService
import com.iceberry.notifybox.data.NearbyTransferManager
import com.iceberry.notifybox.data.local.database.Device
import com.iceberry.notifybox.data.local.database.DeviceDao
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ExportViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val deviceDao: DeviceDao,
    private val transferManager: NearbyTransferManager
) : ViewModel() {
    private val _loading = MutableStateFlow(true)
    private val _scanning = MutableStateFlow(false)
    private val _isImporting = MutableStateFlow(false)
    private val _permissionsState = MutableStateFlow(PermissionsState.RATIONALE)
    private val _discoveredDevices = MutableStateFlow<Map<String, DeviceInfo>>(emptyMap())

    val pager: StateFlow<List<DeviceInfo>> = combine(
        deviceDao.getAllDevices(),
        _discoveredDevices,
        transferManager.transferProgress
    ) { cached, discovered, progress ->
        _loading.update { false }
        
        // Merge cached and discovered
        val allDevices = (cached.map { device ->
            val disc = discovered.values.find { it.deviceUniqueId == device.deviceUniqueId }
            DeviceInfo(
                endpointId = disc?.endpointId ?: "",
                deviceUniqueId = device.deviceUniqueId,
                name = device.name,
                description = device.description,
                connectState = disc?.connectState ?: ConnectState.NOT_CONNECT,
                transferState = disc?.transferState ?: TransferState.TRANSFER_INIT,
                transferProgress = disc?.transferProgress ?: 0f,
                isDiscovered = disc != null
            )
        } + discovered.values.filter { disc -> cached.none { it.deviceUniqueId == disc.deviceUniqueId } }.map {
            it.copy(isDiscovered = true)
        }).map { info ->
            if (progress != null && (progress.endpointId == info.endpointId || progress.endpointId == info.deviceUniqueId)) {
                info.copy(
                    transferState = if (progress.isCompleted) {
                        if (progress.error != null) TransferState.TRANSFER_FAILED else TransferState.TRANSFERRED
                    } else TransferState.TRANSFERRING,
                    transferProgress = progress.progress,
                    description = progress.error ?: info.description
                )
            } else info
        }

        // Current discovered first
        allDevices.sortedWith(compareByDescending<DeviceInfo> { it.isDiscovered }.thenByDescending { it.lastSeen })
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val uiState: StateFlow<ExportUiState> = combine(
        _loading, _scanning, _permissionsState, _isImporting
    ) { loading, scanning, permissionsState, isImporting ->
        ExportUiState(
            loading = loading || isImporting,
            scanning = scanning,
            permissionsState = permissionsState
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = ExportUiState()
    )

    init {
        EventBus.getDefault().register(this)
        viewModelScope.launch {
            deviceDao.getAllDevices().collect { cached ->
                _discoveredDevices.update { current ->
                    val updated = current.toMutableMap()
                    cached.forEach { device ->
                        if (!updated.containsKey(device.deviceUniqueId)) {
                             // This is just a placeholder to ensure the merge logic in pager has access to all info
                        }
                    }
                    updated
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEndpointFound(event: NearbyEndpointFoundEvent) {
        val info = DeviceInfo(
            endpointId = event.endpointId,
            deviceUniqueId = event.deviceUniqueId,
            name = event.info.endpointName.split("|").getOrNull(0) ?: event.info.endpointName,
            description = "附近设备",
            isDiscovered = true,
            lastSeen = System.currentTimeMillis()
        )
        _discoveredDevices.update { it + (event.deviceUniqueId to info) }
        viewModelScope.launch {
            deviceDao.insertDevice(Device(
                deviceUniqueId = event.deviceUniqueId,
                endpointId = event.endpointId,
                name = info.name,
                description = info.description,
                lastSeen = info.lastSeen
            ))
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEndpointLost(event: NearbyEndpointLostEvent) {
        _discoveredDevices.update { current ->
            current.values.find { it.endpointId == event.endpointId }?.let { 
                current - it.deviceUniqueId
            } ?: current
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNearbyConnectionResult(event: NearbyConnectionResultEvent) {
        _discoveredDevices.update { devices ->
            val device = devices.values.find { it.endpointId == event.endpointId }
            if (device != null) {
                Timber.tag("ExportViewModel").i("Connection result for ${device.name}: ${event.isConnected}")
                devices + (device.deviceUniqueId to device.copy(
                    connectState = if (event.isConnected) ConnectState.CONNECTED else ConnectState.CONNECT_FAILED
                ))
            } else {
                Timber.tag("ExportViewModel").w("Connection result for unknown endpoint: ${event.endpointId}")
                devices
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNearbyPayload(event: NearbyPayloadEvent) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNearbyPayloadTransferUpdate(event: NearbyPayloadTransferUpdateEvent) {
    }

    fun enableScan() {
        viewModelScope.launch {
            _scanning.update { true }
            delay(3000L)
            _scanning.update { false }
        }
    }

    fun doConnect(deviceUniqueId: String) {
        _discoveredDevices.update { devices ->
            devices[deviceUniqueId]?.let { 
                Timber.tag("ExportViewModel").i("Connecting to ${it.name} (${it.endpointId})")
                EventBus.getDefault().post(NearbyConnectAction(it.endpointId))
                devices + (deviceUniqueId to it.copy(connectState = ConnectState.CONNECTING))
            } ?: devices
        }
    }

    fun doExport(deviceUniqueId: String) {
        viewModelScope.launch {
            val endpointId = _discoveredDevices.value[deviceUniqueId]?.endpointId ?: ""
            if (endpointId.isEmpty()) return@launch

            _discoveredDevices.update { devices ->
                devices[deviceUniqueId]?.let { 
                    devices + (deviceUniqueId to it.copy(transferState = TransferState.TRANSFERRING, transferProgress = 0f))
                } ?: devices
            }
            try {
                transferManager.sendData(endpointId)
                _discoveredDevices.update { devices ->
                    devices[deviceUniqueId]?.let { 
                        devices + (deviceUniqueId to it.copy(transferState = TransferState.TRANSFERRED, transferProgress = 1f))
                    } ?: devices
                }
            } catch (e: Exception) {
                _discoveredDevices.update { devices ->
                    devices[deviceUniqueId]?.let { 
                        devices + (deviceUniqueId to it.copy(transferState = TransferState.TRANSFER_FAILED))
                    } ?: devices
                }
            }
        }
    }


    fun changePermissionsState(state: PermissionsState) = _permissionsState.update { state }


    fun startNearByDiscovery() {
        if (_permissionsState.value != PermissionsState.GRANTED) return
        val intent = Intent(context, NearByDiscoverService::class.java)
        // Use startService instead of startForegroundService to avoid the strict 
        // "must call startForeground" crash if stopService is called immediately.
        // Since the app is in foreground, startService is allowed.
        context.startService(intent)
    }
    fun startNearByAdvertising() {
        if (_permissionsState.value != PermissionsState.GRANTED) return
        val intent = Intent(context, NearByAdvertisingService::class.java)
        context.startService(intent)
    }
    fun stopNearByDiscovery() {
        val intent = Intent(context, NearByDiscoverService::class.java)
        context.stopService(intent)
    }
    fun stopNearByAdvertising() {
        val intent = Intent(context, NearByAdvertisingService::class.java)
        context.stopService(intent)
    }
}

enum class ConnectState { NOT_CONNECT, CONNECTING, CONNECTED, CONNECT_FAILED }
enum class TransferState { TRANSFER_INIT, TRANSFERRING, TRANSFERRED, TRANSFER_FAILED }
enum class PermissionsState { GRANTED, DENIED, RATIONALE, LOCATION_DISABLED, BLUETOOTH_DISABLED }

data class DeviceInfo(
    val endpointId: String = "",
    val deviceUniqueId: String = "",
    val name: String = "未知设备",
    val description: String = "未知设备",
    val icon: ImageVector = Icons.Filled.PhonelinkSetup,
    val connectState: ConnectState = ConnectState.NOT_CONNECT,
    val transferState: TransferState = TransferState.TRANSFER_INIT,
    val transferProgress: Float = 0.0f,
    val isDiscovered: Boolean = false,
    val lastSeen: Long = 0L
) {
    val id: Long get() = deviceUniqueId.hashCode().toLong()
}


data class ExportUiState(
    val loading: Boolean = true,
    val scanning: Boolean = true,
    val isConnected: Boolean = true,
    val permissionsState: PermissionsState = PermissionsState.RATIONALE
)