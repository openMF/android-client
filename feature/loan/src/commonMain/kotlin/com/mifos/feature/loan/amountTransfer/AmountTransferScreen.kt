/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.amountTransfer

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_amount
import androidclient.feature.loan.generated.resources.feature_loan_applicant_name
import androidclient.feature.loan.generated.resources.feature_loan_currency
import androidclient.feature.loan.generated.resources.feature_loan_description
import androidclient.feature.loan.generated.resources.feature_loan_dialog_action_ok
import androidclient.feature.loan.generated.resources.feature_loan_error_title
import androidclient.feature.loan.generated.resources.feature_loan_from_account
import androidclient.feature.loan.generated.resources.feature_loan_loan_account_type
import androidclient.feature.loan.generated.resources.feature_loan_office
import androidclient.feature.loan.generated.resources.feature_loan_select_account
import androidclient.feature.loan.generated.resources.feature_loan_select_client
import androidclient.feature.loan.generated.resources.feature_loan_select_office
import androidclient.feature.loan.generated.resources.feature_loan_success_title
import androidclient.feature.loan.generated.resources.feature_loan_transfer
import androidclient.feature.loan.generated.resources.feature_loan_transfer_details
import androidclient.feature.loan.generated.resources.feature_loan_transfer_go_back
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextButton
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosProgressIndicatorOverlay
import com.mifos.core.ui.components.MifosStatusDialog
import com.mifos.core.ui.components.ResultStatus
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun AmountTransferScreenRoute(
    navController: NavController,
    viewModel: AmountTransferViewModel = koinViewModel(),
    navigateBack: () -> Unit,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            AmountTransferEvent.NavigateBack -> navigateBack()
        }
    }

    AmountTransferContent(
        navController = navController,
        state = state,
        onAction = viewModel::trySendAction,
    )

    AmountTransferDialogContent(
        dialogState = state.dialogState,
        onAction = viewModel::trySendAction,
    )
}

@Composable
internal fun AmountTransferContent(
    navController: NavController,
    state: AmountTransferUiState,
    onAction: (AmountTransferAction) -> Unit,
) {
    Column {
        MifosBreadcrumbNavBar(navController)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = DesignToken.padding.largeIncreased)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(DesignToken.padding.extraSmall),
        ) {
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                shape = RoundedCornerShape(DesignToken.padding.medium),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(DesignToken.padding.large),
                ) {
                    LoanDetailRow(
                        label = stringResource(Res.string.feature_loan_applicant_name),
                        value = state.fromClientName ?: "",
                    )
                    LoanDetailRow(
                        label = stringResource(Res.string.feature_loan_office),
                        value = state.fromOfficeName ?: "",
                    )
                    LoanDetailRow(
                        label = stringResource(Res.string.feature_loan_from_account),
                        value = state.fromAccountNumber ?: "",
                    )
                    LoanDetailRow(
                        label = stringResource(Res.string.feature_loan_loan_account_type),
                        value = "Loan account",
                    )
                    LoanDetailRow(
                        label = stringResource(Res.string.feature_loan_currency),
                        value = state.currency,
                    )
                }
            }

            Text(
                modifier = Modifier.padding(vertical = DesignToken.padding.medium),
                text = stringResource(Res.string.feature_loan_transfer_details) + " :",
                style = MaterialTheme.typography.headlineSmall,
            )

            MifosTextFieldDropdown(
                value = state.selectedOfficeName,
                onValueChanged = { },
                label = stringResource(Res.string.feature_loan_select_office),
                options = state.offices.map { it.name!! },
                onOptionSelected = { index, text ->
                    onAction.invoke(
                        AmountTransferAction.OnOfficeChanged(
                            index,
                            text,
                        ),
                    )
                },
                errorMessage = state.selectedOfficeIdError?.let { stringResource(it) },
            )

            MifosTextFieldDropdown(
                value = state.selectedClientName,
                onValueChanged = { },
                label = stringResource(Res.string.feature_loan_select_client),
                options = state.clients.map { it.displayName!! },
                onOptionSelected = { index, text ->
                    onAction.invoke(
                        AmountTransferAction.OnClientChange(
                            index,
                            text,
                        ),
                    )
                },
                errorMessage = state.selectedClientIdError?.let { stringResource(it) },
            )

            MifosTextFieldDropdown(
                value = state.selectedAccountType,
                onValueChanged = { },
                label = stringResource(Res.string.feature_loan_loan_account_type),
                options = state.accountTypes.map { it.value!! },
                onOptionSelected = { index, text ->
                    val typeId = state.accountTypes.getOrNull(index)?.id
                    typeId?.let {
                        onAction.invoke(
                            AmountTransferAction.OnAccountTypeChange(
                                it,
                                text,
                            ),
                        )
                    }
                },
                errorMessage = state.accountTypeIdError?.let { stringResource(it) },
            )

            MifosTextFieldDropdown(
                value = state.selectedAccountName,
                onValueChanged = { },
                label = stringResource(Res.string.feature_loan_select_account),
                options = state.accounts.map { it.accountNo!! },
                onOptionSelected = { index, text ->
                    val accountId = state.accounts.getOrNull(index)?.id
                    accountId?.let {
                        onAction.invoke(
                            AmountTransferAction.OnAccountChange(
                                it,
                                text,
                            ),
                        )
                    }
                },
                errorMessage = state.accountIdError?.let { stringResource(it) },
            )

            MifosOutlinedTextField(
                value = state.amount,
                onValueChange = { onAction.invoke(AmountTransferAction.OnAmountChange(it)) },
                label = stringResource(Res.string.feature_loan_amount),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                ),
                errorText = state.amountError?.let { stringResource(it) },
                isError = state.amountError != null,
            )

            MifosOutlinedTextField(
                value = state.description,
                onValueChange = { onAction.invoke(AmountTransferAction.OnDescriptionChange(it)) },
                label = stringResource(Res.string.feature_loan_description),
                errorText = state.descriptionError?.let { stringResource(it) },
                isError = state.descriptionError != null,
            )

            MifosTextButton(
                modifier = Modifier.fillMaxWidth(),
                text = { Text(stringResource(Res.string.feature_loan_transfer)) },
                onClick = { onAction.invoke(AmountTransferAction.OnTransferClicked) },
            )
        }
    }
}

@Composable
private fun AmountTransferDialogContent(
    dialogState: AmountTransferUiState.DialogState?,
    onAction: (AmountTransferAction) -> Unit,
) {
    when (dialogState) {
        is AmountTransferUiState.DialogState.FetchingFailed -> {
            MifosSweetError(
                message = dialogState.message,
                isRetryEnabled = true,
                onclick = { onAction.invoke(AmountTransferAction.OnRetryFetching) },
            )
        }

        AmountTransferUiState.DialogState.Loading -> MifosProgressIndicatorOverlay()

        is AmountTransferUiState.DialogState.TransferState -> {
            MifosStatusDialog(
                status = dialogState.status,
                btnText = when (dialogState.status) {
                    ResultStatus.SUCCESS -> stringResource(Res.string.feature_loan_transfer_go_back)
                    ResultStatus.FAILURE -> stringResource(Res.string.feature_loan_dialog_action_ok)
                },
                onConfirm = {
                    when (dialogState.status) {
                        ResultStatus.SUCCESS -> onAction(AmountTransferAction.TransferSuccess)
                        ResultStatus.FAILURE -> onAction(AmountTransferAction.CloseDialog)
                    }
                },
                onDismissRequest = { },
                successTitle = stringResource(Res.string.feature_loan_success_title),
                successMessage = dialogState.message,
                failureTitle = stringResource(Res.string.feature_loan_error_title),
                failureMessage = dialogState.message,
                showAsDialog = true,
            )
        }

        null -> {}
    }
}

@Composable
private fun LoanDetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(4.2f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(5.8f),
        )
    }
}
