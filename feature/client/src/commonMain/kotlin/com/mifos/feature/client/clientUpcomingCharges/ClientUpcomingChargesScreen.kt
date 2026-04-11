/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientUpcomingCharges

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.client_savings_item
import androidclient.feature.client.generated.resources.client_upcoming_charges_charges_overview
import androidclient.feature.client.generated.resources.client_upcoming_charges_failed_message
import androidclient.feature.client.generated.resources.client_upcoming_charges_no_more_charges_available
import androidclient.feature.client.generated.resources.filter
import androidclient.feature.client.generated.resources.search
import androidclient.feature.client.generated.resources.string_not_available
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.designsystem.utils.onClick
import com.mifos.core.ui.components.Actions
import com.mifos.core.ui.components.MifosActionsClientFeeListingComponent
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosEmptyCard
import com.mifos.core.ui.components.MifosPagingAppendProgress
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.util.EventsEffect
import com.mifos.room.entities.client.ChargesEntity
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme

@Composable
fun ClientUpcomingChargesScreenRoute(
    payOutstandingAmount: () -> Unit,
    navController: NavController,
    viewModel: ClientUpcomingChargesViewmodel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ClientUpcomingChargesEvent.PayOutstandingAmount -> payOutstandingAmount()
        }
    }

    ClientUpcomingChargesScreen(
        state = state,
        navController = navController,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )

    ClientUpcomingChargesDialog(
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )
}

@Composable
fun ClientUpcomingChargesScreen(
    state: ClientUpcomingChargesState,
    modifier: Modifier = Modifier,
    navController: NavController,
    onAction: (ClientUpcomingChargesAction) -> Unit,
) {
    var itemCount by rememberSaveable { mutableStateOf(0) }

    Column(
        modifier = modifier,
    ) {
        MifosBreadcrumbNavBar(navController)

        when (state.isLoading) {
            true -> MifosProgressIndicator()
            false -> {
                Column(
                    modifier = Modifier.fillMaxSize()
                        .padding(horizontal = KptTheme.spacing.md),
                ) {
                    UpcomingChargesHeader(
                        totalItem = itemCount.toString(),
                        onAction = onAction,
                    )

                    Spacer(modifier = Modifier.height(KptTheme.spacing.md))

                    if (state.chargesFlow == null) {
                        MifosEmptyCard()
                    } else {
                        ChargesListContent(
                            state = state,
                            charges = state.chargesFlow,
                            onAction = onAction,
                            refresh = {
                                onAction(ClientUpcomingChargesAction.OnRefresh)
                            },
                            setCount = { itemCount = it },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClientUpcomingChargesDialog(
    state: ClientUpcomingChargesState,
    onAction: (ClientUpcomingChargesAction) -> Unit,
) {
    when (state.dialogState) {
        is ClientUpcomingChargesState.DialogState.Error -> {
            MifosSweetError(
                message = state.dialogState.message,
                onclick = { onAction.invoke(ClientUpcomingChargesAction.OnRefresh) },
            )
        }

        null -> {}
    }
}

@Composable
private fun UpcomingChargesHeader(
    totalItem: String,
    onAction: (ClientUpcomingChargesAction) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            Text(
                text = stringResource(Res.string.client_upcoming_charges_charges_overview),
                style = MifosTypography.titleMedium,
            )

            Text(
                text = totalItem + " " + stringResource(Res.string.client_savings_item),
                style = MifosTypography.labelMedium,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            modifier = Modifier.onClick {
                onAction.invoke(ClientUpcomingChargesAction.ToggleSearch)
            },
            painter = painterResource(Res.drawable.search),
            contentDescription = null,
        )

        Spacer(modifier = Modifier.width(DesignToken.padding.largeIncreased))

        Icon(
            modifier = Modifier.onClick {
                onAction.invoke(ClientUpcomingChargesAction.ToggleFilter)
            },
            painter = painterResource(Res.drawable.filter),
            contentDescription = null,
        )
    }
}

@Composable
fun ChargesListContent(
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

    LaunchedEffect(chargesPagingList.itemCount) {
        setCount.invoke(chargesPagingList.itemCount)
    }

    if (chargesPagingList.loadState.refresh is LoadState.NotLoading && chargesPagingList.itemCount == 0) {
        MifosEmptyCard()
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
                    onClick = { onAction(ClientUpcomingChargesAction.CardClicked(index)) },
                    onActionClicked = { actions ->
                        when (actions) {
                            is Actions.PayOutstandingAmount -> {
                                onAction(ClientUpcomingChargesAction.PayOutstandingAmount)
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
                        chargesPagingList.retry()
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
                                .padding(KptTheme.spacing.sm),
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
