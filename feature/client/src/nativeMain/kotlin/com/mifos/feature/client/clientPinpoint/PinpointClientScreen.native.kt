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

import androidx.compose.runtime.Composable
import com.mifos.core.model.objects.clients.ClientAddressRequest
import com.mifos.core.model.objects.clients.ClientAddressResponse

@Composable
actual fun HandleLocationPermissionRequest(
    show: Boolean,
    onPermissionResult: (Boolean) -> Unit,
) {
    TODO("Not yet implemented")
}

@Composable
actual fun PinpointLocationItem(
    pinpointLocation: ClientAddressResponse,
    onUpdateAddress: (Int, Int, ClientAddressRequest) -> Unit,
    onDeleteAddress: (Int, Int) -> Unit,
) {
    TODO("Not yet implemented")
}
