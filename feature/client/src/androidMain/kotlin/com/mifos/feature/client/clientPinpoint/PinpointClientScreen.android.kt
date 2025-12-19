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
import android.util.Log
import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_cancel_button
import androidclient.feature.client.generated.resources.feature_client_description_label
import androidclient.feature.client.generated.resources.feature_client_latitude_label
import androidclient.feature.client.generated.resources.feature_client_longitude_label
import androidclient.feature.client.generated.resources.feature_client_pinpoint_location_marker_title
import androidclient.feature.client.generated.resources.feature_client_saved_latitude
import androidclient.feature.client.generated.resources.feature_client_saved_longitude
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.model.objects.clients.ClientAddressResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import java.util.Locale
import kotlin.coroutines.cancellation.CancellationException

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
    val isUpdate = initialLat != null && initialLng != null
    val initialLng = LatLng(initialLat ?: mifosLat, initialLng ?: mifosLng)

    var latlng by remember { mutableStateOf(initialLng) }
    var isDirty by remember { mutableStateOf(false) }

    val displayLat = if (isDirty) {
        String.format(Locale.US, "%.6f", latlng.latitude)
    } else {
        initialLng.latitude.toString()
    }

    val displayLng = if (isDirty) {
        String.format(Locale.US, "%.6f", latlng.longitude)
    } else {
        initialLng.longitude.toString()
    }

    var description by remember { mutableStateOf(initialDescription ?: "") }
    val maxDescriptionLength = 255
    val isSubmitEnabled = if (isUpdate) {
        (
            (latlng != initialLng || description != initialDescription) &&
                description.length <= maxDescriptionLength
            )
    } else {
        description.trim().isNotEmpty() && description.length <= maxDescriptionLength
    }

    val roundedLatLng = LatLng(
        "%.2f".format(Locale.US, latlng.latitude).toDouble(),
        "%.2f".format(Locale.US, latlng.longitude).toDouble(),
    )

    val markerState = rememberMarkerState(position = latlng)
    val coroutineScope = rememberCoroutineScope()

    // TODO: Currently using default/test values — fix fetching the user’s
    //  current location when adding an address after permission is granted.

    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(latlng, 15f)
    }

    @SuppressLint("MissingPermission")
    fun fetchLocation() {
        coroutineScope.launch {
            val fineGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED

            val coarseGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED

            if (fineGranted || coarseGranted) {
                try {
                    val location = withContext(Dispatchers.IO) {
                        val fused = LocationServices.getFusedLocationProviderClient(context)
                        val cancellationTokenSource = CancellationTokenSource()

                        try {
                            fused.getCurrentLocation(
                                Priority.PRIORITY_HIGH_ACCURACY,
                                cancellationTokenSource.token,
                            ).await() ?: fused.lastLocation.await()
                        } catch (e: Exception) {
                            fused.lastLocation.await()
                        }
                    }

                    location?.let {
                        if (it.accuracy in 5f..200f) {
                            val newLatLng = LatLng(it.latitude, it.longitude)
                            latlng = newLatLng
                            markerState.position = newLatLng
                            isDirty = true
                            try {
                                cameraPositionState.animate(
                                    CameraUpdateFactory.newLatLngZoom(newLatLng, 15f),
                                )
                            } catch (e: CancellationException) {
                                Log.w("PinpointClientScreen", "Camera animation cancelled: ${e.message}")
                            }
                        } else {
                            Log.w("PinpointClientScreen", "Location accuracy too low: ${it.accuracy}")
                        }
                    }
                } catch (e: SecurityException) {
                    Log.e("PinpointClientScreen", "Permission error getting location: ${e.message}")
                } catch (e: Exception) {
                    Log.e("PinpointClientScreen", "Error getting location: ${e.message}")
                }
            } else {
                Log.w("PinpointClientScreen", "Location permission not granted")
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!isUpdate) {
            fetchLocation()
        }
    }

    LaunchedEffect(markerState.position) {
        if (markerState.position != latlng) {
            latlng = markerState.position
            isDirty = true
        }
    }

    Dialog(onDismissRequest = onCancel) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 4.dp,
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                ) {
                    Text(
                        stringResource(Res.string.feature_client_select_location_dialog_title),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f),
                    )

                    IconButton(
                        onClick = { fetchLocation() },
                        modifier = Modifier.size(40.dp),
                    ) {
                        Icon(
                            imageVector = MifosIcons.MyLocation,
                            contentDescription = "Refresh location",
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }

                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(vertical = 8.dp),
                    cameraPositionState = cameraPositionState,
                    onMapClick = { newLatLng ->
                        latlng = newLatLng
                        isDirty = true
                        markerState.position = newLatLng
                    },
                ) {
                    Marker(
                        state = markerState,
                        draggable = true,
                        title = stringResource(Res.string.feature_client_pinpoint_location_marker_title),
                    )
                }

                Row {
                    OutlinedTextField(
                        value = displayLat,
                        onValueChange = {},
                        label = { Text(stringResource(Res.string.feature_client_latitude_label)) },
                        enabled = false,
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )

                    OutlinedTextField(
                        value = displayLng,
                        onValueChange = {},
                        label = { Text(stringResource(Res.string.feature_client_longitude_label)) },
                        enabled = false,
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it.take(maxDescriptionLength)
                    },
                    label = { Text(stringResource(Res.string.feature_client_description_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = "${description.length} / $maxDescriptionLength",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (description.length >= maxDescriptionLength) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp),
                )

                Text(
                    stringResource(Res.string.feature_client_saved_latitude, roundedLatLng.latitude),
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    stringResource(Res.string.feature_client_saved_longitude, roundedLatLng.longitude),
                    style = MaterialTheme.typography.bodySmall,
                )

                Spacer(Modifier.height(8.dp))

                Row {
                    Button(onClick = { onCancel() }, modifier = Modifier.weight(1f)) {
                        Text(stringResource(Res.string.feature_client_cancel_button))
                    }

                    Spacer(Modifier.size(8.dp))

                    Button(
                        onClick = { onSubmit(roundedLatLng.latitude, roundedLatLng.longitude, description.trim()) },
                        enabled = isSubmitEnabled,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(stringResource(Res.string.feature_client_submit_button))
                    }
                }
            }
        }
    }
}
