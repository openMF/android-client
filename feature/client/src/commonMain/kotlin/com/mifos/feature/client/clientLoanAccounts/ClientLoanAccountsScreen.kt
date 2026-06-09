/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientLoanAccounts

import androidclient.core.ui.generated.resources.send_money
import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.add_icon
import androidclient.feature.client.generated.resources.cash_bundel
import androidclient.feature.client.generated.resources.client_loan_accounts_not_available
import androidclient.feature.client.generated.resources.client_savings_item
import androidclient.feature.client.generated.resources.feature_client_account_status
import androidclient.feature.client.generated.resources.feature_client_dialog_action_ok
import androidclient.feature.client.generated.resources.feature_client_filters
import androidclient.feature.client.generated.resources.feature_client_loan_account
import androidclient.feature.client.generated.resources.feature_client_status_active
import androidclient.feature.client.generated.resources.feature_client_status_closed
import androidclient.feature.client.generated.resources.feature_client_status_overpaid
import androidclient.feature.client.generated.resources.feature_client_status_pending
import androidclient.feature.client.generated.resources.feature_loan_account_empty_list_message
import androidclient.feature.client.generated.resources.filter
import androidclient.feature.client.generated.resources.search
import androidclient.feature.client.generated.resources.wallet
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.Actions
import com.mifos.core.ui.components.MifosActionsLoanListingComponent
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosEmptyCard
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosSearchBar
import com.mifos.core.ui.util.EventsEffect
import com.mifos.room.entities.accounts.loans.LoanAccountEntity
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme
import androidclient.core.ui.generated.resources.Res as UiRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ClientLoanAccountsScreenRoute(
    navigateBack: () -> Unit,
    makeRepayment: (Int) -> Unit,
    viewAccount: (Int) -> Unit,
    transferFund: (Int) -> Unit,
    navController: NavController,
    createAccount: (Int, String) -> Unit,
    viewModel: ClientLoanAccountsViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is ClientLoanAccountsEvent.MakeRepayment -> makeRepayment(event.id)
            ClientLoanAccountsEvent.NavigateBack -> navigateBack()
            is ClientLoanAccountsEvent.ViewAccount -> viewAccount(event.id)
            is ClientLoanAccountsEvent.AddAccount -> createAccount(event.clientId, event.accountNo)
            is ClientLoanAccountsEvent.TransferFund -> transferFund(event.loanId)
        }
    }

    ClientLoanAccountsScreen(
        state = state,
        navController = navController,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )

    ClientLoanAccountsDialog(
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )
    if (state.isFilterDialogOpen) {
        FilterBottomSheet(
            onDismissRequest = { viewModel.trySendAction(ClientLoanAccountsAction.ToggleFilter) },
            sheetState = sheetState,
            selectedStatuses = state.selectedStatus,
            handleFilterClick = { status ->
                viewModel.trySendAction(ClientLoanAccountsAction.HandleFilterClick(status))
            },
            clearFilters = { viewModel.trySendAction(ClientLoanAccountsAction.ClearFilters) },
        )
    }
}

@Composable
private fun ClientLoanAccountsScreen(
    state: ClientLoanAccountsState,
    modifier: Modifier = Modifier,
    navController: NavController,
    onAction: (ClientLoanAccountsAction) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        MifosBreadcrumbNavBar(navController)

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = KptTheme.spacing.md),
        ) {
            ClientsAccountHeader(
                totalItem = state.loanAccounts.size.toString(),
                onAction = onAction,
                isLoanScreenEmpty = state.loanAccounts.isEmpty(),
                isFilterActive = state.selectedStatus.isNotEmpty(),
            )

            if (state.isSearchBarActive) {
                MifosSearchBar(
                    query = state.searchText,
                    onQueryChange = {
                        onAction.invoke(
                            ClientLoanAccountsAction.UpdateSearchValue(
                                it,
                            ),
                        )
                    },
                    onSearchClick = { onAction.invoke(ClientLoanAccountsAction.OnSearchClick) },
                    onBackClick = { onAction.invoke(ClientLoanAccountsAction.ToggleSearch) },
                )
            }

            Spacer(modifier = Modifier.height(KptTheme.spacing.md))

            LoanAccountsList(
                onAction = onAction,
                loanAccounts = state.loanAccounts,
            )
        }
    }
}

@Composable
fun LoanAccountsList(
    loanAccounts: List<LoanAccountEntity>,
    onAction: (ClientLoanAccountsAction) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    if (loanAccounts.isEmpty()) {
        MifosEmptyCard(
            msg = stringResource(Res.string.feature_loan_account_empty_list_message),
            isButtonPresent = true,
            onClick = { onAction.invoke(ClientLoanAccountsAction.AddAccount) },
        )
    } else {
        LazyColumn(
            modifier = modifier,
            state = listState,
        ) {
            items(
                items = loanAccounts,
                key = { loan -> loan.id ?: loan.accountNo.orEmpty() },
            ) { loan ->

                LoanAccountItem(
                    loan = loan,
                    onAction = onAction,
                )

                Spacer(
                    modifier = Modifier.height(KptTheme.spacing.sm),
                )
            }
        }
    }
}

@Composable
private fun LoanAccountItem(
    loan: LoanAccountEntity,
    onAction: (ClientLoanAccountsAction) -> Unit,
) {
    val symbol = loan.currency?.displaySymbol.orEmpty()

    val notAvailable =
        stringResource(Res.string.client_loan_accounts_not_available)

    MifosActionsLoanListingComponent(
        accountNo = loan.accountNo ?: stringResource(Res.string.client_loan_accounts_not_available),

        loanProduct = loan.productName
            ?: stringResource(Res.string.client_loan_accounts_not_available),

        originalLoan = symbol + (
            loan.originalLoan?.toString()
                ?: stringResource(Res.string.client_loan_accounts_not_available)
            ),

        amountPaid = symbol + (
            if (loan.status?.pendingApproval == true) {
                stringResource(Res.string.client_loan_accounts_not_available)
            } else {
                (loan.amountPaid ?: 0.0).toString()
            }
            ),

        loanBalance = symbol + (
            if (loan.status?.pendingApproval == true) {
                stringResource(Res.string.client_loan_accounts_not_available)
            } else {
                (loan.loanBalance ?: 0.0).toString()
            }
            ),

        type = loan.loanType?.value ?: notAvailable,

        status = loan.status?.value ?: notAvailable,

        menuList = buildLoanActions(loan),

        onActionClicked = { action ->
            when (action) {
                is Actions.ViewAccount -> {
                    onAction(
                        ClientLoanAccountsAction.ViewAccount(
                            loan.id,
                        ),
                    )
                }

                is Actions.MakeRepayment -> {
                    onAction(
                        ClientLoanAccountsAction.MakeRepayment(
                            loan.id,
                        ),
                    )
                }

                is Actions.TransferFund -> {
                    onAction(
                        ClientLoanAccountsAction.TransferFund(
                            loanId = loan.id,
                        ),
                    )
                }

                else -> Unit
            }
        },
    )
}

@Composable
private fun buildLoanActions(
    loan: LoanAccountEntity,
): List<Actions> = buildList {
    add(
        Actions.ViewAccount(
            vectorResource(Res.drawable.wallet),
        ),
    )

    when {
        loan.status?.active == true -> {
            add(
                Actions.MakeRepayment(
                    vectorResource(Res.drawable.cash_bundel),
                ),
            )
        }

        loan.status?.overpaid == true -> {
            add(
                Actions.TransferFund(
                    vectorResource(UiRes.drawable.send_money),
                ),
            )
        }
    }
}

@Composable
private fun ClientsAccountHeader(
    totalItem: String,
    isFilterActive: Boolean,
    onAction: (ClientLoanAccountsAction) -> Unit,
    isLoanScreenEmpty: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
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

        if (!isLoanScreenEmpty) {
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
                onClick = { onAction.invoke(ClientLoanAccountsAction.AddAccount) },
            ) {
                Icon(
                    painter = painterResource(Res.drawable.add_icon),
                    contentDescription = null,
                )
            }
            Box(
                modifier = Modifier.wrapContentSize(),
                contentAlignment = Alignment.Center,
            ) {
                IconButton(
                    onClick = { onAction.invoke(ClientLoanAccountsAction.ToggleFilter) },
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.filter),
                        contentDescription = null,
                    )
                }

                if (isFilterActive) {
                    Box(
                        modifier = Modifier.align(Alignment.TopEnd)
                            .padding(top = DesignToken.padding.medium, end = KptTheme.spacing.md)
                            .size(DesignToken.sizes.iconMinyMiny).clip(CircleShape)
                            .background(KptTheme.colorScheme.error),
                    )
                }
            }
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

        ClientLoanAccountsState.DialogState.Loading -> {
            MifosProgressIndicator()
        }

        null -> Unit
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBottomSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    handleFilterClick: (LoanStatusFilter) -> Unit,
    selectedStatuses: Set<LoanStatusFilter>,
    clearFilters: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = KptTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier.padding(KptTheme.spacing.md),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(DesignToken.padding.medium),
            ) {
                Text(
                    text = stringResource(Res.string.feature_client_filters),
                    style = MifosTypography.titleLargeEmphasized,
                    color = KptTheme.colorScheme.primary,
                )
                Row {
                    IconButton(
                        onClick = {
                            clearFilters()
                            onDismissRequest()
                        },
                    ) {
                        Icon(
                            imageVector = MifosIcons.Redo,
                            contentDescription = "Clear",
                        )
                    }

                    IconButton(
                        onClick = onDismissRequest,
                    ) {
                        Icon(
                            imageVector = MifosIcons.Check,
                            contentDescription = "Apply",
                        )
                    }
                }
            }
        }

        HorizontalDivider(Modifier.fillMaxWidth(), thickness = 1.5.dp)
        Column(modifier = Modifier.padding(DesignToken.padding.medium)) {
            Text(
                text = stringResource(Res.string.feature_client_account_status),
                style = MifosTypography.titleMediumEmphasized,
                modifier = Modifier.padding(bottom = KptTheme.spacing.sm),
            )

            LoanStatusFilter.entries.forEach { status ->
                val isChecked = selectedStatuses.contains(status)
                val statusLabel = when (status) {
                    LoanStatusFilter.ACTIVE -> stringResource(Res.string.feature_client_status_active)
                    LoanStatusFilter.PENDING -> stringResource(Res.string.feature_client_status_pending)
                    LoanStatusFilter.CLOSED -> stringResource(Res.string.feature_client_status_closed)
                    LoanStatusFilter.OVERPAID -> stringResource(Res.string.feature_client_status_overpaid)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(modifier = Modifier.width(DesignToken.padding.medium))
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { handleFilterClick(status) },
                    )
                    Text(text = statusLabel)
                }
            }
        }
    }
}
