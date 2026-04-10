/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.mifos.feature.client.clientPinpoint

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_add_location
import androidclient.feature.client.generated.resources.feature_client_approve_permission_description_location
import androidclient.feature.client.generated.resources.feature_client_client_locations
import androidclient.feature.client.generated.resources.feature_client_delete_client_address
import androidclient.feature.client.generated.resources.feature_client_dismiss
import androidclient.feature.client.generated.resources.feature_client_failed_to_load_pinpoint
import androidclient.feature.client.generated.resources.feature_client_no_location_data_found
import androidclient.feature.client.generated.resources.feature_client_permission_required
import androidclient.feature.client.generated.resources.feature_client_pinpoint_location_added
import androidclient.feature.client.generated.resources.feature_client_please_select
import androidclient.feature.client.generated.resources.feature_client_proceed
import androidclient.feature.client.generated.resources.feature_client_update_client_address
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.PermissionBox
import com.mifos.core.designsystem.component.getRequiredPermissionsForLocation
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.model.objects.clients.ClientAddressRequest
import com.mifos.core.model.objects.clients.ClientAddressResponse
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosEmptyCard
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.util.DevicePreview
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme

@Composable
internal fun PinpointClientScreen(
    onBackPressed: () -> Unit,
    navController: NavController,
    viewModel: PinPointClientViewModel = koinViewModel(),
) {
    val clientId by viewModel.clientId.collectAsStateWithLifecycle()
    val state by viewModel.pinPointClientUiState.collectAsStateWithLifecycle()
    val refreshState by viewModel.isRefreshing.collectAsStateWithLifecycle()

    PinpointClientScreen(
        state = state,
        navController = navController,
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
    navController: NavController,
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

    Column(modifier = Modifier.fillMaxSize()) {
        MifosBreadcrumbNavBar(navController)
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
                            showPermissionDialog = true
                        },
                        onClickAddLocation = {
                            updateMode = false
                            addressToUpdate = null
                            showPermissionDialog = true
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

@Composable
private fun PinPointClientContent(
    pinpointLocations: List<ClientAddressResponse>,
    onStartUpdateAddress: (ClientAddressResponse) -> Unit,
    onClickAddLocation: () -> Unit,
    onDeleteAddress: (Int, Int) -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = KptTheme.spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.feature_client_client_locations),
                style = MifosTypography.titleMediumEmphasized,
                color = KptTheme.colorScheme.onSurface,
            )

            Icon(
                imageVector = MifosIcons.Add,
                contentDescription = stringResource(Res.string.feature_client_add_location),
                modifier = Modifier.clickable {
                    onClickAddLocation()
                }.size(DesignToken.sizes.iconAverage),
            )
        }

        Spacer(modifier = Modifier.height(KptTheme.spacing.md))

        if (pinpointLocations.isEmpty()) {
            MifosEmptyCard(
                msg = stringResource(Res.string.feature_client_no_location_data_found),
            )
        } else {
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
            colors = CardDefaults.cardColors(KptTheme.colorScheme.surface),
            shape = DesignToken.shapes.largeIncreased,
        ) {
            Column(
                modifier = Modifier
                    .padding(DesignToken.padding.dp30),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(Res.string.feature_client_please_select),
                    modifier = Modifier.fillMaxWidth(),
                    style = KptTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(DesignToken.spacing.largeIncreased))

                Button(
                    onClick = { updateAddress() },
                ) {
                    Text(
                        text = stringResource(Res.string.feature_client_update_client_address),
                        modifier = Modifier.fillMaxWidth(),
                        style = KptTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }
                Button(
                    onClick = { deleteAddress() },
                ) {
                    Text(
                        text = stringResource(Res.string.feature_client_delete_client_address),
                        modifier = Modifier.fillMaxWidth(),
                        style = KptTheme.typography.bodyLarge,
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
        navController = rememberNavController(),
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
