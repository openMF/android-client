/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.mifos.feature.client.clientPinpoint

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_approve_permission_description_location
import androidclient.feature.client.generated.resources.feature_client_delete_client_address
import androidclient.feature.client.generated.resources.feature_client_dismiss
import androidclient.feature.client.generated.resources.feature_client_failed_to_load_pinpoint
import androidclient.feature.client.generated.resources.feature_client_permission_required
import androidclient.feature.client.generated.resources.feature_client_pinpoint_client
import androidclient.feature.client.generated.resources.feature_client_pinpoint_location_added
import androidclient.feature.client.generated.resources.feature_client_please_select
import androidclient.feature.client.generated.resources.feature_client_proceed
import androidclient.feature.client.generated.resources.feature_client_update_client_address
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.PermissionBox
import com.mifos.core.designsystem.component.getRequiredPermissionsForLocation
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.model.objects.clients.ClientAddressRequest
import com.mifos.core.model.objects.clients.ClientAddressResponse
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.util.DevicePreview
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun PinpointClientScreen(
    onBackPressed: () -> Unit,
    viewModel: PinPointClientViewModel = koinViewModel(),
) {
    val clientId by viewModel.clientId.collectAsStateWithLifecycle()
    val state by viewModel.pinPointClientUiState.collectAsStateWithLifecycle()
    val refreshState by viewModel.isRefreshing.collectAsStateWithLifecycle()

    PinpointClientScreen(
        state = state,
        onBackPressed = onBackPressed,
        onRefresh = {
            viewModel.refreshPinpointLocations(clientId)
        },
        refreshState = refreshState,
        onRetry = {
            viewModel.getClientPinpointLocations(clientId)
        },
        onAddAddress = { clientAddressRequest ->
            viewModel.addClientPinpointLocation(
                clientId,
                clientAddressRequest,
            )
        },
        onUpdateAddress = { apptableId, dapptableId, clientAddressRequest ->
            viewModel.updateClientPinpointLocation(
                apptableId,
                dapptableId,
                clientAddressRequest,
            )
        },
        onDeleteAddress = { apptableId, dapptableId ->
            viewModel.deleteClientPinpointLocation(
                apptableId,
                dapptableId,
            )
        },
        onAddressesChanged = {
            viewModel.getClientPinpointLocations(clientId)
        },
    )
}

@Composable
internal fun PinpointClientScreen(
    state: PinPointClientUiState,
    onBackPressed: () -> Unit,
    onRefresh: () -> Unit,
    refreshState: Boolean,
    onRetry: () -> Unit,
    onAddAddress: (ClientAddressRequest) -> Unit,
    onUpdateAddress: (Int, Int, ClientAddressRequest) -> Unit,
    onDeleteAddress: (Int, Int) -> Unit,
    onAddressesChanged: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val pullRefreshState = rememberPullToRefreshState()

    var showPermissionDialog by remember { mutableStateOf(false) }
    var showMapDialogScreen by remember { mutableStateOf(false) }
    var updateMode by remember { mutableStateOf(false) }
    var addressToUpdate by remember { mutableStateOf<ClientAddressResponse?>(null) }

    if (showPermissionDialog) {
        PermissionBox(
            requiredPermissions = getRequiredPermissionsForLocation(),
            title = stringResource(Res.string.feature_client_permission_required),
            description = stringResource(Res.string.feature_client_approve_permission_description_location),
            confirmButtonText = stringResource(Res.string.feature_client_proceed),
            dismissButtonText = stringResource(Res.string.feature_client_dismiss),
            onGranted = {
                showPermissionDialog = false
                showMapDialogScreen = true
            },
        )
    }

    if (showMapDialogScreen) {
        PinpointMapDialogScreen(
            initialLat = addressToUpdate?.latitude,
            initialLng = addressToUpdate?.longitude,
            initialDescription = addressToUpdate?.placeAddress,
            onSubmit = { lat, lng, description ->
                if (updateMode && addressToUpdate != null) {
                    val address = requireNotNull(addressToUpdate)
                    val id = requireNotNull(address.id)
                    val clientId = requireNotNull(address.clientId)

                    onUpdateAddress(
                        clientId,
                        id,
                        ClientAddressRequest(
                            latitude = lat,
                            longitude = lng,
                            placeAddress = description,
                        ),
                    )
                } else {
                    onAddAddress(
                        ClientAddressRequest(
                            latitude = lat,
                            longitude = lng,
                            placeAddress = description,
                        ),
                    )
                }
                showMapDialogScreen = false
                addressToUpdate = null
            },
            onCancel = {
                showMapDialogScreen = false
                addressToUpdate = null
            },
        )
    }

    MifosScaffold(
        title = stringResource(Res.string.feature_client_pinpoint_client),
        onBackPressed = onBackPressed,
        actions = {
            IconButton(onClick = {
                showPermissionDialog = true
                updateMode = false
                addressToUpdate = null
            }) {
                Icon(
                    imageVector = MifosIcons.AddLocation,
                    contentDescription = null,
                )
            }
        },
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            PullToRefreshBox(
                state = pullRefreshState,
                onRefresh = onRefresh,
                isRefreshing = refreshState,
            ) {
                when (state) {
                    is PinPointClientUiState.ClientPinpointLocations -> {
                        PinPointClientContent(
                            pinpointLocations = state.clientAddressResponses,
                            onStartUpdateAddress = { address ->
                                updateMode = true
                                addressToUpdate = address
                                showMapDialogScreen = true
                            },
                            onDeleteAddress = onDeleteAddress,
                        )
                    }

                    is PinPointClientUiState.Error -> MifosSweetError(message = stringResource(state.message)) {
                        onRetry()
                    }

                    is PinPointClientUiState.Loading -> MifosProgressIndicator()

                    is PinPointClientUiState.SuccessMessage -> {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = getString(state.message),
                            )
                        }
                        onAddressesChanged()
                    }
                }
            }
        }
    }
}

@Composable
private fun PinPointClientContent(
    pinpointLocations: List<ClientAddressResponse>,
    onStartUpdateAddress: (ClientAddressResponse) -> Unit,
    onDeleteAddress: (Int, Int) -> Unit,
) {
    LazyColumn {
        items(pinpointLocations) { pinpointLocation ->
            PinpointLocationItem(
                pinpointLocation = pinpointLocation,
                onStartUpdateAddress = onStartUpdateAddress,
                onDeleteAddress = onDeleteAddress,
            )
        }
    }
}

@Composable
expect fun PinpointMapDialogScreen(
    initialLat: Double? = null,
    initialLng: Double? = null,
    initialDescription: String? = null,
    onSubmit: (lat: Double, lng: Double, description: String) -> Unit,
    onCancel: () -> Unit,
)

@Composable
internal expect fun PinpointLocationItem(
    pinpointLocation: ClientAddressResponse,
    onStartUpdateAddress: (ClientAddressResponse) -> Unit,
    onDeleteAddress: (Int, Int) -> Unit,
)

@Composable
internal fun PinPointSelectDialog(
    onDismissRequest: () -> Unit,
    updateAddress: () -> Unit,
    deleteAddress: () -> Unit,
) {
    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
    ) {
        Card(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(30.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(Res.string.feature_client_please_select),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { updateAddress() },
                ) {
                    Text(
                        text = stringResource(Res.string.feature_client_update_client_address),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }
                Button(
                    onClick = { deleteAddress() },
                ) {
                    Text(
                        text = stringResource(Res.string.feature_client_delete_client_address),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

private class PinpointClientUiStateProvider : PreviewParameterProvider<PinPointClientUiState> {

    override val values: Sequence<PinPointClientUiState>
        get() = sequenceOf(
            PinPointClientUiState.Loading,
            PinPointClientUiState.Error(Res.string.feature_client_failed_to_load_pinpoint),
            PinPointClientUiState.SuccessMessage(Res.string.feature_client_pinpoint_location_added),
            PinPointClientUiState.ClientPinpointLocations(clientAddressResponses = samplePinpointLocations),
        )
}

@DevicePreview
@Composable
private fun PinpointClientScreenPreview(
    @PreviewParameter(PinpointClientUiStateProvider::class) state: PinPointClientUiState,
) {
    PinpointClientScreen(
        state = state,
        onBackPressed = {},
        onRefresh = {},
        refreshState = false,
        onRetry = {},
        onAddAddress = {},
        onUpdateAddress = { _, _, _ -> },
        onDeleteAddress = { _, _ -> },
        onAddressesChanged = {},
    )
}

val samplePinpointLocations = List(10) {
    ClientAddressResponse(
        placeAddress = "Address $it",
        latitude = 0.0,
        longitude = 0.0,
        clientId = 1,
        id = 1,
    )
}
