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

package com.iceberry.notifybox.bg.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import com.iceberry.notifybox.MainActivity
import com.iceberry.notifybox.R
import com.iceberry.notifybox.bg.event.NearbyConnectAction
import com.iceberry.notifybox.bg.event.NearbyConnectionResultEvent
import com.iceberry.notifybox.bg.event.NearbyEndpointFoundEvent
import com.iceberry.notifybox.bg.event.NearbyEndpointLostEvent
import com.iceberry.notifybox.bg.event.NearbyPayloadEvent
import com.iceberry.notifybox.bg.event.NearbyPayloadTransferUpdateEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.util.concurrent.TimeUnit


class NearByDiscoverService : Service() {
    private val TAG = "DiscoverService"
    private val STRATEGY = Strategy.P2P_POINT_TO_POINT
    private lateinit var localEndpointName: String
    private var SERVICE_ID: String? = null
    private val NAERBY_NOTIFICATION_CHANNEL_ID = "nearby"
    private val FOREGROUND_NOTIFICATION_ID = 0x00251401
    private var connectionsClient: ConnectionsClient? = null

    private val payloadCallback: PayloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            EventBus.getDefault().post(NearbyPayloadEvent(endpointId, payload))
        }

        override fun onPayloadTransferUpdate(endpointId: String, payloadTransferUpdate: PayloadTransferUpdate) {
            EventBus.getDefault().post(NearbyPayloadTransferUpdateEvent(endpointId, payloadTransferUpdate))
        }
    }

    private val connectionLifecycleCallback = object : com.google.android.gms.nearby.connection.ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: com.google.android.gms.nearby.connection.ConnectionInfo) {
            Timber.tag(TAG).i("onConnectionInitiated(endpointId=$endpointId, info=${info.endpointName})")
            // Automatically accept for discoverer side if we initiated it?
            // Actually, Nearby Connections requires both sides to accept.
            // We'll let the user handle it via the request event if it was incoming, 
            // but here we initiated it, so we accept automatically.
            connectionsClient?.acceptConnection(endpointId, payloadCallback)
        }

        override fun onConnectionResult(
            endpointId: String,
            result: com.google.android.gms.nearby.connection.ConnectionResolution
        ) {
            val isConnected =
                result.status.statusCode == com.google.android.gms.nearby.connection.ConnectionsStatusCodes.STATUS_OK
            if (isConnected) {
                Timber.tag(TAG).i("Connection established with $endpointId")
            } else {
                Timber.tag(TAG).w("Connection failed with $endpointId: ${result.status.statusCode}")
            }
            EventBus.getDefault().post(NearbyConnectionResultEvent(endpointId, isConnected))
        }

        override fun onDisconnected(endpointId: String) {
            Timber.tag(TAG).i("Disconnected from $endpointId")
            EventBus.getDefault().post(NearbyConnectionResultEvent(endpointId, false))
        }
    }

    private var endpointDiscoveryCallback: EndpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            Log.d(TAG, "onEndpointFound: endpointId=$endpointId, info=${info.endpointName}")
            // Nearby Connections doesn't provide a persistent unique ID for devices in DiscoveredEndpointInfo.
            // However, the endpointName can be structured to include one.
            // For now, if we can't get a real one, we'll use the endpointName itself as a fallback for uniqueness.
            // Ideally, the advertiser should include a unique ID in the endpoint name.
            val parts = info.endpointName.split("|")
            val name = parts.getOrNull(0) ?: info.endpointName
            val uniqueId = parts.getOrNull(1) ?: name

            EventBus.getDefault().post(NearbyEndpointFoundEvent(endpointId, uniqueId, info))
        }

        override fun onEndpointLost(endpointId: String) {
            Log.d(TAG, "onEndpointLost: endpointId=$endpointId")
            EventBus.getDefault().post(NearbyEndpointLostEvent(endpointId))
        }
    }
    private var handler: Handler = Handler(Looper.myLooper()!!)


    /**
     * Length of time to allow Discovery before automatically shutting off. (10 minutes)
     */
    private val TIMEOUT: Long = TimeUnit.MILLISECONDS.convert(10, TimeUnit.MINUTES)

    override fun onCreate() {
        super.onCreate()
        goForeground()
        SERVICE_ID = applicationContext.packageName
        connectionsClient = Nearby.getConnectionsClient(applicationContext)
        val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        localEndpointName = "${android.os.Build.MODEL}|$androidId"
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        handler.removeCallbacksAndMessages(null);
        stopDiscovery()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNearbyConnectAction(action: NearbyConnectAction) {
        connectionsClient?.requestConnection(localEndpointName, action.endpointId, connectionLifecycleCallback)
            ?.addOnFailureListener {
                EventBus.getDefault().post(NearbyConnectionResultEvent(action.endpointId, false))
            }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun goForeground() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        val bleNotificationChannel = NotificationChannel(
            NAERBY_NOTIFICATION_CHANNEL_ID,
            getString(R.string.service_nearby_devices),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = getString(R.string.service_nearby_devices_description)
        }
        val nManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nManager.createNotificationChannel(bleNotificationChannel)

        val nBuilder = Notification.Builder(this, NAERBY_NOTIFICATION_CHANNEL_ID)
        val notification = nBuilder.setContentTitle(getString(R.string.service_nearby_devices))
            .setContentText(getString(R.string.service_discovering))
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(pendingIntent)
            .build()

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                startForeground(
                    FOREGROUND_NOTIFICATION_ID,
                    notification,
                    android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
                )
            } else {
                startForeground(FOREGROUND_NOTIFICATION_ID, notification)
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to start foreground service")
        }
    }

    private fun startDiscovery() {
        val discoveryOptions = DiscoveryOptions.Builder()
            .setStrategy(STRATEGY)
            .build()

        connectionsClient?.startDiscovery(SERVICE_ID!!, endpointDiscoveryCallback, discoveryOptions)
            ?.addOnSuccessListener {
                Timber.tag(TAG).i("Discovery started successfully")
            }
            ?.addOnFailureListener {
                Timber.tag(TAG).e(it, "Discovery start failed")
            }
    }

    private fun stopDiscovery() = connectionsClient?.stopDiscovery()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startDiscovery()
        handler.postDelayed({
            stopDiscovery()
            stopSelf()
        }, TIMEOUT)
        return START_NOT_STICKY
    }
}