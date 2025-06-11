package com.mifos.feature.client.clientPinpoint

import androidx.compose.runtime.Composable
import com.mifos.core.model.objects.clients.ClientAddressRequest
import com.mifos.core.model.objects.clients.ClientAddressResponse

@Composable
actual fun HandleLocationPermissionRequest(
    show: Boolean,
    onPermissionResult: (Boolean) -> Unit
) {
    TODO("Not yet implemented")
}

@Composable
actual fun PinpointLocationItem(
    pinpointLocation: ClientAddressResponse,
    onUpdateAddress: (Int, Int, ClientAddressRequest) -> Unit,
    onDeleteAddress: (Int, Int) -> Unit
) {
    TODO("Not yet implemented")
}