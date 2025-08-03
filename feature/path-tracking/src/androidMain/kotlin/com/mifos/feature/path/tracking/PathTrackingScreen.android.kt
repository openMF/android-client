/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
@file:OptIn(
    ExperimentalMaterial3Api::class,
)

package com.mifos.feature.path.tracking

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Geocoder
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.registerReceiver
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.mifos.core.common.utils.Constants
import com.mifos.core.model.objects.users.UserLatLng
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun PathTrackingScreen(
    onBackPressed: () -> Unit,
    viewModel: PathTrackingViewModel,
) {
    val context = LocalContext.current
    val state by viewModel.pathTrackingUiState.collectAsStateWithLifecycle()
    val refreshState by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val userStatus by viewModel.userStatus.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        val notificationReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (Constants.STOP_TRACKING == action) {
                    viewModel.loadPathTracking()
                }
            }
        }
        registerReceiver(
            context,
            notificationReceiver,
            IntentFilter(Constants.STOP_TRACKING),
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )

        onDispose {
            context.unregisterReceiver(notificationReceiver)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadPathTracking()
    }

    PathTrackingScreen(
        state = state,
        onBackPressed = onBackPressed,
        onRetry = {
            viewModel.loadPathTracking()
        },
        onPathTrackingClick = { userLatLngs ->
            val uri = if (userLatLngs.isNotEmpty()) {
                val originLatLng = userLatLngs[0]
                val destinationLatLng = userLatLngs[userLatLngs.size - 1]
                "https://maps.google.com/maps?f=d&hl=en&saddr=${originLatLng.lat},${originLatLng.lng}" +
                    "&daddr=${destinationLatLng.lat},${destinationLatLng.lng}"
            } else {
                // Handle the case when userLatLngs is empty
                ""
            }

            val intent = Intent(Intent.ACTION_VIEW, uri.toUri())
            intent.setClassName(
                "com.google.android.apps.maps",
                "com.google.android.maps.MapsActivity",
            )
            startActivity(context, Intent.createChooser(intent, "Start Tracking"), null)
        },
        onRefresh = {
            viewModel.refreshCenterList()
        },
        refreshState = refreshState,
        userStatus = userStatus ?: false,
        updateUserStatus = { viewModel.updateUserStatus(it) },
    )
}

@Composable
actual fun PathTrackingMapView(latLngList: List<UserLatLng>) {
    if (latLngList.isEmpty()) return
    val latLng = latLngList[0]
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(latLng.lat, latLng.lng), 15f)
    }
    val uiSettings by remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = true)) }

    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        cameraPositionState = cameraPositionState,
        uiSettings = uiSettings,
    ) {
        if (latLngList.isNotEmpty()) {
            val startPoint = latLngList.first()
            val endPoint = latLngList.last()
            val context = LocalContext.current
            Marker(
                state = MarkerState(position = LatLng(startPoint.lat, startPoint.lng)),
                title = getAddressFromLatLng(
                    lat = startPoint.lat,
                    lng = startPoint.lng,
                    context = context,
                )?.trim(),
                draggable = true,
            )

            if (latLngList.size > 1) {
                Marker(
                    state = MarkerState(position = LatLng(endPoint.lat, endPoint.lng)),
                    title = getAddressFromLatLng(
                        lat = endPoint.lat,
                        lng = endPoint.lng,
                        context = context,
                    )?.trim(),
                    draggable = true,
                )

                Polyline(
                    points = latLngList.map { LatLng(it.lat, it.lng) },
                    color = MaterialTheme.colorScheme.primary,
                    width = 10f,
                )
            }
        }
    }
}

private fun getAddressFromLatLng(context: Context, lat: Double, lng: Double): String? {
    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(lat, lng, 1)
        addresses?.firstOrNull()?.getAddressLine(0)
    } catch (e: Exception) {
        null
    }
}
