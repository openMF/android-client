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

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.mifos.core.ui.util.TextUtil

@Composable
fun PrintTextUtil(
    item: TextUtil,
) {
    Text(
        text = item.text,
        color = item.color ?: MaterialTheme.colorScheme.onSurface,
        style = item.style ?: MaterialTheme.typography.bodyMedium,
    )
}
