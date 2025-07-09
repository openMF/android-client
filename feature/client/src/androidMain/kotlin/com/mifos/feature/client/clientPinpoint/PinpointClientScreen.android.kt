/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientPinpoint

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.mifos.core.model.objects.clients.ClientAddressRequest
import com.mifos.core.model.objects.clients.ClientAddressResponse
import kotlinx.coroutines.tasks.await

@Composable
internal actual fun PinpointLocationItem(
    pinpointLocation: ClientAddressResponse,
    onUpdateAddress: (Int, Int, ClientAddressRequest) -> Unit,
    onDeleteAddress: (Int, Int) -> Unit,
) {
    val cameraPositionState = rememberCameraPositionState {
        pinpointLocation.latitude?.let { latitude ->
            pinpointLocation.longitude?.let { longitude ->
                position = CameraPosition.fromLatLngZoom(LatLng(latitude, longitude), 15f)
            }
        }
    }

    val uiSettings by remember {
        mutableStateOf(MapUiSettings(zoomControlsEnabled = false))
    }

    var showPinPointDialog by rememberSaveable { mutableStateOf(false) }

    if (showPinPointDialog) {
        PinPointSelectDialog(
            onDismissRequest = { showPinPointDialog = false },
            updateAddress = {
                pinpointLocation.id?.let { id ->
                    pinpointLocation.clientId?.let { clientId ->
                        onUpdateAddress(
                            clientId,
                            id,
                            ClientAddressRequest(),
                        )
                    }
                }
                showPinPointDialog = false
            },
            deleteAddress = {
                pinpointLocation.id?.let { id ->
                    pinpointLocation.clientId?.let { clientId ->
                        onDeleteAddress(
                            clientId,
                            id,
                        )
                    }
                }
                showPinPointDialog = false
            },
        )
    }

    OutlinedCard(
        modifier = Modifier
            .padding(8.dp),
        colors = CardDefaults.outlinedCardColors(MaterialTheme.colorScheme.surface),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(150.dp),
        ) {
            GoogleMap(
                modifier = Modifier.matchParentSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = uiSettings,
            ) {
                pinpointLocation.latitude?.let { latitude ->
                    pinpointLocation.longitude?.let { longitude ->
                        Marker(
                            state = MarkerState(position = LatLng(latitude, longitude)),
                            title = "Pinpoint Location",
                        )
                    }
                }
            }

            Box(
                Modifier
                    .matchParentSize()
                    .combinedClickable(
                        onClick = {},
                        onLongClick = { showPinPointDialog = true },
                    ),
            )
        }

        Text(
            modifier = Modifier.padding(8.dp),
            text = pinpointLocation.placeAddress.orEmpty(),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
actual fun PinpointMapDialogScreen(
    onSubmit: (lat: Double, lng: Double, description: String) -> Unit,
    onCancel: () -> Unit,
) {
    // Mifos Initiative latitude and longitude
    val initialLat = 47.66
    val initialLng = -122.37

    val context = LocalContext.current
    var latlng by remember { mutableStateOf(LatLng(initialLat, initialLng)) }
    var description by remember { mutableStateOf("") }
    val markerState = rememberMarkerState(position = latlng)

    @SuppressLint("MissingPermission")
    LaunchedEffect(Unit) {
        val fused = LocationServices.getFusedLocationProviderClient(context)

        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            try {
                val location = fused.lastLocation.await()
                latlng = LatLng(location.latitude, location.longitude)
            } catch (e: Exception) {
                latlng = LatLng(initialLat, initialLng)
            }
        }
    }

    LaunchedEffect(markerState.position) {
        latlng = markerState.position
    }

    Dialog(onDismissRequest = onCancel) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 4.dp,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Select Location", style = MaterialTheme.typography.titleLarge)

                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(vertical = 8.dp),
                    cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(latlng, 15f)
                    },
                    onMapClick = { newLatLng -> latlng = newLatLng },
                ) {
                    Marker(
                        state = markerState,
                        draggable = true,
                        title = "Pinpoint Location",
                    )
                }

                Row {
                    OutlinedTextField(
                        value = latlng.latitude.toString(),
                        onValueChange = {},
                        label = { Text("Latitude") },
                        enabled = false,
                        modifier = Modifier.weight(1f),
                    )

                    OutlinedTextField(
                        value = latlng.longitude.toString(),
                        onValueChange = {},
                        label = { Text("Longitude") },
                        enabled = false,
                        modifier = Modifier.weight(1f),
                    )
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth(),
                )

                Row {
                    Button(onClick = { onCancel() }, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = { onSubmit(latlng.latitude, latlng.longitude, description) },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Submit")
                    }
                }
            }
        }
    }
}
