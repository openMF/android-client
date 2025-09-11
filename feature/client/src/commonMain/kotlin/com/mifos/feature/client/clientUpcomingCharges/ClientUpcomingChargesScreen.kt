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
import androidclient.feature.client.generated.resources.client_savings_item
import androidclient.feature.client.generated.resources.client_upcoming_charges_charges_overview
import androidclient.feature.client.generated.resources.filter
import androidclient.feature.client.generated.resources.search
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.PagingData
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.designsystem.utils.onClick
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosEmptyCard
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.util.EventsEffect
import com.mifos.room.entities.client.ChargesEntity
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

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
    navController: NavController,
    onAction: (ClientUpcomingChargesAction) -> Unit,
) {
    var itemCount by rememberSaveable { mutableStateOf(0) }
    MifosScaffold(
        title = "Client Upcoming Charges",
        onBackPressed = {},
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding),
        ) {
            MifosBreadcrumbNavBar(navController)

            when (state.isLoading) {
                true -> MifosProgressIndicator()
                false -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                            .padding(horizontal = DesignToken.padding.large),
                    ) {
                        UpcomingChargesHeader(
                            totalItem = itemCount.toString(),
                            onAction = onAction,
                        )

                        Spacer(modifier = Modifier.height(DesignToken.padding.large))

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
expect fun ChargesListContent(
    charges: Flow<PagingData<ChargesEntity>>,
    state: ClientUpcomingChargesState,
    onAction: (ClientUpcomingChargesAction) -> Unit,
    setCount: (Int) -> Unit,
    refresh: () -> Unit,
)
