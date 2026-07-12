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

package com.iceberry.notifybox.bg.event

import com.google.android.gms.nearby.connection.ConnectionInfo
import com.iceberry.notifybox.util.EventBus

data class NearbyConnectionRequestEvent(
    val endpointId: String,
    val info: ConnectionInfo
) : EventBus.Event

data class NearbyConnectionResponseEvent(
    val endpointId: String,
    val accept: Boolean
) : EventBus.Event

data class NearbyConnectAction(
    val endpointId: String
) : EventBus.Event

data class NearbyConnectionResultEvent(
    val endpointId: String,
    val isConnected: Boolean
) : EventBus.Event

data class NearbyPayloadEvent(
    val endpointId: String,
    val payload: com.google.android.gms.nearby.connection.Payload
) : EventBus.Event

data class NearbyPayloadTransferUpdateEvent(
    val endpointId: String,
    val update: com.google.android.gms.nearby.connection.PayloadTransferUpdate
) : EventBus.Event

data class NearbyEndpointFoundEvent(
    val endpointId: String,
    val deviceUniqueId: String,
    val info: com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
) : EventBus.Event

data class NearbyEndpointLostEvent(
    val endpointId: String
) : EventBus.Event
