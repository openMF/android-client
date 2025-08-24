/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientProfile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mifos.core.designsystem.component.MifosCard
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.AppColors
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTheme
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosUserImage
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProfileCard(
    image: ByteArray?,
    name: String,
    accountNo: String,
    office: String,
    onClick: () -> Unit,
) {
    MifosCard(
        modifier = Modifier.clickable {
            onClick()
        },
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = AppColors.customWhite,
            disabledContainerColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = AppColors.customWhite,
        ),
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
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(DesignToken.padding.extraExtraSmall),
            ) {
                Text(
                    text = name,
                    style = MifosTypography.titleMediumEmphasized,
                )

                Text(
                    text = "Acc. No. $accountNo",
                    style = MifosTypography.bodySmall,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                )
                Text(
                    text = office,
                    style = MifosTypography.bodySmall,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                )
            }
            Spacer(Modifier.width(DesignToken.padding.medium))
            Icon(
                imageVector = MifosIcons.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(DesignToken.sizes.iconSmall),
            )
        }
    }
}

@Preview
@Composable
fun ProfileCardPreview() {
    MifosTheme {
        ProfileCard(
            image = null,
            name = "John",
            accountNo = "2344",
            office = "Head Office",
            onClick = {},
        )
    }
}
