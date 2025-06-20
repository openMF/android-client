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
import androidclient.feature.client.generated.resources.feature_client_ic_done_all_black_24dp
import androidclient.feature.client.generated.resources.feature_client_ic_dp_placeholder
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosPagingAppendProgress
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.room.entities.client.ClientEntity
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.painterResource

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
            MifosSweetError(message = "Failed to Fetch Clients") {
                failedRefresh()
            }
        }

        is LoadState.Loading -> MifosCircularProgress()

        is LoadState.NotLoading -> Unit
    }

    LazyColumn {
        items(clientPagingList.itemCount) { index ->

            val isSelected = clientPagingList[index]?.let { selectedItems.contains(it) }

            val surfaceColor = MaterialTheme.colorScheme.surface
            val outlineColor = MaterialTheme.colorScheme.outline
            var cardColor by remember { mutableStateOf(surfaceColor) }

            OutlinedCard(
                modifier = Modifier
                    .padding(6.dp)
                    .combinedClickable(
                        onClick = {
                            if (isInSelectionMode) {
                                cardColor = if (isSelected == true) {
                                    clientPagingList[index]?.let { selectedItems.remove(it) }
                                    surfaceColor
                                } else {
                                    clientPagingList[index]?.let { selectedItems.add(it) }
                                    outlineColor
                                }
                            } else {
                                clientPagingList[index]?.id?.let { onClientSelect(it) }
                            }
                        },
                        onLongClick = {
                            if (isInSelectionMode) {
                                cardColor = if (isSelected == true) {
                                    clientPagingList[index]?.let { selectedItems.remove(it) }
                                    surfaceColor
                                } else {
                                    clientPagingList[index]?.let { selectedItems.add(it) }
                                    outlineColor
                                }
                            } else {
                                selectedMode()
                                clientPagingList[index]?.let { selectedItems.add(it) }
                                cardColor = outlineColor
                            }
                        },
                    ),
                colors = CardDefaults.outlinedCardColors(containerColor = cardColor),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 24.dp,
                            bottom = 24.dp,
                        ),
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
                        clientPagingList[index]?.displayName?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                        Text(
                            text = clientPagingList[index]?.accountNo.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    if (clientPagingList[index]?.sync == true) {
                        Image(
                            painter = painterResource(Res.drawable.feature_client_ic_done_all_black_24dp),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }

        when (clientPagingList.loadState.append) {
            is LoadState.Error -> {
            }

            is LoadState.Loading -> {
                item {
                    MifosPagingAppendProgress()
                }
            }

            is LoadState.NotLoading -> Unit
        }
        when (clientPagingList.loadState.append.endOfPaginationReached) {
            true -> {
                item {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        text = "No More Clients Available !",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            false -> Unit
        }
    }
}
