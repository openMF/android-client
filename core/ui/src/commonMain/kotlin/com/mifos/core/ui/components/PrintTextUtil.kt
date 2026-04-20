/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.util.TextUtil
import template.core.base.designsystem.theme.KptTheme

@Composable
fun PrintTextUtil(
    item: TextUtil,
) {
    Text(
        text = item.text,
        color = item.color ?: KptTheme.colorScheme.onSurface,
        style = item.style ?: MifosTypography.bodySmall,
    )
}
