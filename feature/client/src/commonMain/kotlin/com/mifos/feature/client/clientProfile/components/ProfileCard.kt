/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientProfile.components

import kpt.feature.client.generated.resources.Res
import kpt.feature.client.generated.resources.group_label
import kpt.feature.client.generated.resources.group_na
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.mifos.core.designsystem.component.MifosCard
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTheme
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosUserImage
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kpt.core.base.designsystem.KptTheme
import kpt.core.base.designsystem.theme.LocalKptColors
import kpt.core.base.designsystem.theme.LocalKptShapes
import kpt.core.base.designsystem.theme.LocalKptSpacing

@Composable
fun ProfileCard(
    image: ByteArray?,
    name: String,
    accountNo: String,
    office: String,
    groupName: String? = null,
    onGroupClick: () -> Unit = {},
    onClick: () -> Unit,
) {
    MifosCard(
        modifier = Modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = LocalKptColors.current.primary,
            contentColor = LocalKptColors.current.onPrimary,
            disabledContainerColor = LocalKptColors.current.primary,
            disabledContentColor = LocalKptColors.current.onPrimary,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LocalKptSpacing.current.lg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MifosUserImage(
                bitmap = image,
                modifier = Modifier.size(DesignToken.sizes.avatarLarge),
            )
            Spacer(Modifier.width(DesignToken.padding.medium))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(DesignToken.padding.extraExtraSmall),
            ) {
                Text(
                    text = name,
                    style = MifosTypography.titleMediumEmphasized,
                )

                Text(
                    text = "Acc. No. $accountNo",
                    style = MifosTypography.bodySmall,
                    color = LocalKptColors.current.onPrimary.copy(alpha = 0.8f),
                )
                Text(
                    text = office,
                    style = MifosTypography.bodySmall,
                    color = LocalKptColors.current.onPrimary.copy(alpha = 0.8f),
                )

                Spacer(Modifier.height(LocalKptSpacing.current.sm))

                val displayGroupName = groupName ?: stringResource(Res.string.group_na)
                val isClickable = !groupName.isNullOrBlank() && groupName != stringResource(Res.string.group_na)
                val groupLabel = stringResource(Res.string.group_label)

                GroupChip(
                    text = "$groupLabel $displayGroupName",
                    isClickable = isClickable,
                    onClick = onGroupClick,
                )
            }
            Spacer(Modifier.width(DesignToken.padding.medium))
            Icon(
                imageVector = MifosIcons.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(DesignToken.sizes.iconSmall),
                tint = LocalKptColors.current.onPrimary.copy(alpha = 0.6f),
            )
        }
    }
}

@Composable
private fun GroupChip(
    text: String,
    isClickable: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(LocalKptShapes.current.small)
            .background(
                color = LocalKptColors.current.onPrimary.copy(alpha = 0.15f),
            )
            .clickable(enabled = isClickable, onClick = onClick)
            .padding(horizontal = LocalKptSpacing.current.sm, vertical = LocalKptSpacing.current.xs),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = MifosIcons.Group,
                contentDescription = null,
                modifier = Modifier.size(DesignToken.sizes.iconSmall),
                tint = LocalKptColors.current.onPrimary,
            )
            Spacer(modifier = Modifier.width(LocalKptSpacing.current.xs))
            Text(
                text = text,
                style = MifosTypography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = LocalKptColors.current.onPrimary,
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
            groupName = "Finance",
            onClick = {},
        )
    }
}
