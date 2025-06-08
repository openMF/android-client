/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.center.centerList.ui

import androidclient.feature.center.generated.resources.Res
import androidclient.feature.center.generated.resources.feature_center_error_loading_centers
import androidclient.feature.center.generated.resources.feature_center_no_more_centers
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosPagingAppendProgress
import com.mifos.core.designsystem.component.MifosSweetError
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
actual fun CenterListContent(
    state: CenterListUiState,
    isInSelectionMode: Boolean,
    selectedItems: SelectedItemsState,
    onRefresh: () -> Unit,
    onCenterSelect: (Int) -> Unit,
    modifier: Modifier,
) {
    if (state is CenterListUiState.CenterList) {
        val centerPagingList = state.centers.collectAsLazyPagingItems()
        when (centerPagingList.loadState.refresh) {
            is LoadState.Error -> {
                MifosSweetError(message = stringResource(Res.string.feature_center_error_loading_centers)) {
                    onRefresh()
                }
            }

            is LoadState.Loading -> MifosCircularProgress()

            is LoadState.NotLoading -> Unit
        }

        LazyColumn(
            modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(
                count = centerPagingList.itemCount,
                key = {
                    centerPagingList[it]?.id ?: it
                },
            ) { index ->
                val center = centerPagingList[index]!!

                CenterCard(
                    center = center,
                    selected = selectedItems.contains(center),
                    isInSelectionMode = selectedItems.size() > 0,
                    onSelect = {
                        if (selectedItems.contains(it)) {
                            selectedItems.remove(it)
                        } else {
                            selectedItems.add(it)
                        }
                    },
                    onClick = {
                        onCenterSelect(it.id ?: 0)
                    },
                )
            }

            when (centerPagingList.loadState.append) {
                is LoadState.Error -> {}

                is LoadState.Loading -> {
                    item {
                        MifosPagingAppendProgress()
                    }
                }

                is LoadState.NotLoading -> Unit
            }
            when (centerPagingList.loadState.append.endOfPaginationReached) {
                true -> {
                    item {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(6.dp),
                            text = stringResource(Res.string.feature_center_no_more_centers),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                false -> Unit
            }
        }
    }
}
