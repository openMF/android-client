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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mifos.core.designsystem.theme.MifosTheme
import com.mifos.core.ui.util.DevicePreview

@Composable
fun MifosCheckBox(
    text: String,
    checked: Boolean,
    onCheckChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    checkboxColors: CheckboxColors = CheckboxDefaults.colors(),
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { onCheckChanged.invoke(it) },
            colors = checkboxColors,
        )
        Text(text = text)
    }
}

@DevicePreview
@Composable
fun MifosCheckBoxPreview() {
    MifosTheme {
        MifosCheckBox(
            checked = false,
            onCheckChanged = {},
            text = "",
        )
    }
}
