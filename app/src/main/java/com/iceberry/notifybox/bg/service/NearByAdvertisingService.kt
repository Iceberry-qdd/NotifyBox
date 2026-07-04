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
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import com.iceberry.notifybox.MainActivity
import com.iceberry.notifybox.R
import com.iceberry.notifybox.bg.event.NearbyConnectionRequestEvent
import com.iceberry.notifybox.bg.event.NearbyConnectionResponseEvent
import com.iceberry.notifybox.bg.event.NearbyConnectionResultEvent
import com.iceberry.notifybox.bg.event.NearbyPayloadEvent
import com.iceberry.notifybox.bg.event.NearbyPayloadTransferUpdateEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.util.concurrent.TimeUnit


private const val TAG = "AdvertiserService"
private val STRATEGY = Strategy.P2P_POINT_TO_POINT
private val NAERBY_NOTIFICATION_CHANNEL_ID = "nearby"
private const val FOREGROUND_NOTIFICATION_ID = 0x00251400

class NearByAdvertisingService : Service() {
    private var SERVICE_ID: String? = null
    private var connectionsClient: ConnectionsClient? = null
    private lateinit var localEndpointName: String

    private val payloadCallback: PayloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            Log.d(TAG, "onPayloadReceived(endpointId=$endpointId, payload=$payload)")
            EventBus.getDefault().post(NearbyPayloadEvent(endpointId, payload))
        }

        override fun onPayloadTransferUpdate(endpointId: String, payloadTransferUpdate: PayloadTransferUpdate) {
            Log.d(TAG, "onPayloadTransferUpdate(endpointId=$endpointId, payloadTransferUpdate=$payloadTransferUpdate)")
            EventBus.getDefault().post(NearbyPayloadTransferUpdateEvent(endpointId, payloadTransferUpdate))
        }
    }
    private var connectionLifecycleCallback: ConnectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            Timber.tag(TAG).i("onConnectionInitiated(endpointId=$endpointId, info=${info.endpointName})")
            EventBus.getDefault().post(NearbyConnectionRequestEvent(endpointId, info))
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    Timber.tag(TAG).i("Connection established with $endpointId")
                    EventBus.getDefault().post(NearbyConnectionResultEvent(endpointId, true))
                }

                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    Timber.tag(TAG).w("Connection rejected by $endpointId")
                    EventBus.getDefault().post(NearbyConnectionResultEvent(endpointId, false))
                }

                ConnectionsStatusCodes.STATUS_ERROR -> {
                    Timber.tag(TAG).e("Connection error with $endpointId")
                    EventBus.getDefault().post(NearbyConnectionResultEvent(endpointId, false))
                }

                else -> {
                    Timber.tag(TAG).d("Connection result: ${result.status.statusCode}")
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            Timber.tag(TAG).i("Disconnected from $endpointId")
            EventBus.getDefault().post(NearbyConnectionResultEvent(endpointId, false))
        }
    }
    private var handler: Handler = Handler(Looper.myLooper()!!)


    /**
     * Length of time to allow advertising before automatically shutting off. (10 minutes)
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
        )
        val nManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nManager.createNotificationChannel(bleNotificationChannel)

        val nBuilder = Notification.Builder(this, NAERBY_NOTIFICATION_CHANNEL_ID)
        val notification = nBuilder.setContentTitle(getString(R.string.service_nearby_devices))
            .setContentText(getString(R.string.service_advertising))
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

    private fun startAdvertising() {
        val advertisingOptions = AdvertisingOptions.Builder()
            .setStrategy(STRATEGY)
            .build()

        connectionsClient?.startAdvertising(localEndpointName, SERVICE_ID!!, connectionLifecycleCallback, advertisingOptions)
            ?.addOnSuccessListener {
                Timber.tag(TAG).i("Advertising started successfully")
            }
            ?.addOnFailureListener {
                Timber.tag(TAG).e(it, "Advertising start failed")
            }
    }

    private fun stopAdvertising() = connectionsClient?.stopAdvertising()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startAdvertising()
        handler.postDelayed({
            stopAdvertising()
            stopSelf()
        }, TIMEOUT)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        handler.removeCallbacksAndMessages(null)
        stopAdvertising()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNearbyConnectionResponse(event: NearbyConnectionResponseEvent) {
        if (event.accept) {
            connectionsClient?.acceptConnection(event.endpointId, payloadCallback)
        } else {
            connectionsClient?.rejectConnection(event.endpointId)
        }
    }
}