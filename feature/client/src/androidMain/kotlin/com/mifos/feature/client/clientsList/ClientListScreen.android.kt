/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientsList

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_failed_to_fetch_clients
import androidclient.feature.client.generated.resources.feature_client_failed_to_more_clients
import androidclient.feature.client.generated.resources.feature_client_no_more_clients_available
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosPagingAppendProgress
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.room.entities.client.ClientEntity
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource

@Composable
internal actual fun LazyColumnForClientListApi(
    pagingFlow: Flow<PagingData<ClientEntity>>,
    onRefresh: () -> Unit,
    onClientSelect: (Int) -> Unit,
    fetchImage: (Int) -> Unit,
    images: Map<Int, ByteArray?>,
    modifier: Modifier,
) {
    val clientPagingList = pagingFlow.collectAsLazyPagingItems()
    when (clientPagingList.loadState.refresh) {
        is LoadState.Error -> {
            MifosSweetError(message = stringResource(Res.string.feature_client_failed_to_fetch_clients)) {
                onRefresh()
            }
        }

        is LoadState.Loading -> MifosCircularProgress()

        is LoadState.NotLoading -> Unit
    }

    LazyColumn(
        modifier = modifier,
    ) {
        items(
            count = clientPagingList.itemCount,
            key = { index -> clientPagingList[index]?.id ?: index },
        ) { index ->
            clientPagingList[index]?.let { client ->
                LaunchedEffect(client.id) {
                    fetchImage(client.id)
                }
                ClientItem(
                    client = client,
                    byteArray = images[client.id],
                    onClientClick = onClientSelect,
                )
            }
        }

        when (clientPagingList.loadState.append) {
            is LoadState.Error -> {
                item {
                    MifosSweetError(message = stringResource(Res.string.feature_client_failed_to_more_clients)) {
                        onRefresh()
                    }
                }
            }

            is LoadState.Loading -> {
                item {
                    MifosPagingAppendProgress()
                }
            }

            is LoadState.NotLoading -> {
                if (clientPagingList.loadState.append.endOfPaginationReached &&
                    clientPagingList.itemCount > 0
                ) {
                    item {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = DesignToken.padding.extraExtraLarge)
                                .padding(bottom = DesignToken.padding.extraExtraLarge),
                            text = stringResource(Res.string.feature_client_no_more_clients_available),
                            style = MifosTypography.bodyMedium,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}
