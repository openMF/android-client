/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.savingsAccounts

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.client_product_saving_account
import androidclient.feature.client.generated.resources.client_savings_item
import androidclient.feature.client.generated.resources.client_savings_not_avilable
import androidclient.feature.client.generated.resources.client_savings_pending_approval
import androidclient.feature.client.generated.resources.client_savings_savings_accounts
import androidclient.feature.client.generated.resources.feature_client_dialog_action_ok
import androidclient.feature.client.generated.resources.filter
import androidclient.feature.client.generated.resources.search
import androidclient.feature.client.generated.resources.update_default_account_title
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.theme.AppColors
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.Actions
import com.mifos.core.ui.components.MifosActionsSavingsListingComponent
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosSearchBar
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SavingsAccountsScreenRoute(
    navigateBack: () -> Unit,
    navController: NavController,
    viewModel: SavingsAccountsViewModel = koinViewModel(),
    navigateToViewAccount: (Int) -> Unit,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            SavingsAccountEvent.NavigateBack -> navigateBack()
            is SavingsAccountEvent.ViewAccount -> navigateToViewAccount(2)
            SavingsAccountEvent.ApproveAccount -> {}
        }
    }

    SavingsAccountsDialog(
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )

    SavingsAccountsScreen(
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
        navController = navController,
    )
}

@Composable
fun SavingsAccountsScreen(
    onAction: (SavingsAccountAction) -> Unit,
    state: SavingsAccountState,
    navController: NavController,
) {
    MifosScaffold(
        title = stringResource(Res.string.update_default_account_title),
        onBackPressed = { onAction(SavingsAccountAction.NavigateBack) },
        modifier = Modifier,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            MifosBreadcrumbNavBar(navController)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = DesignToken.padding.large),
            ) {
                SavingsAccountsHeader(
                    totalItem = state.savingsAccounts.size.toString(),
                    onAction = onAction,
                )

                // todo implement search bar functionality
                if (state.isSearchBarActive) {
                    MifosSearchBar(
                        query = state.searchText,
                        onQueryChange = { onAction.invoke(SavingsAccountAction.UpdateSearchValue(it)) },
                        onSearchClick = { onAction.invoke(SavingsAccountAction.OnSearchClick) },
                        onBackClick = { onAction.invoke(SavingsAccountAction.ToggleSearch) },
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (state.savingsAccounts.isEmpty()) {
                    EmptySavingsCard()
                } else {
                    LazyColumn {
                        items(state.savingsAccounts) { savings ->
                            MifosActionsSavingsListingComponent(
                                accountNo = savings.accountNo.toString(),
                                savingsProduct = stringResource(Res.string.client_product_saving_account),
                                savingsProductName = savings.productName.toString(),
                                // todo modify with currency symbol when not getting null from api, currently getting null
                                balance = if (savings.accountBalance != null) {
                                    "${savings.currency?.displaySymbol ?: ""} ${savings.accountBalance}"
                                } else {
                                    stringResource(Res.string.client_savings_not_avilable)
                                },
                                menuList = if (savings.status?.submittedAndPendingApproval == true) {
                                    listOf(
                                        Actions.ViewAccount(),
                                        Actions.ApproveAccount(),
                                    )
                                } else {
                                    listOf(
                                        Actions.ViewAccount(),
                                    )
                                },
                                onActionClicked = { actions ->
                                    when (actions) {
                                        is Actions.ViewAccount -> onAction.invoke(
                                            SavingsAccountAction.ViewAccount(
                                                state.clientId,
                                            ),
                                        )

                                        is Actions.ApproveAccount -> onAction.invoke(
                                            SavingsAccountAction.ApproveAccount(
                                                state.clientId,
                                            ),
                                        )

                                        else -> null
                                    }
                                },
                                lastActive = if (savings.lastActiveTransactionDate != null) {
                                    DateHelper.getDateAsString(savings.lastActiveTransactionDate!!)
                                } else if (savings.status?.submittedAndPendingApproval == true) {
                                    stringResource(Res.string.client_savings_pending_approval)
                                } else {
                                    stringResource(Res.string.client_savings_not_avilable)
                                },
                            )

                            Spacer(modifier = Modifier.height(DesignToken.spacing.small))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SavingsAccountsHeader(
    totalItem: String,
    onAction: (SavingsAccountAction) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            Text(
                text = stringResource(Res.string.client_savings_savings_accounts),
                style = MifosTypography.titleMedium,
            )

            Text(
                text = totalItem + " " + stringResource(Res.string.client_savings_item),
                style = MifosTypography.labelMedium,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = { onAction.invoke(SavingsAccountAction.ToggleSearch) },
        ) {
            // add a cross icon when its active, talk with design team
            Icon(
                painter = painterResource(Res.drawable.search),
                contentDescription = null,
            )
        }

        DesignToken.padding

        IconButton(
            onClick = { onAction.invoke(SavingsAccountAction.ToggleFilter) },
        ) {
            Icon(
                painter = painterResource(Res.drawable.filter),
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun EmptySavingsCard() {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(
            width = 1.dp,
            color = AppColors.cardBorders,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "No Item Found",
                style = MifosTypography.titleSmallEmphasized,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Click Here To View Filled State. ",
                style = MifosTypography.bodySmall,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SavingsAccountsDialog(
    state: SavingsAccountState,
    onAction: (SavingsAccountAction) -> Unit,
) {
    when (state.dialogState) {
        is SavingsAccountState.DialogState.Error -> {
            AlertDialog(
                title = { Text("Error") },
                text = { Text(text = state.dialogState.message) },
                confirmButton = {
                    TextButton(
                        onClick = { onAction.invoke(SavingsAccountAction.CloseDialog) },
                    ) {
                        Text(stringResource(Res.string.feature_client_dialog_action_ok))
                    }
                },
                onDismissRequest = {},
            )
        }

        SavingsAccountState.DialogState.Loading -> MifosProgressIndicator()

        else -> null
    }
}
