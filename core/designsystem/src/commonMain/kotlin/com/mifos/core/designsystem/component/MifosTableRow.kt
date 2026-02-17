/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.designsystem.component

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mifos.core.designsystem.theme.DesignToken

/**
 * A reusable table row component that supports horizontal scrolling synchronization.
 * Used to create consistent table layouts with fixed column widths.
 *
 * @param cells List of composable functions representing each cell in the row
 * @param columnWidths List of fixed widths for each column (must match cells count)
 * @param scrollState Shared scroll state for synchronizing horizontal scrolling across rows
 * @param isHeader Whether this row is a header row (affects styling)
 * @param backgroundColor Background color for the row
 * @param modifier Optional modifier for the row
 */
@Composable
fun MifosTableRow(
    cells: List<@Composable () -> Unit>,
    columnWidths: List<Dp>,
    scrollState: ScrollState,
    isHeader: Boolean = false,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    modifier: Modifier = Modifier,
) {
    require(cells.size == columnWidths.size) {
        "Number of cells (${cells.size}) must match number of column widths (${columnWidths.size})"
    }

    Row(
        modifier = modifier
            .background(backgroundColor)
            .horizontalScroll(scrollState),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        cells.forEachIndexed { index, cell ->
            if (index > 0) {
                VerticalDivider(
                    modifier = Modifier.padding(vertical = DesignToken.padding.extraSmall),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
            }
            Row(
                modifier = Modifier
                    .width(columnWidths[index])
                    .padding(
                        horizontal = DesignToken.padding.small,
                        vertical = if (isHeader) {
                            DesignToken.padding.medium
                        } else {
                            DesignToken.padding.small
                        },
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                cell()
            }
        }
    }
    if (!isHeader) {
        HorizontalDivider(
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
        )
    }
}
