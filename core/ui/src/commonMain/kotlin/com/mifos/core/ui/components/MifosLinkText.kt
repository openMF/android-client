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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.mifos.core.designsystem.theme.MifosTheme
import com.mifos.core.ui.util.DevicePreview

@Composable
fun MifosLinkText(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isUnderlined: Boolean = true,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium.copy(
            textDecoration = if (isUnderlined) TextDecoration.Underline else null,
        ),
        modifier = modifier
            .padding(vertical = 2.dp)
            .clickable {
                onClick()
            },
    )
}

@DevicePreview
@Composable
fun MifosLinkTextPreview(
    modifier: Modifier = Modifier,
) {
    MifosTheme {
        MifosLinkText(
            text = "Link Text",
            onClick = {},
            modifier = modifier,
        )
    }
}
