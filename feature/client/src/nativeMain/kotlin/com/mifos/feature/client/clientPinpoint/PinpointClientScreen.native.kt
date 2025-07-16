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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mifos.core.model.objects.clients.ClientAddressResponse

@Composable
internal actual fun PinpointLocationItem(
    pinpointLocation: ClientAddressResponse,
    onStartUpdateAddress: (ClientAddressResponse) -> Unit,
    onDeleteAddress: (Int, Int) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Not available in this platform yet",
        )
    }
}

@Composable
actual fun PinpointMapDialogScreen(
    initialLat: Double?,
    initialLng: Double?,
    initialDescription: String?,
    onSubmit: (Double, Double, String) -> Unit,
    onCancel: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Not available in this platform yet",
        )
    }
}
