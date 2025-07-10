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

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_address_label
import androidclient.feature.client.generated.resources.feature_client_cancel_button
import androidclient.feature.client.generated.resources.feature_client_latitude_label
import androidclient.feature.client.generated.resources.feature_client_longitude_label
import androidclient.feature.client.generated.resources.feature_client_pinpoint_location_marker_title
import androidclient.feature.client.generated.resources.feature_client_select_location_dialog_title
import androidclient.feature.client.generated.resources.feature_client_submit_button
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.mifos.core.model.objects.clients.ClientAddressResponse
import org.jetbrains.compose.resources.stringResource
import java.util.Locale

@Composable
internal actual fun PinpointLocationItem(
    pinpointLocation: ClientAddressResponse,
    onStartUpdateAddress: (ClientAddressResponse) -> Unit,
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
                onStartUpdateAddress(pinpointLocation)
                showPinPointDialog = false
            },
            deleteAddress = {
                pinpointLocation.id?.let { id ->
                    pinpointLocation.clientId?.let { clientId ->
                        onDeleteAddress(clientId, id)
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
                            title = stringResource(Res.string.feature_client_pinpoint_location_marker_title),
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
    initialLat: Double?,
    initialLng: Double?,
    initialDescription: String?,
    onSubmit: (lat: Double, lng: Double, description: String) -> Unit,
    onCancel: () -> Unit,
) {
    // Mifos Initiative latitude and longitude
    val mifosLat = 47.66
    val mifosLng = -122.37

    val context = LocalContext.current
    var latlng by remember {
        mutableStateOf(
            LatLng(
                initialLat ?: mifosLat,
                initialLng ?: mifosLng,
            ),
        )
    }
    var description by remember { mutableStateOf(initialDescription ?: "") }
    val markerState = rememberMarkerState(position = latlng)

// TODO: Currently using default values — fix fetching the user’s current location when
//  adding an address after permission is granted.

//    @SuppressLint("MissingPermission")
//    LaunchedEffect(Unit) {
//        val fused = LocationServices.getFusedLocationProviderClient(context)
//
//        val fineGranted = ContextCompat.checkSelfPermission(
//            context,
//            Manifest.permission.ACCESS_FINE_LOCATION,
//        ) == PackageManager.PERMISSION_GRANTED
//
//        val coarseGranted = ContextCompat.checkSelfPermission(
//            context,
//            Manifest.permission.ACCESS_FINE_LOCATION,
//        ) == PackageManager.PERMISSION_GRANTED
//
//        if (fineGranted || coarseGranted) {
//            try {
//                val location = fused.lastLocation.await()
//                latlng = LatLng(location.latitude, location.longitude)
//            } catch (e: Exception) {
//                latlng = LatLng(MIFOS_LAT, MIFOS_LNG)
//            }
//        }
//    }

    LaunchedEffect(markerState.position) {
        latlng = markerState.position
    }

    Dialog(onDismissRequest = onCancel) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 4.dp,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    stringResource(Res.string.feature_client_select_location_dialog_title),
                    style = MaterialTheme.typography.titleLarge,
                )

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
                        title = stringResource(Res.string.feature_client_pinpoint_location_marker_title),
                    )
                }

                Row {
                    OutlinedTextField(
                        value = String.format(Locale.US, "%.7f", latlng.latitude),
                        onValueChange = {},
                        label = { Text(stringResource(Res.string.feature_client_latitude_label)) },
                        enabled = false,
                        modifier = Modifier.weight(1f),
                    )

                    OutlinedTextField(
                        value = String.format(Locale.US, "%.7f", latlng.longitude),
                        onValueChange = {},
                        label = { Text(stringResource(Res.string.feature_client_longitude_label)) },
                        enabled = false,
                        modifier = Modifier.weight(1f),
                    )
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(Res.string.feature_client_address_label)) },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(8.dp))

                Row {
                    Button(onClick = { onCancel() }, modifier = Modifier.weight(1f)) {
                        Text(stringResource(Res.string.feature_client_cancel_button))
                    }

                    Button(
                        onClick = { onSubmit(latlng.latitude, latlng.longitude, description) },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(stringResource(Res.string.feature_client_submit_button))
                    }
                }
            }
        }
    }
}
