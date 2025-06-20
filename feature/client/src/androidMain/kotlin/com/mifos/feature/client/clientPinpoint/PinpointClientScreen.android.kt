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
import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_approve_permission_description_location
import androidclient.feature.client.generated.resources.feature_client_dismiss
import androidclient.feature.client.generated.resources.feature_client_permission_required
import androidclient.feature.client.generated.resources.feature_client_proceed
import androidx.compose.foundation.combinedClickable
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
import com.google.maps.android.compose.rememberCameraPositionState
import com.mifos.core.designsystem.component.PermissionBox
import com.mifos.core.model.objects.clients.ClientAddressRequest
import com.mifos.core.model.objects.clients.ClientAddressResponse
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun HandleLocationPermissionRequest(
    show: Boolean,
    onPermissionResult: (granted: Boolean) -> Unit,
) {
    if (show) {
        PermissionBox(
            requiredPermissions = listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ),
            title = stringResource(Res.string.feature_client_permission_required),
            description = stringResource(Res.string.feature_client_approve_permission_description_location),
            confirmButtonText = stringResource(Res.string.feature_client_proceed),
            dismissButtonText = stringResource(Res.string.feature_client_dismiss),
            onGranted = {
                onPermissionResult(true)
            },
        )
    }
}

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
                // TODO Implement Place picker intent and fetch data and put into ClientAddressRequest
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
            .padding(8.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    showPinPointDialog = true
                },
            ),
        colors = CardDefaults.outlinedCardColors(MaterialTheme.colorScheme.surface),
    ) {
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings,
        )
        Text(
            modifier = Modifier.padding(8.dp),
            text = pinpointLocation.placeAddress.toString(),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
