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
import androidclient.feature.search.generated.resources.feature_search_exact_match
import androidclient.feature.search.generated.resources.feature_search_search_hint
import androidclient.feature.search.generated.resources.feature_search_title
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.ui.util.DevicePreview
import com.mifos.feature.search.FilterOption
import com.mifos.feature.search.SearchScreenEvent
import com.mifos.feature.search.SearchScreenState
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SearchBox(
    state: SearchScreenState,
    onEvent: (SearchScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Title And Filter Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.feature_search_title),
                    style = MaterialTheme.typography.headlineSmall,
                )

                AssistChip(
                    onClick = {
                        showDialog = true
                    },
                    label = {
                        Text(
                            text = state.selectedFilter?.label ?: "All",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = MifosIcons.Filter,
                            contentDescription = "Search Icon",
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = MifosIcons.KeyboardArrowDown,
                            contentDescription = "Dropdown Icon",
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors().copy(
                        leadingIconContentColor = MaterialTheme.colorScheme.tertiary,
                    ),
                )
            }

            // Search Text Field
            MifosOutlinedTextField(
                value = state.searchText,
                onValueChange = {
                    onEvent(SearchScreenEvent.UpdateSearchText(it))
                },
                leadingIcon = MifosIcons.Search,
                label = stringResource(Res.string.feature_search_search_hint),
                showClearIcon = state.searchText.isNotEmpty(),
                onClickClearIcon = {
                    onEvent(SearchScreenEvent.ClearSearchText)
                },
                maxLines = 1,
            )

            // Search Button
            Button(
                onClick = {
                    onEvent(SearchScreenEvent.PerformSearch)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 48.dp),
            ) {
                Icon(
                    imageVector = MifosIcons.Search,
                    contentDescription = "MifosIcons",
                )

                Text(
                    text = stringResource(Res.string.feature_search_title),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            // Exact Match Checkbox
            Row(
                modifier = Modifier
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                    ) {
                        onEvent(SearchScreenEvent.UpdateExactMatch)
                    }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
            ) {
                Checkbox(
                    checked = state.exactMatch ?: false,
                    onCheckedChange = {
                        onEvent(SearchScreenEvent.UpdateExactMatch)
                    },
                    modifier = Modifier
                        .size(20.dp),
                )

                Text(
                    text = stringResource(Res.string.feature_search_exact_match),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }

    AnimatedVisibility(
        visible = showDialog,
    ) {
        FilterDialog(
            selected = state.selectedFilter,
            onEvent = onEvent,
            onDismiss = {
                showDialog = false
            },
        )
    }
}

@DevicePreview
@Composable
private fun SearchBoxPreview() {
    SearchBox(
        modifier = Modifier.background(Color.White),
        state = SearchScreenState(),
        onEvent = {},
    )
}

@DevicePreview
@Composable
private fun SearchBoxWithValuesPreview() {
    SearchBox(
        modifier = Modifier.background(Color.White),
        state = SearchScreenState(
            searchText = "search text",
            selectedFilter = FilterOption.Groups,
            exactMatch = true,
        ),
        onEvent = {},
    )
}
