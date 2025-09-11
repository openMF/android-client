/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientLoanAccounts

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.cash_bundel
import androidclient.feature.client.generated.resources.client_savings_item
import androidclient.feature.client.generated.resources.feature_client_dialog_action_ok
import androidclient.feature.client.generated.resources.feature_client_loan_account
import androidclient.feature.client.generated.resources.filter
import androidclient.feature.client.generated.resources.search
import androidclient.feature.client.generated.resources.update_default_account_title
import androidclient.feature.client.generated.resources.wallet
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.Actions
import com.mifos.core.ui.components.MifosActionsLoanListingComponent
import com.mifos.core.ui.components.MifosEmptyCard
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosSearchBar
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ClientLoanAccountsScreenRoute(
    navigateBack: () -> Unit,
    makeRepayment: (Int) -> Unit,
    viewAccount: (Int) -> Unit,
    viewModel: ClientLoanAccountsViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is ClientLoanAccountsEvent.MakeRepayment -> makeRepayment(event.id)
            ClientLoanAccountsEvent.NavigateBack -> navigateBack()
            is ClientLoanAccountsEvent.ViewAccount -> viewAccount(event.id)
        }
    }

    ClientLoanAccountsDialog(
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )

    ClientLoanAccountsScreen(
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )
}

@Composable
private fun ClientLoanAccountsScreen(
    state: ClientLoanAccountsState,
    onAction: (ClientLoanAccountsAction) -> Unit,
) {
    MifosScaffold(
        title = stringResource(Res.string.update_default_account_title),
        onBackPressed = { onAction(ClientLoanAccountsAction.NavigateBack) },
        modifier = Modifier,
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
                .fillMaxSize()
                .padding(
                    start = DesignToken.padding.large,
                    end = DesignToken.padding.large,
                    top = DesignToken.padding.large,
                ),
        ) {
            ClientsAccountHeader(
                totalItem = state.loanAccounts.size.toString(),
                onAction = onAction,
            )

            if (state.isSearchBarActive) {
                MifosSearchBar(
                    query = state.searchText,
                    onQueryChange = { onAction.invoke(ClientLoanAccountsAction.UpdateSearchValue(it)) },
                    onSearchClick = { onAction.invoke(ClientLoanAccountsAction.OnSearchClick) },
                    onBackClick = { onAction.invoke(ClientLoanAccountsAction.ToggleSearch) },
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.padding.large))

            if (state.loanAccounts.isEmpty()) {
                MifosEmptyCard()
            } else {
                LazyColumn {
                    items(state.loanAccounts) { loan ->
                        val symbol = loan.currency?.displaySymbol ?: ""
                        MifosActionsLoanListingComponent(
                            accountNo = (loan.accountNo ?: "Not Available"),
                            loanProduct = loan.productName ?: "Not Available",
                            originalLoan = symbol + (
                                (loan.originalLoan ?: "Not Available").toString()
                                ),
                            amountPaid = symbol + ((loan.amountPaid ?: "Not Available").toString()),
                            loanBalance = symbol + (
                                (loan.amountPaid ?: "Not Available").toString()
                                ),
                            type = loan.loanType?.value ?: "Not Available",
                            // TODO check if we need to add other options as well, such as disburse and all
                            // currently didn't add it cuz its not in the UI design
                            menuList = when {
                                loan.status?.active == true -> {
                                    listOf(
                                        Actions.ViewAccount(
                                            vectorResource(Res.drawable.wallet),
                                        ),
                                        Actions.MakeRepayment(
                                            vectorResource(Res.drawable.cash_bundel),
                                        ),
                                    )
                                }

                                else -> {
                                    listOf(
                                        Actions.ViewAccount(
                                            vectorResource(Res.drawable.wallet),
                                        ),
                                    )
                                }
                            },
                            onActionClicked = { actions ->
                                when (actions) {
                                    is Actions.ViewAccount -> onAction(ClientLoanAccountsAction.ViewAccount)
                                    is Actions.MakeRepayment -> onAction(ClientLoanAccountsAction.MakeRepayment)
                                    else -> null
                                }
                            },
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ClientsAccountHeader(
    totalItem: String,
    onAction: (ClientLoanAccountsAction) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            Text(
                text = stringResource(Res.string.feature_client_loan_account),
                style = MifosTypography.titleMedium,
            )

            Text(
                text = totalItem + " " + stringResource(Res.string.client_savings_item),
                style = MifosTypography.labelMedium,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = { onAction.invoke(ClientLoanAccountsAction.ToggleSearch) },
        ) {
            // add a cross icon when its active, talk with design team
            Icon(
                painter = painterResource(Res.drawable.search),
                contentDescription = null,
            )
        }

        IconButton(
            onClick = { onAction.invoke(ClientLoanAccountsAction.ToggleFilter) },
        ) {
            Icon(
                painter = painterResource(Res.drawable.filter),
                contentDescription = null,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClientLoanAccountsDialog(
    state: ClientLoanAccountsState,
    onAction: (ClientLoanAccountsAction) -> Unit,
) {
    when (state.dialogState) {
        is ClientLoanAccountsState.DialogState.Error -> {
            AlertDialog(
                title = { Text("Error") },
                text = { Text(text = state.dialogState.message) },
                confirmButton = {
                    TextButton(
                        onClick = { onAction.invoke(ClientLoanAccountsAction.CloseDialog) },
                    ) {
                        Text(stringResource(Res.string.feature_client_dialog_action_ok))
                    }
                },
                onDismissRequest = {},
            )
        }

        ClientLoanAccountsState.DialogState.Loading -> MifosProgressIndicator()

        else -> null
    }
}
