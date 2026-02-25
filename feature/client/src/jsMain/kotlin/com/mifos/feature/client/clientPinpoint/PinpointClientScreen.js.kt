/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientPinpoint

import androidx.compose.runtime.Composable
import com.mifos.core.model.objects.clients.ClientAddressResponse

@Composable
actual fun PinpointMapDialogScreen(
    initialLat: Double?,
    initialLng: Double?,
    initialDescription: String?,
    onSubmit: (lat: Double, lng: Double, description: String) -> Unit,
    onCancel: () -> Unit,
) {
}

@Composable
internal actual fun PinpointLocationItem(
    pinpointLocation: ClientAddressResponse,
    onStartUpdateAddress: (ClientAddressResponse) -> Unit,
    onDeleteAddress: (Int, Int) -> Unit,
) {
}
