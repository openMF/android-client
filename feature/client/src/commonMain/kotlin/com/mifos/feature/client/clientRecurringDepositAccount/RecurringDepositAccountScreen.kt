package com.mifos.feature.client.clientRecurringDepositAccount

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.client_savings_item
import androidclient.feature.client.generated.resources.feature_client_dialog_action_ok
import androidclient.feature.client.generated.resources.filter
import androidclient.feature.client.generated.resources.search
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosActionsSavingsListingComponent
import com.mifos.core.ui.components.MifosEmptyCard
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.util.EventsEffect
import com.mifos.feature.client.clientGeneral.ClientProfileGeneralState
import com.mifos.feature.client.savingsAccounts.SavingsAccountAction
import com.mifos.feature.client.savingsAccounts.SavingsAccountState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun RecurringDepositAccountScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecurringDepositAccountViewModel = koinViewModel()
){
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->

        when(event) {
            is RecurringDepositAccountActions.ApproveAccount -> TODO()
            RecurringDepositAccountEvent.onApproveAccount -> TODO()
            RecurringDepositAccountEvent.onNavigateBack -> navigateBack
            is RecurringDepositAccountEvent.onViewAccount -> TODO()
        }
    }

    RecurringDepositAccountDialog(
        state,
        onAction = remember(viewModel) {
            {action-> viewModel.trySendAction(action) }
        },
    )

    RecurringDepositAccountScaffold(
        state = state,
        modifier = modifier,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecurringDepositAccountDialog(
    state: RecurringDepositAccountState,
    onAction: (RecurringDepositAccountActions) -> Unit,
) {
    when (state.dialogState) {
        is RecurringDepositAccountState.DialogState.Error -> {
            AlertDialog(
                title = { Text("Error") },
                text = { Text(text = state.dialogState.message) },
                confirmButton = {
                    TextButton(
                        onClick = { onAction.invoke(RecurringDepositAccountActions.CloseDialog) },
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
fun RecurringDepositAccountScaffold(
    state: RecurringDepositAccountState,
    modifier: Modifier = Modifier,
    onAction: (RecurringDepositAccountActions) -> Unit
){
    MifosScaffold(
        onBackPressed = {},
        modifier,
    ) { paddingValues ->

        Column(
            Modifier.fillMaxSize()
                .padding(paddingValues)
        ) {

            RecurringDepositAccountHeader(
                "",
                onSearch = {}
            )

            if (state.recurringDepositAccounts.isEmpty()) {
                MifosEmptyCard(msg = "Click Here To View Filled State. ")
            } else {
                LazyColumn {
                    items(state.recurringDepositAccounts){
                        MifosActionsSavingsListingComponent(
                            "1",
                            "",
                            "",
                            "",
                            emptyList()
                        ){

                        }
                    }
                }

            }
        }

    }

}


@Composable
fun RecurringDepositAccountHeader(
    totalItem: String,
    modifier: Modifier = Modifier,
    onSearch: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Column {
            Text(
                text = "Recurring Deposit Accounts",
                style = MifosTypography.titleMedium,
            )

            Text(
                text = totalItem + " " + stringResource(Res.string.client_savings_item),
                style = MifosTypography.labelMedium,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.wrapContentSize()
        ) {
            IconButton(
                onClick = {

                },
            ) {
                Icon(
                    painter = painterResource(Res.drawable.search),
                    contentDescription = null,
                )
            }

            Spacer(modifier = Modifier.padding(DesignToken.padding.largeIncreased))

            IconButton(
                onClick = {  },
            ) {
                Icon(
                    painter = painterResource(Res.drawable.filter),
                    contentDescription = null,
                )
            }
        }

    }
}