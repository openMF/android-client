/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.shareAccounts

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.client_product_shares_account
import androidclient.feature.client.generated.resources.client_savings_item
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.designsystem.utils.onClick
import com.mifos.core.ui.components.Actions
import com.mifos.core.ui.components.MifosActionsShareListingComponent
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosEmptyCard
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ShareAccountsScreenRoute(
    navController: NavController,
    viewAccount: (Int) -> Unit,
    viewModel: ShareAccountsViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is ShareAccountsEvent.ViewAccount -> viewAccount(event.accountId)
        }
    }

    ShareAccountsScreen(
        state = state,
        navController = navController,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )

    ShareAccountsDialog(
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )
}

@Composable
internal fun ShareAccountsScreen(
    navController: NavController,
    state: ShareAccountsUiState,
    onAction: (ShareAccountsAction) -> Unit,
) {
    MifosScaffold(
        title = "Share Accounts",
        onBackPressed = {},
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
                .fillMaxSize(),
        ) {
            MifosBreadcrumbNavBar(
                navController = navController,
            )

            when (state.isLoading) {
                true -> MifosProgressIndicator()

                false -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                            .padding(horizontal = DesignToken.padding.large),
                    ) {
                        ShareAccountHeader(
                            totalItem = state.accounts.size.toString(),
                            onAction = onAction,
                        )

                        Spacer(modifier = Modifier.height(DesignToken.padding.large))

                        if (state.accounts.isNotEmpty()) {
                            val emptyText = stringResource(Res.string.string_not_available)
                            LazyColumn {
                                item {
                                    state.accounts.forEachIndexed { index, account ->
                                        MifosActionsShareListingComponent(
                                            accountNo = account.accountNo ?: emptyText,
                                            shareProductName = account.shortProductName
                                                ?: emptyText,
                                            pendingForApprovalShares = account.totalPendingForApprovalShares,
                                            approvedShares = account.totalApprovedShares,
                                            isExpanded = state.currentlyActiveIndex == index && state.isCardActive,
                                            menuList = (listOf(Actions.ViewAccount())),
                                            onActionClicked = { actions ->
                                                when (actions) {
                                                    is Actions.ViewAccount -> {
                                                        onAction(
                                                            ShareAccountsAction.ViewAccount(
                                                                account.id ?: -1,
                                                            ),
                                                        )
                                                    }

                                                    else -> {}
                                                }
                                            },
                                            onClick = {
                                                onAction(
                                                    ShareAccountsAction.CardClicked(
                                                        index,
                                                    ),
                                                )
                                            },
                                        )

                                        Spacer(Modifier.height(DesignToken.padding.small))
                                    }
                                }
                            }
                        } else {
                            MifosEmptyCard()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShareAccountHeader(
    totalItem: String,
    onAction: (ShareAccountsAction) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            Text(
                text = stringResource(Res.string.client_product_shares_account),
                style = MifosTypography.titleMedium,
            )

            Text(
                text = totalItem + " " + stringResource(Res.string.client_savings_item),
                style = MifosTypography.labelMedium,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // add a cross icon when its active, talk with design team
        Icon(
            modifier = Modifier.onClick { onAction.invoke(ShareAccountsAction.ToggleSearchBar) },
            painter = painterResource(Res.drawable.search),
            contentDescription = null,
        )

        Icon(
            modifier = Modifier.onClick { onAction.invoke(ShareAccountsAction.ToggleFiler) },
            painter = painterResource(Res.drawable.filter),
            contentDescription = null,
        )
    }
}

@Composable
private fun ShareAccountsDialog(
    state: ShareAccountsUiState,
    onAction: (ShareAccountsAction) -> Unit,
) {
    when (state.dialogState) {
        is ShareAccountsUiState.DialogState.Error -> {
            MifosSweetError(
                message = state.dialogState.message,
                onclick = { onAction.invoke(ShareAccountsAction.Refresh) },
            )
        }

        null -> {}
    }
}
