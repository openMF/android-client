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
import androidx.compose.foundation.lazy.items
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
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ClientLoanAccountsScreenRoute(
    navigateBack: () -> Unit,
    makeRepayment: (Int) -> Unit,
    viewAccount: (Int) -> Unit,
    navController: NavController,
    viewModel: ClientLoanAccountsViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is ClientLoanAccountsEvent.MakeRepayment -> makeRepayment(event.id)
            ClientLoanAccountsEvent.NavigateBack -> navigateBack()
            is ClientLoanAccountsEvent.ViewAccount -> viewAccount(event.id)
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
        modifier = modifier
            .fillMaxSize(),
    ) {
        MifosBreadcrumbNavBar(navController)

        when (state.isLoading) {
            true -> MifosProgressIndicator()
            false -> {
                Column(
                    modifier = Modifier.fillMaxSize()
                        .padding(horizontal = KptTheme.spacing.md),
                ) {
                    ClientsAccountHeader(
                        totalItem = state.loanAccounts.size.toString(),
                        onAction = onAction,
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

                    if (state.loanAccounts.isEmpty()) {
                        MifosEmptyCard()
                    } else {
                        LazyColumn {
                            items(state.loanAccounts) { loan ->
                                val symbol = loan.currency?.displaySymbol ?: ""
                                MifosActionsLoanListingComponent(
                                    accountNo = (
                                        loan.accountNo ?: stringResource(
                                            Res.string.client_loan_accounts_not_available,
                                        )
                                        ),
                                    loanProduct = loan.productName ?: stringResource(Res.string.client_loan_accounts_not_available),
                                    originalLoan = symbol + (
                                        (loan.originalLoan ?: stringResource(Res.string.client_loan_accounts_not_available)).toString()
                                        ),
                                    amountPaid = symbol + (
                                        (
                                            if (loan.status?.pendingApproval == true) {
                                                stringResource(Res.string.client_loan_accounts_not_available)
                                            } else {
                                                (
                                                    loan.amountPaid
                                                        ?: 0.0
                                                    ).toString()
                                            }
                                            )
                                        ),
                                    loanBalance = symbol + (
                                        (
                                            if (loan.status?.pendingApproval == true) {
                                                stringResource(Res.string.client_loan_accounts_not_available)
                                            } else {
                                                (
                                                    loan.loanBalance
                                                        ?: 0.0
                                                    ).toString()
                                            }
                                            )
                                        ),
                                    type = loan.loanType?.value ?: stringResource(Res.string.client_loan_accounts_not_available),
                                    status = loan.status?.value ?: stringResource(Res.string.client_loan_accounts_not_available),
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
                                            is Actions.ViewAccount -> onAction(
                                                ClientLoanAccountsAction.ViewAccount(loan.id ?: 0),
                                            )
                                            is Actions.MakeRepayment -> onAction(
                                                ClientLoanAccountsAction.MakeRepayment,
                                            )

                                            else -> null
                                        }
                                    },
                                )

                                Spacer(modifier = Modifier.height(KptTheme.spacing.sm))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClientsAccountHeader(
    totalItem: String,
    isFilterActive: Boolean,
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
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = DesignToken.padding.medium, end = KptTheme.spacing.md)
                        .size(DesignToken.sizes.iconMinyMiny)
                        .clip(CircleShape)
                        .background(KptTheme.colorScheme.error),
                )
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

        else -> null
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(DesignToken.padding.medium),
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
