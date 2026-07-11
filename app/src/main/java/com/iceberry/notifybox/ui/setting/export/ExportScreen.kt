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

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.core.location.LocationManagerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.iceberry.notifybox.R
import com.iceberry.notifybox.ui.ScrollToTopFab
import com.iceberry.notifybox.ui.theme.NBApplicationTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    modifier: Modifier = Modifier,
    viewModel: ExportViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior((rememberTopAppBarState()))
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val items by viewModel.pager.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()

    Scaffold(
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            val density = LocalDensity.current
            ExportTopAppBar(
                onBack = onBackClick,
                scrollBehavior = scrollBehavior,
                windowInsets = TopAppBarDefaults.windowInsets.exclude(
                    WindowInsets(
                        top = (TopAppBarDefaults.windowInsets.getTop(density) * scrollBehavior.state.collapsedFraction).toInt()
                    )
                ),
                onScan = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0, scrollOffset = 0)
                    }
                    viewModel.enableScan()
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            ScrollToTopFab(
                scrollBehavior = scrollBehavior,
                lazyListState = lazyListState
            )
        }
    ) { innerPadding ->
        var launchTrigger by remember { mutableIntStateOf(0) }
        NearByPermissionsEffect(
            launchTrigger = launchTrigger,
            onTriggerReset = { launchTrigger = 0 },
            onPermissionsChange = { viewModel.changePermissionsState(it) }
        )

        when (uiState.permissionsState) {
            PermissionsState.GRANTED -> {
                Log.i("120", "GRANTED")
                DisposableEffect(Unit) {
                    viewModel.startNearByDiscovery()
                    onDispose {
                        viewModel.stopNearByDiscovery()
                    }
                }
                ExportContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface),
                    list = items,
                    uiState = uiState,
                    lazyListState = lazyListState,
                    paddingValues = innerPadding,
                    doConnect = { viewModel.doConnect(it) },
                    doExport = { viewModel.doExport(it) }
                )
            }

            else -> {
                Log.i("137", "NOT GRANTED")
                NoNearByPermissionsContent(
                    modifier = Modifier
                        .padding(
                            start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                            end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                            top = innerPadding.calculateTopPadding()
                        )
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface),
                    permissionsState = uiState.permissionsState,
                    onPermissionsChange = { viewModel.changePermissionsState(it) },
                    onAuthorize = { launchTrigger++ }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportTopAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    onBack: () -> Unit,
    onScan: () -> Unit = {}
) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.export_title)) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
            }
        },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        windowInsets = windowInsets,
        actions = {
            IconButton(onClick = onScan) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.forward_rescan)
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun ExportTopAppBarLightPreview() {
    NBApplicationTheme {
        ExportTopAppBar(
            onBack = {},
            scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        )
    }
}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
internal fun NearByPermissionsEffect(
    onPermissionsChange: (PermissionsState) -> Unit,
    launchTrigger: Int = 0,
    onTriggerReset: () -> Unit = {}
) {
    val context = LocalContext.current
    if (LocalInspectionMode.current) return
//    if (VERSION.SDK_INT < VERSION_CODES.TIRAMISU) return
    val nearByPermissionsState = rememberMultiplePermissionsState(
        if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            listOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.NEARBY_WIFI_DEVICES,
                Manifest.permission.POST_NOTIFICATIONS,
            )
        } else if (VERSION.SDK_INT >= VERSION_CODES.S) {
            listOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        } else if (VERSION.SDK_INT >= VERSION_CODES.Q) {
            listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )
        } else {
            listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )
        }
    )

    fun checkAll(): PermissionsState {
        if (!nearByPermissionsState.allPermissionsGranted) {
            return if (nearByPermissionsState.shouldShowRationale) PermissionsState.RATIONALE else PermissionsState.DENIED
        }

        // Check Bluetooth
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val isBluetoothEnabled = bluetoothManager.adapter?.isEnabled == true
        if (!isBluetoothEnabled) return PermissionsState.BLUETOOTH_DISABLED

        // Check Location
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isLocationEnabled = LocationManagerCompat.isLocationEnabled(locationManager)
        if (!isLocationEnabled) return PermissionsState.LOCATION_DISABLED

        return PermissionsState.GRANTED
    }

    LaunchedEffect(launchTrigger) {
        if (launchTrigger > 0) {
            nearByPermissionsState.launchMultiplePermissionRequest()
            onTriggerReset()
        }
    }

    LaunchedEffect(nearByPermissionsState.allPermissionsGranted, nearByPermissionsState.shouldShowRationale) {
        onPermissionsChange(checkAll())
    }

    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                onPermissionsChange(checkAll())
            }
        }
        val filter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(LocationManager.PROVIDERS_CHANGED_ACTION)
        }
        context.registerReceiver(receiver, filter)
        onDispose {
            context.unregisterReceiver(receiver)
        }
    }
}
