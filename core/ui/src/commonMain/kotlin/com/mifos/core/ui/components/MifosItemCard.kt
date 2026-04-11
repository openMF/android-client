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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTheme
import com.mifos.core.ui.util.DevicePreview
import template.core.base.designsystem.theme.KptTheme

@Composable
fun MifosItemCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    elevation: Dp = KptTheme.elevation.level1,
    shape: Shape = DesignToken.shapes.small,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        shape = shape,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation,
        ),
        content = content,
    )
}

@DevicePreview
@Composable
private fun MifosItemCardPreview(
    modifier: Modifier = Modifier,
) {
    MifosTheme {
        MifosItemCard(
            onClick = {},
            modifier = modifier,
        ) {
            Text(text = "Card Content")
        }
    }
}
