/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientUpcomingCharges

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.client_upcoming_charges_failed_message
import androidclient.feature.client.generated.resources.client_upcoming_charges_no_more_charges_available
import androidclient.feature.client.generated.resources.string_not_available
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.ui.components.Actions
import com.mifos.core.ui.components.MifosActionsClientFeeListingComponent
import com.mifos.core.ui.components.MifosPagingAppendProgress
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.room.entities.client.ChargesEntity
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun ChargesListContent(
    charges: Flow<PagingData<ChargesEntity>>,
    state: ClientUpcomingChargesState,
    onAction: (ClientUpcomingChargesAction) -> Unit,
    setCount: (Int) -> Unit,
    refresh: () -> Unit,
) {
    val chargesPagingList = charges.collectAsLazyPagingItems()

    when (chargesPagingList.loadState.refresh) {
        is LoadState.Error -> MifosSweetError(
            message = stringResource(Res.string.client_upcoming_charges_failed_message),
            onclick = refresh,
        )

        LoadState.Loading -> MifosProgressIndicator()

        is LoadState.NotLoading -> Unit
    }

    LaunchedEffect(chargesPagingList) {
        setCount.invoke(chargesPagingList.itemCount)
    }

    LazyColumn {
        items(
            count = chargesPagingList.itemCount,
            key = { index -> chargesPagingList[index]?.id ?: index },
        ) { index ->
            chargesPagingList[index]?.let { charge ->
                MifosActionsClientFeeListingComponent(
                    name = charge.name ?: stringResource(Res.string.string_not_available),
                    dueAsOf = if (charge.dueDate != null) {
                        DateHelper.getDateAsString(charge.dueDate!!)
                    } else {
                        stringResource(Res.string.string_not_available)
                    },
                    // todo check if its the right way to get due
                    due = if (charge.amount != null && charge.amountPaid != null) {
                        (charge.amount!! - charge.amountPaid!!).toString()
                    } else {
                        stringResource(Res.string.string_not_available)
                    },
                    paid = charge.amountPaid.toString(),
                    waived = charge.amountWaived.toString(),
                    outstanding = charge.amountOutstanding.toString(),
                    menuList = listOf(
                        Actions.PayOutstandingAmount(),
                    ),
                    isActive = index == state.expandedItemIndex,
                    onClick = { ClientUpcomingChargesAction.CardClicked(index) },
                    onActionClicked = { actions ->
                        when (actions) {
                            is Actions.PayOutstandingAmount -> {
                                ClientUpcomingChargesAction.PayOutstandingAmount
                            }

                            else -> {}
                        }
                    },
                )
                Spacer(modifier = Modifier.padding(bottom = DesignToken.padding.large))
            }
        }

        when (chargesPagingList.loadState.append) {
            is LoadState.Error -> {
                item {
                    MifosSweetError(message = org.jetbrains.compose.resources.stringResource(Res.string.client_upcoming_charges_failed_message)) {
                        refresh()
                    }
                }
            }

            is LoadState.Loading -> {
                item {
                    MifosPagingAppendProgress()
                }
            }

            is LoadState.NotLoading -> {
                if (chargesPagingList.loadState.append.endOfPaginationReached &&
                    chargesPagingList.itemCount > 0
                ) {
                    item {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(6.dp),
                            text = stringResource(Res.string.client_upcoming_charges_no_more_charges_available),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}
