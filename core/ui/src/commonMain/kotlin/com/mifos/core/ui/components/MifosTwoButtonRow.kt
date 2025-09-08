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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mifos.core.designsystem.component.MifosOutlinedButton
import com.mifos.core.designsystem.component.MifosTextButton
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTheme
import com.mifos.core.designsystem.theme.MifosTypography
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MifosTwoButtonRow(
    firstBtnText: String,
    secondBtnText: String,
    onFirstBtnClick: () -> Unit,
    onSecondBtnClick: () -> Unit,
    isFirstButtonEnabled: Boolean = true,
    isSecondButtonEnabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth()) {
        MifosOutlinedButton(
            onClick = {
                onFirstBtnClick()
            },
            leadingIcon = {
                Icon(
                    imageVector = MifosIcons.ChevronLeft,
                    contentDescription = null,
                    modifier = Modifier.size(DesignToken.sizes.iconAverage),
                    tint = MaterialTheme.colorScheme.primary,
                )
            },
            text = {
                Text(
                    text = firstBtnText,
                    color = MaterialTheme.colorScheme.primary,
                    style = MifosTypography.labelLarge,
                )
            },
            modifier = Modifier.weight(1f),
            enabled = isFirstButtonEnabled,
        )
        Spacer(Modifier.padding(DesignToken.padding.small))
        MifosTextButton(
            onClick = {
                onSecondBtnClick()
            },
            leadingIcon = {
                Icon(
                    imageVector = MifosIcons.Check,
                    contentDescription = null,
                    modifier = Modifier.size(DesignToken.sizes.iconAverage),
                )
            },
            text = {
                Text(
                    text = secondBtnText,
                    style = MifosTypography.labelLarge,
                )
            },
            modifier = Modifier.weight(1f),
            enabled = isSecondButtonEnabled,
        )
    }
}

@Composable
@Preview
private fun MifosTwoButtonRowPreview() {
    MifosTheme {
        MifosTwoButtonRow(
            firstBtnText = "First Button",
            secondBtnText = "Second Button",
            onFirstBtnClick = {},
            onSecondBtnClick = {},
        )
    }
}
