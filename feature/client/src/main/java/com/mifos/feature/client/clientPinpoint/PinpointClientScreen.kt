@file:OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)

package com.mifos.feature.client.clientPinpoint

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.PermissionBox
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.Black
import com.mifos.core.designsystem.theme.BlueSecondary
import com.mifos.core.designsystem.theme.White
import com.mifos.core.objects.client.ClientAddressRequest
import com.mifos.core.objects.client.ClientAddressResponse
import com.mifos.feature.client.R

@Composable
fun PinpointClientScreen(
    onBackPressed: () -> Unit,
) {

    val viewModel: PinPointClientViewModel = hiltViewModel()
    val clientId by viewModel.clientId.collectAsStateWithLifecycle()
    val state by viewModel.pinPointClientUiState.collectAsStateWithLifecycle()
    val refreshState by viewModel.isRefreshing.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getClientPinpointLocations(clientId = clientId)
    }


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
                clientAddressRequest
            )
        },
        onUpdateAddress = { apptableId, dapptableId, clientAddressRequest ->
            viewModel.updateClientPinpointLocation(
                apptableId, dapptableId, clientAddressRequest,
            )
        },
        onDeleteAddress = { apptableId, dapptableId ->
            viewModel.deleteClientPinpointLocation(
                apptableId,
                dapptableId
            )
        }
    )

}

@Composable
fun PinpointClientScreen(
    state: PinPointClientUiState,
    onBackPressed: () -> Unit,
    onRefresh: () -> Unit,
    refreshState: Boolean,
    onRetry: () -> Unit,
    onAddAddress: (ClientAddressRequest) -> Unit,
    onUpdateAddress: (Int, Int, ClientAddressRequest) -> Unit,
    onDeleteAddress: (Int, Int) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshState,
        onRefresh = onRefresh
    )
    var showPermissionDialog by remember { mutableStateOf(false) }

    if (showPermissionDialog) {
        PermissionBox(
            requiredPermissions = listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            title = R.string.feature_client_permission_required,
            description = R.string.feature_client_approve_permission_description_location,
            confirmButtonText = R.string.feature_client_proceed,
            dismissButtonText = R.string.feature_client_dismiss,
            onGranted = {
                showPermissionDialog = false
                onAddAddress(
                    // TODO Implement Place picker intent and fetch data and put into ClientAddressRequest
                    ClientAddressRequest()
                )
            }
        )
    }

    MifosScaffold(
        icon = MifosIcons.arrowBack,
        title = "Pinpoint Client",
        onBackPressed = onBackPressed,
        actions = {
            IconButton(onClick = {
                showPermissionDialog = true
            }) {
                Icon(
                    imageVector = MifosIcons.addLocation,
                    contentDescription = null
                )
            }
        },
        snackbarHostState = snackbarHostState
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
                when (state) {
                    is PinPointClientUiState.ClientPinpointLocations -> {
                        PinPointClientContent(
                            pinpointLocations = state.clientAddressResponses,
                            onUpdateAddress = onUpdateAddress,
                            onDeleteAddress = onDeleteAddress
                        )
                    }

                    is PinPointClientUiState.Error -> MifosSweetError(message = stringResource(id = state.message)) {
                        onRetry()
                    }

                    is PinPointClientUiState.Loading -> MifosCircularProgress()

                    is PinPointClientUiState.SuccessMessage -> {
                        Toast.makeText(
                            LocalContext.current,
                            stringResource(id = state.message),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                PullRefreshIndicator(
                    refreshing = refreshState,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}

@Composable
fun PinPointClientContent(
    pinpointLocations: List<ClientAddressResponse>,
    onUpdateAddress: (Int, Int, ClientAddressRequest) -> Unit,
    onDeleteAddress: (Int, Int) -> Unit,
) {

    LazyColumn {
        items(pinpointLocations) { pinpointLocation ->
            PinpointLocationItem(
                pinpointLocation = pinpointLocation,
                onUpdateAddress = onUpdateAddress,
                onDeleteAddress = onDeleteAddress
            )

        }
    }
}

@Composable
fun PinpointLocationItem(
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
                            ClientAddressRequest()
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
                            id
                        )
                    }
                }
                showPinPointDialog = false
            }
        )
    }

    OutlinedCard(
        modifier = Modifier
            .padding(8.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    showPinPointDialog = true
                }
            ),
        colors = CardDefaults.outlinedCardColors(White)
    ) {
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings
        )
        Text(
            modifier = Modifier.padding(8.dp),
            text = pinpointLocation.placeAddress.toString(),
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal,
                color = Black
            )
        )
    }
}

@Composable
fun PinPointSelectDialog(
    onDismissRequest: () -> Unit,
    updateAddress: () -> Unit,
    deleteAddress: () -> Unit,
) {

    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            colors = CardDefaults.cardColors(White),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(30.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.feature_client_please_select),
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal
                    ),
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { updateAddress() },
                    colors = ButtonDefaults.buttonColors(BlueSecondary)
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_client_update_client_address),
                        modifier = Modifier.fillMaxWidth(),
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal
                        ),
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
                Button(
                    onClick = { deleteAddress() },
                    colors = ButtonDefaults.buttonColors(BlueSecondary)
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_client_delete_image),
                        modifier = Modifier.fillMaxWidth(),
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal
                        ),
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

class PinpointClientUiStateProvider : PreviewParameterProvider<PinPointClientUiState> {

    override val values: Sequence<PinPointClientUiState>
        get() = sequenceOf(
            PinPointClientUiState.Loading,
            PinPointClientUiState.Error(R.string.feature_client_failed_to_load_pinpoint),
            PinPointClientUiState.SuccessMessage(R.string.feature_client_pinpoint_location_added),
            PinPointClientUiState.ClientPinpointLocations(clientAddressResponses = samplePinpointLocations)
        )
}

@Preview(showBackground = true)
@Composable
private fun PinpointClientScreenPreview(
    @PreviewParameter(PinpointClientUiStateProvider::class) state: PinPointClientUiState
) {
    PinpointClientScreen(
        state = state,
        onBackPressed = {},
        onRefresh = {},
        refreshState = false,
        onRetry = {},
        onAddAddress = {},
        onUpdateAddress = { _, _, _ -> },
        onDeleteAddress = { _, _ -> }
    )
}

val samplePinpointLocations = List(10) {
    ClientAddressResponse(placeAddress = "Address $it", latitude = 0.0, longitude = 0.0)
}