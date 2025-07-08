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

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.mifos.core.model.objects.clients.ClientAddressRequest
import com.mifos.core.model.objects.clients.ClientAddressResponse

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
