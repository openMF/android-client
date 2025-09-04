package com.mifos.feature.client.shareAccounts

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.client_savings_item
import androidclient.feature.client.generated.resources.feature_client_error
import androidclient.feature.client.generated.resources.feature_client_loan_account
import androidclient.feature.client.generated.resources.filter
import androidclient.feature.client.generated.resources.search
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.model.objects.account.share.ShareAccounts
import com.mifos.core.model.objects.account.share.ShareAccountsStatus
import com.mifos.core.ui.components.Actions
import com.mifos.core.ui.components.MifosActionsSavingsListingComponent
import com.mifos.core.ui.components.MifosActionsShareListingComponent
import com.mifos.core.ui.components.MifosAlertDialog
import com.mifos.core.ui.components.MifosEmptyCard
import com.mifos.core.ui.components.MifosSearchBar
import com.mifos.core.ui.util.EventsEffect
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ShareAccountsScreenRoute(
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
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )
}

@Composable
fun ShareAccountsScreen(
    state: ShareAccountsUiState,
    onAction: (ShareAccountsAction) -> Unit,
) {
    MifosScaffold(
        topBar = { Text("Share Accounts") },
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
                .fillMaxSize()
                .padding(DesignToken.padding.large),
        ) {
            ShareAccountHeader(
                totalItem = state.accounts.size.toString(),
                onAction = onAction,
            )

            Spacer(modifier = Modifier.height(DesignToken.padding.large))

            if (state.accounts.isNotEmpty()) {
                LazyColumn {
                    item {
                        state.accounts.forEachIndexed { index, account ->
                            MifosActionsShareListingComponent(
                                accountNo = account.accountNo ?: "Not Available",
                                shareProductName = account.shortProductName ?: "Not Available",
                                pendingForApprovalShares = account.totalPendingForApprovalShares,
                                approvedShares = account.totalApprovedShares,
                                isExpanded = state.currentlyActiveIndex == index,
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
                                onClick = { onAction(ShareAccountsAction.CardClicked(index)) },
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
            onClick = { onAction.invoke(ShareAccountsAction.ToggleSearchBar) },
        ) {
            // add a cross icon when its active, talk with design team
            Icon(
                painter = painterResource(Res.drawable.search),
                contentDescription = null,
            )
        }

        IconButton(
            onClick = { onAction.invoke(ShareAccountsAction.ToggleFiler) },
        ) {
            Icon(
                painter = painterResource(Res.drawable.filter),
                contentDescription = null,
            )
        }
    }
}

@Composable
fun ShareAccountsDialog(
    state: ShareAccountsUiState,
    onAction: (ShareAccountsAction) -> Unit,
) {
    when (state.dialogState) {
        is ShareAccountsUiState.DialogState.Error -> {
            MifosAlertDialog(
                dialogText = state.dialogState.message,
                dialogTitle = stringResource(Res.string.feature_client_error),
                onConfirmation = {},
                onDismissRequest = {
                    onAction.invoke(ShareAccountsAction.)
                },
            )
        }

        ShareAccountsUiState.DialogState.Loading -> MifosCircularProgress()
        null -> TODO()
    }
}


