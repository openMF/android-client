/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientDetailsProfile.components

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.email_na
import androidclient.feature.client.generated.resources.mobile_na
import androidclient.feature.client.generated.resources.name_na
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTheme
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosUserImage
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ClientDetailsProfile(
    image: ByteArray?,
    name: String?,
    mobile: String?,
    email: String?,
) {
    Row(
        Modifier.fillMaxWidth()
            .padding(DesignToken.padding.largeIncreasedExtra),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MifosUserImage(
            bitmap = image,
            modifier = Modifier.size(DesignToken.sizes.avatarLarge),
        )
        Spacer(Modifier.width(DesignToken.padding.medium))
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(DesignToken.padding.extraExtraSmall),
        ) {
            Text(
                text = name ?: stringResource(Res.string.name_na),
                style = MifosTypography.titleMediumEmphasized,
            )
            Text(
                text = mobile ?: stringResource(Res.string.mobile_na),
                style = MifosTypography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
            )
            Text(
                text = email ?: stringResource(Res.string.email_na),
                style = MifosTypography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Preview
@Composable
fun ClientDetailsProfilePreview() {
    MifosTheme {
        ClientDetailsProfile(
            image = null,
            name = "John",
            mobile = "+91 9030808053",
            email = "jrevanth101@gmail.com",
        )
    }
}
