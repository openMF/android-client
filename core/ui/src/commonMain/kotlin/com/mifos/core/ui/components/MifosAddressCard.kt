/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MifosAddressCard(
    title: String,
    addressList: Map<String, String>,
    modifier: Modifier = Modifier,
) {
    MifosListingComponentOutline {
        Column(
            modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(DesignToken.padding.extraSmall),
        ) {
            Text(
                text = title,
                style = MifosTypography.titleMediumEmphasized,
            )
            Spacer(Modifier.height(DesignToken.padding.medium))
            addressList.forEach { (key, value) ->
                MifosAddressItem(key, value)
                Spacer(Modifier.height(DesignToken.padding.small))
            }
        }
    }
}

@Composable
fun MifosAddressItem(
    addressLabel: String,
    addressValue: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(DesignToken.padding.extraExtraSmall),
    ) {
        Text(
            text = addressLabel,
            style = MifosTypography.labelSmall,
            color = MaterialTheme.colorScheme.secondary,
        )
        Text(
            text = addressValue,
            style = MifosTypography.bodySmall,
        )
    }
}

@Preview
@Composable
fun MifosAddressCardPreview() {
    val sampleAddresses = mapOf(
        "Address 1" to "123, MG Road",
        "Address 2" to "2nd Floor, Lotus Building",
        "City" to "Bengaluru",
        "State" to "Karnataka",
        "Pincode" to "560001",
        "Country" to "India",
    )

    MifosAddressCard(
        title = "Client Address",
        addressList = sampleAddresses,
    )
}
