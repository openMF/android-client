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

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mifos.core.designsystem.component.MifosOutlinedButton
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTheme
import com.mifos.core.designsystem.theme.MifosTypography
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MifosRowWithTextAndButton(
    onBtnClick: () -> Unit,
    btnText: String,
    btnEnabled: Boolean = true,
    text: String,
    modifier: Modifier = Modifier,
) {
    MifosListingComponentOutline(
        modifier = modifier,
        content = {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = text,
                    style = MifosTypography.bodyLarge,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(DesignToken.padding.extraSmall))
                MifosOutlinedButton(
                    onClick = { onBtnClick() },
                    text = {
                        Text(
                            text = btnText,
                            color = MaterialTheme.colorScheme.primary,
                            style = MifosTypography.labelMediumEmphasized,
                        )
                    },
                    enabled = btnEnabled,
                )
            }
        },
    )
}

@Composable
@Preview
private fun MifosRowWithTextAndButtonPreview() {
    MifosTheme {
        MifosRowWithTextAndButton(
            onBtnClick = {},
            btnText = "View",
            text = "2 Collaterals",
        )
    }
}
