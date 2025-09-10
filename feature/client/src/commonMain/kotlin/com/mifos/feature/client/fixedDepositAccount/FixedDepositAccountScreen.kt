/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.fixedDepositAccount

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.client_empty_card_message
import androidclient.feature.client.generated.resources.client_product_fixed_deposit_account
import androidclient.feature.client.generated.resources.client_profile_fixed_deposit_account_title
import androidclient.feature.client.generated.resources.client_savings_item
import androidclient.feature.client.generated.resources.client_savings_not_avilable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosScaffold
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

@Composable
fun FixedDepositAccountScreen(
    navController: NavController,
    navigateBack: () -> Unit,
    onApproveAccount: (String) -> Unit,
    onViewAccount: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FixedDepositAccountViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is FixedDepositAccountEvent.OnApproveAccount -> {
                onApproveAccount(event.accountNumber)
            }

            FixedDepositAccountEvent.OnNavigateBack -> navigateBack()
            is FixedDepositAccountEvent.OnViewAccount -> {
                onViewAccount(event.accountNumber)
            }
        }
    }

    FixedDepositAccountDialog(
        state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )

    FixedDepositAccountScaffold(
        navController,
        state = state,
        modifier = modifier,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FixedDepositAccountDialog(
    state: FixedDepositAccountState,
    onAction: (FixedDepositAccountAction) -> Unit,
) {
    when (state.dialogState) {
        is FixedDepositAccountState.DialogState.Error -> {
            AlertDialog(
                title = { Text("Error") },
                text = { Text(text = state.dialogState.message) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onAction(FixedDepositAccountAction.CloseDialog)
                        },
                    ) {
                        Text(stringResource(Res.string.feature_client_dialog_action_ok))
                    }
                },
                onDismissRequest = {},
            )
        }

        FixedDepositAccountState.DialogState.Loading -> MifosProgressIndicator()

        else -> null
    }
}

@Composable
fun FixedDepositAccountScaffold(
    navController: NavController,
    state: FixedDepositAccountState,
    modifier: Modifier = Modifier,
    onAction: (FixedDepositAccountAction) -> Unit,
) {
    MifosScaffold(
        onBackPressed = {
            onAction(FixedDepositAccountAction.NavigateBack)
        },
        modifier = modifier,
        title = "",
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            MifosBreadcrumbNavBar(navController)
            Column(
                Modifier.fillMaxSize()
                    .padding(
                        horizontal = DesignToken.padding.large,
                    ),
            ) {
                val notAvailableText = stringResource(Res.string.client_savings_not_avilable)

                FixedDepositAccountHeader(
                    state.fixedDepositAccount.size.toString(),
                    onToggleSearch = {
                        onAction(FixedDepositAccountAction.ToggleSearch)
                    },
                    onToggleFilter = {
                        onAction(FixedDepositAccountAction.ToggleFilter)
                    },
                )

                // todo implement search bar functionality
                if (state.isSearchBarActive) {
                    MifosSearchBar(
                        query = state.searchText,
                        onQueryChange = {
                            onAction(FixedDepositAccountAction.UpdateSearch(it))
                        },
                        onSearchClick = {
                            onAction(FixedDepositAccountAction.Search)
                        },
                        onBackClick = {
                            onAction(FixedDepositAccountAction.ToggleSearch)
                        },
                    )
                }

                Spacer(modifier = Modifier.height(DesignToken.padding.largeIncreasedExtra))

                if (state.fixedDepositAccount.isEmpty()) {
                    MifosEmptyCard(msg = stringResource(Res.string.client_empty_card_message))
                } else {
                    LazyColumn {
                        items(state.fixedDepositAccount) { fixedDepositAccount ->
                            MifosActionsSavingsListingComponent(
                                accountNo = fixedDepositAccount.accountNo ?: notAvailableText,
                                savingsProduct = stringResource(Res.string.client_product_fixed_deposit_account),
                                savingsProductName = fixedDepositAccount.shortProductName
                                    ?: notAvailableText,
                                lastActive = if (fixedDepositAccount.status?.submittedAndPendingApproval == true) {
                                    stringResource(Res.string.client_savings_pending_approval)
                                } else if (fixedDepositAccount.lastActiveTransactionDate != null) {
                                    DateHelper.getDateAsString(fixedDepositAccount.lastActiveTransactionDate!!)
                                } else {
                                    notAvailableText
                                },
                                balance = if (fixedDepositAccount.accountBalance != null) {
                                    "${fixedDepositAccount.currency?.displaySymbol ?: ""} ${fixedDepositAccount.accountBalance}"
                                } else {
                                    notAvailableText
                                },
                                menuList = if (fixedDepositAccount.status?.submittedAndPendingApproval == true) {
                                    listOf(
                                        Actions.ViewAccount(MifosIcons.ShielOutlined),
                                        Actions.ApproveAccount(),
                                    )
                                } else {
                                    listOf(
                                        Actions.ViewAccount(MifosIcons.ShielOutlined),
                                    )
                                },
                            ) { actions ->
                                when (actions) {
                                    is Actions.ViewAccount -> {
                                        onAction(
                                            FixedDepositAccountAction.ViewAccount(
                                                fixedDepositAccount.accountNo ?: "",
                                            ),
                                        )
                                    }

                                    is Actions.ApproveAccount -> {
                                        FixedDepositAccountAction.ApproveAccount(
                                            fixedDepositAccount.accountNo ?: "",
                                        )
                                    }

                                    else -> null
                                }
                            }

                            Spacer(modifier = Modifier.height(DesignToken.spacing.small))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FixedDepositAccountHeader(
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
                text = stringResource(Res.string.client_profile_fixed_deposit_account_title),
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
