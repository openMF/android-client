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
import com.mifos.room.entities.client.ChargesEntity
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun ClientChargeContent(
    pagingFlow: Flow<PagingData<ChargesEntity>>,
    onRetry: () -> Unit,
) {
    val chargesPage = pagingFlow.collectAsLazyPagingItems()

    when (chargesPage.loadState.refresh) {
        is LoadState.Error -> {
            MifosSweetError(message = stringResource(Res.string.feature_client_failed_to_load_client_charges)) {
                onRetry()
            }
        }

        is LoadState.Loading -> MifosCircularProgress()

        is LoadState.NotLoading -> {
            LazyColumn {
                items(
                    chargesPage.itemCount,
                    key = { index -> chargesPage[index]?.id ?: index },
                ) { index ->
                    chargesPage[index]?.let { ChargesItems(it) }
                }

                when (chargesPage.loadState.append) {
                    is LoadState.Error -> {
                    }

                    is LoadState.Loading -> {
                        item {
                            MifosPagingAppendProgress()
                        }
                    }

                    is LoadState.NotLoading -> Unit
                }

                if (
                    chargesPage.loadState.append is LoadState.NotLoading &&
                    chargesPage.loadState.append.endOfPaginationReached &&
                    chargesPage.itemCount > 0
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
