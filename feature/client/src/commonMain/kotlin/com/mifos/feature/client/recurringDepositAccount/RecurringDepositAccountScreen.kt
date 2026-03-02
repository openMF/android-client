/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.recurringDepositAccount

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.client_empty_card_message
import androidclient.feature.client.generated.resources.client_product_recurring_deposit_account
import androidclient.feature.client.generated.resources.client_profile_recurring_deposit_account_title
import androidclient.feature.client.generated.resources.client_savings_item
import androidclient.feature.client.generated.resources.client_savings_not_available
import androidclient.feature.client.generated.resources.client_savings_pending_approval
import androidclient.feature.client.generated.resources.feature_client_dialog_action_ok
import androidclient.feature.client.generated.resources.filter
import androidclient.feature.client.generated.resources.search
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.Actions
import com.mifos.core.ui.components.MifosActionsSavingsListingComponent
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosEmptyCard
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosSearchBar
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme

@Composable
fun RecurringDepositAccountScreen(
    navController: NavController,
    navigateBack: () -> Unit,
    onApproveAccount: (String) -> Unit,
    onViewAccount: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecurringDepositAccountViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is RecurringDepositAccountEvent.OnApproveAccount -> {
                onApproveAccount(event.accountNumber)
            }

            RecurringDepositAccountEvent.OnNavigateBack -> navigateBack()
            is RecurringDepositAccountEvent.OnViewAccount -> {
                onViewAccount(event.accountNumber)
            }
        }
    }

    RecurringDepositAccountDialog(
        state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )

    RecurringDepositAccountContent(
        navController = navController,
        state = state,
        modifier = modifier,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RecurringDepositAccountDialog(
    state: RecurringDepositAccountState,
    onAction: (RecurringDepositAccountAction) -> Unit,
) {
    when (state.dialogState) {
        is RecurringDepositAccountState.DialogState.Error -> {
            AlertDialog(
                title = { Text("Error") },
                text = { Text(text = state.dialogState.message) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onAction(RecurringDepositAccountAction.CloseDialog)
                        },
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

@Composable
internal fun RecurringDepositAccountContent(
    navController: NavController,
    state: RecurringDepositAccountState,
    modifier: Modifier = Modifier,
    onAction: (RecurringDepositAccountAction) -> Unit,
) {
    var expandedIndex by rememberSaveable { mutableStateOf(-1) }

    Column(
        modifier = modifier
            .fillMaxSize(),
    ) {
        MifosBreadcrumbNavBar(navController)

        when (state.isLoading) {
            true -> MifosProgressIndicator()
            false -> {
                Column(
                    Modifier.fillMaxSize()
                        .padding(horizontal = KptTheme.spacing.md),
                ) {
                    val notAvailableText = stringResource(Res.string.client_savings_not_available)
                    RecurringDepositAccountHeader(
                        state.recurringDepositAccounts.size.toString(),
                        onToggleSearch = {
                            onAction(RecurringDepositAccountAction.ToggleSearch)
                        },
                        onToggleFilter = {
                            onAction(RecurringDepositAccountAction.ToggleFilter)
                        },
                    )

                    // todo implement search bar functionality
                    if (state.isSearchBarActive) {
                        MifosSearchBar(
                            query = state.searchText,
                            onQueryChange = {
                                onAction(RecurringDepositAccountAction.UpdateSearch(it))
                            },
                            onSearchClick = {
                                onAction(RecurringDepositAccountAction.Search)
                            },
                            onBackClick = {
                                onAction(RecurringDepositAccountAction.ToggleSearch)
                            },
                        )
                    }

                    Spacer(modifier = Modifier.height(KptTheme.spacing.lg))

                    if (state.recurringDepositAccounts.isEmpty()) {
                        MifosEmptyCard(msg = stringResource(Res.string.client_empty_card_message))
                    } else {
                        LazyColumn {
                            itemsIndexed(state.recurringDepositAccounts) { index, recurringDeposit ->
                                MifosActionsSavingsListingComponent(
                                    accountNo = recurringDeposit.accountNo ?: notAvailableText,
                                    savingsProduct = stringResource(Res.string.client_product_recurring_deposit_account),
                                    savingsProductName = recurringDeposit.shortProductName
                                        ?: notAvailableText,
                                    lastActive = if (recurringDeposit.status?.submittedAndPendingApproval == true) {
                                        stringResource(Res.string.client_savings_pending_approval)
                                    } else if (recurringDeposit.lastActiveTransactionDate != null) {
                                        DateHelper.getDateAsString(recurringDeposit.lastActiveTransactionDate!!)
                                    } else {
                                        notAvailableText
                                    },
                                    balance =
                                    if (recurringDeposit.accountBalance != null) {
                                        "${recurringDeposit.currency?.displaySymbol ?: ""} ${recurringDeposit.accountBalance}"
                                    } else {
                                        notAvailableText
                                    },
                                    isExpanded = expandedIndex == index,
                                    onExpandToggle = {
                                        expandedIndex = if (expandedIndex == index) -1 else index
                                    },
                                    menuList = if (recurringDeposit.status?.submittedAndPendingApproval == true) {
                                        listOf(
                                            Actions.ViewAccount(MifosIcons.Calendar),
                                            Actions.ApproveAccount(),
                                        )
                                    } else {
                                        listOf(
                                            Actions.ViewAccount(MifosIcons.Calendar),
                                        )
                                    },
                                ) { actions ->
                                    when (actions) {
                                        is Actions.ViewAccount -> {
                                            onAction(
                                                RecurringDepositAccountAction.ViewAccount(
                                                    recurringDeposit.accountNo ?: "",
                                                ),
                                            )
                                        }

                                        is Actions.ApproveAccount -> {
                                            RecurringDepositAccountAction.ApproveAccount(
                                                recurringDeposit.accountNo ?: "",
                                            )
                                        }

                                        else -> null
                                    }
                                }

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
private fun RecurringDepositAccountHeader(
    totalItem: String,
    onToggleFilter: () -> Unit,
    modifier: Modifier = Modifier,
    onToggleSearch: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Column {
            Text(
                text = stringResource(Res.string.client_profile_recurring_deposit_account_title),
                style = MifosTypography.titleMedium,
            )

            Text(
                text = totalItem + " " + stringResource(Res.string.client_savings_item),
                style = MifosTypography.labelMedium,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            painter = painterResource(Res.drawable.search),
            contentDescription = null,
            modifier = Modifier.clickable {
                onToggleSearch.invoke()
            },
        )

        Spacer(modifier = Modifier.width(DesignToken.spacing.largeIncreased))

        Icon(
            painter = painterResource(Res.drawable.filter),
            contentDescription = null,
            modifier = Modifier.clickable {
                onToggleFilter.invoke()
            },
        )
    }
}
