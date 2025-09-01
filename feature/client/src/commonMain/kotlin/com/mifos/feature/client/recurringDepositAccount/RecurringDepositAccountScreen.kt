package com.mifos.feature.client.recurringDepositAccount

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.client_profile_recurring_deposit_account_title
import androidclient.feature.client.generated.resources.client_savings_item
import androidclient.feature.client.generated.resources.client_savings_not_avilable
import androidclient.feature.client.generated.resources.client_savings_pending_approval
import androidclient.feature.client.generated.resources.feature_client_dialog_action_ok
import androidclient.feature.client.generated.resources.filter
import androidclient.feature.client.generated.resources.search
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.Actions
import com.mifos.core.ui.components.MifosActionsSavingsListingComponent
import com.mifos.core.ui.components.MifosEmptyCard
import com.mifos.core.ui.components.MifosSearchBar
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun RecurringDepositAccountScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    onApproveAccount: (Int) -> Unit = {},
    onViewAccount: (Int) -> Unit = {},
    viewModel: RecurringDepositAccountViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is RecurringDepositAccountEvent.onApproveAccount -> {
                onApproveAccount(event.accountId)
            }

            RecurringDepositAccountEvent.onNavigateBack -> navigateBack
            is RecurringDepositAccountEvent.onViewAccount -> {
                onViewAccount(event.accountId)
            }
        }
    }

    val action: (RecurringDepositAccountAction) -> Unit = remember(viewModel) {
        {
            when (it) {
                RecurringDepositAccountAction.CloseDialog -> {
                    viewModel.trySendAction(RecurringDepositAccountAction.CloseDialog)
                }

                RecurringDepositAccountAction.NavigateBack -> {
                    viewModel.trySendAction(RecurringDepositAccountAction.NavigateBack)
                }

                RecurringDepositAccountAction.Refresh -> {
                    viewModel.trySendAction(RecurringDepositAccountAction.Refresh)
                }

                is RecurringDepositAccountAction.Search -> {
                    viewModel.trySendAction(
                        RecurringDepositAccountAction.Search(it.query),
                    )
                }

                RecurringDepositAccountAction.ToggleFilter -> {
                    viewModel.trySendAction(RecurringDepositAccountAction.ToggleFilter)
                }

                RecurringDepositAccountAction.ToggleSearch -> {
                    viewModel.trySendAction(RecurringDepositAccountAction.ToggleSearch)
                }

                is RecurringDepositAccountAction.UpdateSearch -> {
                    viewModel.trySendAction(
                        RecurringDepositAccountAction.UpdateSearch(it.query),
                    )
                }

                is RecurringDepositAccountAction.ViewAccount -> {
                    viewModel.trySendAction(
                        RecurringDepositAccountAction.ViewAccount(it.accountId),
                    )
                }

                is RecurringDepositAccountAction.ApproveAccount -> {
                    viewModel.trySendAction(
                        RecurringDepositAccountAction.ApproveAccount(it.accountId),
                    )
                }
            }
        }
    }


    RecurringDepositAccountDialog(
        state,
        onCloseDialog = {
            action(RecurringDepositAccountAction.CloseDialog)
        },
    )

    RecurringDepositAccountScaffold(
        state = state,
        onNavigateBack = navigateBack,
        modifier = modifier,
        onSearchClick = {
            action(RecurringDepositAccountAction.Search(it))
        },
        onToggleFilter = { action(RecurringDepositAccountAction.ToggleFilter) },
        onToggleSearch = { action(RecurringDepositAccountAction.ToggleSearch) },
        onViewAccount = {
            action(RecurringDepositAccountAction.ViewAccount(accountId = state.clientId))
        },
        onApproveAccount = {
            action(RecurringDepositAccountAction.ApproveAccount(accountId = state.clientId))
        },
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RecurringDepositAccountDialog(
    state: RecurringDepositAccountState,
    onCloseDialog: () -> Unit,
) {
    when (state.dialogState) {
        is RecurringDepositAccountState.DialogState.Error -> {
            AlertDialog(
                title = { Text("Error") },
                text = { Text(text = state.dialogState.message) },
                confirmButton = {
                    TextButton(
                        onClick = onCloseDialog,
                    ) {
                        Text(stringResource(Res.string.feature_client_dialog_action_ok))
                    }
                },
                onDismissRequest = {},
            )
        }

        RecurringDepositAccountState.DialogState.Loading -> MifosCircularProgress()

        else -> null
    }
}

@Composable
internal fun RecurringDepositAccountScaffold(
    state: RecurringDepositAccountState,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    onSearchClick: (String) -> Unit = {},
    onUpdateSearch: (String) -> Unit = {},
    onToggleFilter: () -> Unit = {},
    onToggleSearch: () -> Unit = {},
    onViewAccount: () -> Unit = {},
    onApproveAccount: () -> Unit = {},
) {
    MifosScaffold(
        onBackPressed = onNavigateBack,
        modifier = modifier,
        title = "",
    ) { paddingValues ->

        Column(
            Modifier.fillMaxSize()
                .padding(paddingValues)
                .padding(
                    vertical = DesignToken.padding.extraLarge,
                    horizontal = DesignToken.padding.large,
                ),
        ) {
            val notAvailableText = stringResource(Res.string.client_savings_not_avilable)
            RecurringDepositAccountHeader(
                state.recurringDepositAccounts.size.toString(),
                onToggleSearch = onToggleSearch,
                onToggleFilter = onToggleFilter,
            )

            // todo implement search bar functionality
            if (state.isSearchBarActive) {
                MifosSearchBar(
                    query = state.searchText,
                    onQueryChange = {
                        onUpdateSearch(it)
                    },
                    onSearchClick = {
                        onSearchClick(it)
                    },
                    onBackClick = onToggleSearch,
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.padding.largeIncreasedExtra))

            if (state.recurringDepositAccounts.isEmpty()) {
                MifosEmptyCard(msg = "Click Here To View Filled State. ")
            } else {
                LazyColumn {
                    items(state.recurringDepositAccounts) { recurringDeposit ->
                        MifosActionsSavingsListingComponent(
                            accountNo = recurringDeposit.accountNo ?: notAvailableText,
                            savingsProduct = "Recurring Deposit Product",
                            savingsProductName = recurringDeposit.shortProductName ?: notAvailableText,
                            lastActive = if (recurringDeposit.status?.submittedAndPendingApproval == true) {
                                stringResource(Res.string.client_savings_pending_approval)
                            } else if (recurringDeposit.lastActiveTransactionDate != null) {
                                DateHelper.getDateAsString(recurringDeposit.lastActiveTransactionDate!!)
                            } else {
                                notAvailableText
                            },
                            balance = recurringDeposit.accountBalance?.toString() ?: notAvailableText,
                            menuList = if (recurringDeposit.status?.submittedAndPendingApproval == true) {
                                listOf(
                                    Actions.ViewAccount,
                                    Actions.ApproveAccount,
                                )
                            } else {
                                listOf(
                                    Actions.ViewAccount,
                                )
                            },
                        ) { actions ->
                            when (actions) {
                                Actions.ViewAccount -> onApproveAccount
                                Actions.ApproveAccount -> onViewAccount
                                else -> null
                            }

                        }
                    }
                }

            }
        }

    }
}


@Composable
internal fun RecurringDepositAccountHeader(
    totalItem: String,
    modifier: Modifier = Modifier,
    onToggleFilter: () -> Unit = {},
    onToggleSearch: () -> Unit = {},
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