/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.search.components

import androidclient.feature.search.generated.resources.Res
import androidclient.feature.search.generated.resources.feature_search_filter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.mifos.core.designsystem.component.MifosDialogBox
import com.mifos.core.ui.util.DevicePreview
import com.mifos.feature.search.FilterOption
import com.mifos.feature.search.SearchScreenEvent
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FilterDialog(
    selected: FilterOption?,
    onEvent: (SearchScreenEvent.UpdateSelectedFilter) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosDialogBox(
        title = stringResource(Res.string.feature_search_filter),
        showDialogState = true,
        confirmButtonText = "",
        dismissButtonText = "Close",
        onConfirm = {},
        onDismiss = onDismiss,
        modifier = modifier,
        message = {
            Column(
                modifier = modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
            ) {
                HorizontalDivider()
                FilterOption(
                    text = "All",
                    selected = selected == null,
                    onSelected = {
                        onEvent(SearchScreenEvent.UpdateSelectedFilter(null))
                        onDismiss()
                    },
                )
                HorizontalDivider()
                FilterOption.values.forEachIndexed { index, option ->
                    FilterOption(
                        text = option.label,
                        selected = option == selected,
                        onSelected = {
                            onEvent(SearchScreenEvent.UpdateSelectedFilter(option))
                            onDismiss()
                        },
                    )
                    if (index != FilterOption.values.size - 1) {
                        HorizontalDivider()
                    }
                }
            }
        },
    )
}

@Composable
internal fun FilterOption(
    text: String,
    selected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onSelected()
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = {
                onSelected()
            },
        )
        Text(
            text = text,
            fontSize = 16.sp,
        )
    }
}

@DevicePreview
@Composable
private fun FilterDialogPreview(
    modifier: Modifier = Modifier,
) {
    FilterDialog(
        modifier = modifier,
        selected = null,
        onEvent = {},
        onDismiss = {},
    )
}

@DevicePreview
@Composable
private fun FilterOptionPreview(
    modifier: Modifier = Modifier,
) {
    MaterialTheme {
        FilterOption(
            modifier = modifier,
            text = "Search Option",
            selected = true,
            onSelected = {},
        )
    }
}
