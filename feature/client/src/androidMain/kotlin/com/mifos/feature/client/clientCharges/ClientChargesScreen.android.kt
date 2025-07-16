/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientCharges

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_failed_to_load_client_charges
import androidclient.feature.client.generated.resources.feature_client_failed_to_load_more_charges
import androidclient.feature.client.generated.resources.feature_client_no_charges_found
import androidclient.feature.client.generated.resources.feature_client_no_more_charges_available
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
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosPagingAppendProgress
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.ui.components.MifosEmptyUi
import com.mifos.room.entities.client.ChargesEntity
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun ClientChargeContent(
    pagingFlow: Flow<PagingData<ChargesEntity>>,
    onRetry: () -> Unit,
) {
    val chargesPagingList = pagingFlow.collectAsLazyPagingItems()

    when (chargesPagingList.loadState.refresh) {
        is LoadState.Error -> {
            MifosSweetError(message = stringResource(Res.string.feature_client_failed_to_load_client_charges)) {
                onRetry()
            }
        }

        is LoadState.Loading -> MifosCircularProgress()

        is LoadState.NotLoading -> {
            if (chargesPagingList.itemCount == 0) {
                MifosEmptyUi(
                    text = stringResource(Res.string.feature_client_no_charges_found),
                    icon = MifosIcons.Payments,
                )
            } else {
                // Use a composite key of id and index to guarantee uniqueness,
                // preventing LazyColumn crashes when duplicate ids are present in paged data.
                LazyColumn {
                    items(
                        chargesPagingList.itemCount,
                        key = { index ->
                            val id = chargesPagingList[index]?.id
                            if (id != null) "id_${id}_index_$index" else "index_$index"
                        },
                    ) { index ->
                        chargesPagingList[index]?.let { ChargesItems(it) }
                    }

                    when (chargesPagingList.loadState.append) {
                        is LoadState.Error -> {
                            item {
                                MifosSweetError(message = stringResource(Res.string.feature_client_failed_to_load_more_charges)) {
                                    onRetry()
                                }
                            }
                        }

                        is LoadState.Loading -> {
                            item {
                                MifosPagingAppendProgress()
                            }
                        }

                        is LoadState.NotLoading -> Unit
                    }

                    if (
                        chargesPagingList.loadState.append is LoadState.NotLoading &&
                        chargesPagingList.loadState.append.endOfPaginationReached &&
                        chargesPagingList.itemCount > 0
                    ) {
                        item {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(6.dp),
                                text = stringResource(Res.string.feature_client_no_more_charges_available),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    }
}
