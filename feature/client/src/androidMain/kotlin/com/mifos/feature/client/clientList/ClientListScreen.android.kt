/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientList

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_failed_to_fetch_clients
import androidclient.feature.client.generated.resources.feature_client_failed_to_more_clients
import androidclient.feature.client.generated.resources.feature_client_ic_done_all_black_24dp
import androidclient.feature.client.generated.resources.feature_client_ic_dp_placeholder
import androidclient.feature.client.generated.resources.feature_client_no_more_clients_available
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.ui.components.MifosPagingAppendProgress
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.room.entities.client.ClientEntity
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal actual fun LazyColumnForClientListApi(
    pagingFlow: Flow<PagingData<ClientEntity>>,
    isInSelectionMode: Boolean,
    selectedItems: ClientSelectionState,
    failedRefresh: () -> Unit,
    onClientSelect: (Int) -> Unit,
    selectedMode: () -> Unit,
) {
    val clientPagingList = pagingFlow.collectAsLazyPagingItems()
    when (clientPagingList.loadState.refresh) {
        is LoadState.Error -> {
            MifosSweetError(message = stringResource(Res.string.feature_client_failed_to_fetch_clients)) {
                failedRefresh()
            }
        }

        is LoadState.Loading -> MifosProgressIndicator()

        is LoadState.NotLoading -> Unit
    }

    LazyColumn {
        items(
            count = clientPagingList.itemCount,
            key = { index -> clientPagingList[index]?.id ?: index },
        ) { index ->
            clientPagingList[index]?.let { client ->
                val isSelected = selectedItems.contains(client)
                val surfaceColor = MaterialTheme.colorScheme.surface
                val outlineColor = MaterialTheme.colorScheme.outline
                val cardColor = if (isSelected) outlineColor else surfaceColor

                OutlinedCard(
                    modifier = Modifier
                        .padding(6.dp)
                        .combinedClickable(
                            onClick = {
                                if (isInSelectionMode) {
                                    if (isSelected) {
                                        selectedItems.remove(client)
                                    } else {
                                        selectedItems.add(client)
                                    }
                                } else {
                                    onClientSelect(client.id)
                                }
                            },
                            onLongClick = {
                                if (!isInSelectionMode) {
                                    selectedMode()
                                }
                                if (isSelected) {
                                    selectedItems.remove(client)
                                } else {
                                    selectedItems.add(client)
                                }
                            },
                        ),
                    colors = CardDefaults.outlinedCardColors(containerColor = cardColor),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.feature_client_ic_dp_placeholder),
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp),
                        ) {
                            Text(
                                text = client.displayName ?: "",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            Text(
                                text = client.accountNo ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                        if (client.sync) {
                            Image(
                                painter = painterResource(Res.drawable.feature_client_ic_done_all_black_24dp),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                }
            }
        }

        when (clientPagingList.loadState.append) {
            is LoadState.Error -> {
                item {
                    MifosSweetError(message = stringResource(Res.string.feature_client_failed_to_more_clients)) {
                        failedRefresh()
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
                                .padding(6.dp),
                            text = stringResource(Res.string.feature_client_no_more_clients_available),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}
