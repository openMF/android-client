/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.savings.savingsAccountTransaction

import androidclient.feature.savings.generated.resources.Res
import androidclient.feature.savings.generated.resources.feature_savings_account_number
import androidclient.feature.savings.generated.resources.feature_savings_amount
import androidclient.feature.savings.generated.resources.feature_savings_amount_message_field_required
import androidclient.feature.savings.generated.resources.feature_savings_cancel
import androidclient.feature.savings.generated.resources.feature_savings_date
import androidclient.feature.savings.generated.resources.feature_savings_deposit
import androidclient.feature.savings.generated.resources.feature_savings_deposit_successful_transaction_ID
import androidclient.feature.savings.generated.resources.feature_savings_dialog_message_sync_savingaccounttransaction
import androidclient.feature.savings.generated.resources.feature_savings_error_amount_can_not_be_empty
import androidclient.feature.savings.generated.resources.feature_savings_error_invalid_amount
import androidclient.feature.savings.generated.resources.feature_savings_payment_type
import androidclient.feature.savings.generated.resources.feature_savings_review_transaction
import androidclient.feature.savings.generated.resources.feature_savings_review_transaction_details
import androidclient.feature.savings.generated.resources.feature_savings_savingsAccount
import androidclient.feature.savings.generated.resources.feature_savings_select_date
import androidclient.feature.savings.generated.resources.feature_savings_sync_previous_transaction
import androidclient.feature.savings.generated.resources.feature_savings_transaction_date
import androidclient.feature.savings.generated.resources.feature_savings_transaction_saved_in_db
import androidclient.feature.savings.generated.resources.feature_savings_withdrawal
import androidclient.feature.savings.generated.resources.feature_savings_withdrawal_successful_transaction_ID
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Logger
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.model.objects.account.saving.SavingsAccountTransactionResponse
import com.mifos.core.ui.components.MifosAlertDialog
import com.mifos.room.entities.accounts.savings.SavingsAccountTransactionRequestEntity
import com.mifos.room.entities.templates.savings.SavingsAccountTransactionTemplateEntity
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel

/**
 * Created by Pronay Sarker on 18/07/2024 (8:28 PM)
 */

@Composable
internal fun SavingsAccountTransactionScreen(
    navigateBack: () -> Unit,
    viewmodel: SavingsAccountTransactionViewModel = koinViewModel(),
) {
    val uiState by viewmodel.savingsAccountTransactionUiState.collectAsStateWithLifecycle()

    val savingsAccountNumber = viewmodel.savingsAccountNumber
    val clientName = viewmodel.clientName
    val transactionType = viewmodel.transactionType

    LaunchedEffect(key1 = Unit) {
        viewmodel.checkInDatabaseSavingAccountTransaction()
    }

    SavingsAccountTransactionScreen(
        clientName = clientName,
        uiState = uiState,
        navigateBack = navigateBack,
        onRetry = {
            viewmodel.checkInDatabaseSavingAccountTransaction()
        },
        transactionType = transactionType,
        loadSavingAccountTemplate = {
            viewmodel.loadSavingAccountTemplate()
        },
        savingsAccountNumber = savingsAccountNumber,
        onProcessTransaction = {
            viewmodel.processTransaction(it)
        },
        setUserOffline = { viewmodel.setUserOffline() },
    )
}

@Composable
internal fun SavingsAccountTransactionScreen(
    clientName: String?,
    savingsAccountNumber: String?,
    uiState: SavingsAccountTransactionUiState,
    navigateBack: () -> Unit,
    onRetry: () -> Unit,
    transactionType: String,
    loadSavingAccountTemplate: () -> Unit,
    onProcessTransaction: (request: SavingsAccountTransactionRequestEntity) -> Unit,
    modifier: Modifier = Modifier,
    setUserOffline: () -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }

    val topbarTitle = if (transactionType == Constants.SAVINGS_ACCOUNT_TRANSACTION_DEPOSIT) {
        stringResource(Res.string.feature_savings_savingsAccount) + " " +
            stringResource(Res.string.feature_savings_deposit)
    } else {
        stringResource(Res.string.feature_savings_savingsAccount) + " " +
            stringResource(Res.string.feature_savings_withdrawal)
    }
    var dialogTitle by rememberSaveable { mutableStateOf("") }
    var dialogText by rememberSaveable { mutableStateOf("") }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var dialogMessageStr: StringResource? = null

    if (showDialog) {
        MifosAlertDialog(
            dialogTitle = if (dialogMessageStr != null) {
                stringResource(dialogMessageStr)
            } else {
                dialogTitle
            },
            dialogText = dialogText,
            onDismissRequest = {
                navigateBack.invoke()
            },
            onConfirmation = {
                navigateBack.invoke()
            },
        )
    }

    MifosScaffold(
        modifier = modifier,
        snackbarHostState = snackBarHostState,
        title = topbarTitle,
        onBackPressed = navigateBack,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            when (uiState) {
                is SavingsAccountTransactionUiState.ShowError -> {
                    MifosSweetError(message = uiState.message) {
                        onRetry()
                    }
                }

                SavingsAccountTransactionUiState.ShowProgressbar -> {
                    MifosCircularProgress()
                }

                is SavingsAccountTransactionUiState.ShowSavingAccountTemplate -> {
                    SavingsAccountTransactionContent(
                        clientName = clientName,
                        navigateBack = navigateBack,
                        savingsAccountTransactionTemplate = uiState.savingsAccountTransactionTemplate,
                        savingsAccountNumber = savingsAccountNumber,
                        onProcessTransaction = onProcessTransaction,
                        setUserOffline = setUserOffline,
                        showSnackbar = { message ->
                            dialogMessageStr = message
                            showDialog = true
                        },
                    )
                }

                SavingsAccountTransactionUiState.ShowSavingAccountTransactionDoesNotExistInDatabase -> {
                    loadSavingAccountTemplate()
                }

                SavingsAccountTransactionUiState.ShowSavingAccountTransactionExistInDatabase -> {
                    showDialog = true
                    dialogMessageStr = Res.string.feature_savings_sync_previous_transaction
                    dialogText = stringResource(Res.string.feature_savings_dialog_message_sync_savingaccounttransaction)
                }

                is SavingsAccountTransactionUiState.ShowTransactionSuccessfullyDone -> {
                    showDialog = true
                    if (uiState.savingsAccountTransactionResponse.resourceId == null) {
                        dialogText =
                            stringResource(Res.string.feature_savings_transaction_saved_in_db)
                    } else {
                        if (transactionType == Constants.SAVINGS_ACCOUNT_TRANSACTION_DEPOSIT) {
                            dialogText =
                                stringResource(Res.string.feature_savings_deposit_successful_transaction_ID) +
                                uiState.savingsAccountTransactionResponse.resourceId.toString()
                            // todo find a way to show transactionID as well
                        } else if (transactionType == Constants.SAVINGS_ACCOUNT_TRANSACTION_WITHDRAWAL) {
                            dialogText =
                                stringResource(Res.string.feature_savings_withdrawal_successful_transaction_ID) +
                                uiState.savingsAccountTransactionResponse.resourceId
                            // todo find a way to show transactionID as well
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SavingsAccountTransactionContent(
    clientName: String?,
    savingsAccountNumber: String?,
    navigateBack: () -> Unit,
    savingsAccountTransactionTemplate: SavingsAccountTransactionTemplateEntity,
    onProcessTransaction: (savingsAccountTransactionRequest: SavingsAccountTransactionRequestEntity) -> Unit,
    modifier: Modifier = Modifier,
    setUserOffline: () -> Unit,
    showSnackbar: (StringResource) -> Unit,
) {
    var amount by rememberSaveable { mutableStateOf("") }
    var paymentType by rememberSaveable { mutableStateOf("") }
    var paymentTypeId by rememberSaveable { mutableStateOf(0) }
    var transactionDate by rememberSaveable {
        mutableLongStateOf(Clock.System.now().toEpochMilliseconds())
    }
    var openDatepicker by rememberSaveable { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = transactionDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= Clock.System.now().toEpochMilliseconds()
            }
        },
    )
    val scrollState = rememberScrollState()
    var showReviewTransactionDialog by rememberSaveable { mutableStateOf(false) }

    if (showReviewTransactionDialog) {
        AlertDialog(
            onDismissRequest = { showReviewTransactionDialog = false },
            title = {
                Text(
                    text = stringResource(Res.string.feature_savings_review_transaction_details),
                    style = MaterialTheme.typography.titleLarge,
                )
            },
            text = {
                Column {
                    Text(
                        text = stringResource(Res.string.feature_savings_transaction_date) + " : " +
                            DateHelper.getDateAsStringFromLong(transactionDate),
                    )
                    Text(text = stringResource(Res.string.feature_savings_payment_type) + " : " + paymentType)
                    Text(text = stringResource(Res.string.feature_savings_amount) + " : " + amount)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showReviewTransactionDialog = false
                        val savingsAccountTransactionRequest =
                            SavingsAccountTransactionRequestEntity(
                                locale = "en",
                                dateFormat = DateHelper.SHORT_MONTH,
                                transactionDate = DateHelper.getDateAsStringFromLong(transactionDate),
                                transactionAmount = amount,
                                paymentTypeId = paymentTypeId.toString(),
                            )
                        val builtTransactionRequestAsJson =
                            Json.encodeToString(savingsAccountTransactionRequest)
                        Logger.d(
                            "builtTransactionRequestAsJson",
                            Throwable(builtTransactionRequestAsJson),
                        )
                        onProcessTransaction.invoke(savingsAccountTransactionRequest)
                    },
                ) {
                    Text(text = stringResource(Res.string.feature_savings_review_transaction))
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewTransactionDialog = false }) {
                    Text(text = stringResource(Res.string.feature_savings_cancel))
                }
            },
        )
    }

    if (openDatepicker) {
        DatePickerDialog(
            onDismissRequest = {
                openDatepicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            transactionDate = it
                        }
                        openDatepicker = false
                    },
                ) { Text(stringResource(Res.string.feature_savings_select_date)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDatepicker = false
                    },
                ) { Text(stringResource(Res.string.feature_savings_cancel)) }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .verticalScroll(scrollState),
    ) {
        Text(
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            text = clientName ?: "",

            // TODO from old fragment
            // 1. Implement QuickContactBadge here
        )

        HorizontalDivider(modifier = Modifier.padding(top = 6.dp))

        FarApartTextItem(
            title = stringResource(Res.string.feature_savings_account_number),
            value = savingsAccountNumber?.toString() ?: "",
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TODO from old fragment: Add Validation to make sure :
        // 1. Date Is in Correct Format
        // 2. Date Entered is not greater than Date Today i.e Date is not in future

        MifosDatePickerTextField(
            value = DateHelper.getDateAsStringFromLong(transactionDate),
            label = stringResource(Res.string.feature_savings_date),
            modifier = Modifier.fillMaxWidth(),
        ) {
            openDatepicker = true
        }

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = amount,
            onValueChange = { amount = it },
            label = stringResource(Res.string.feature_savings_amount),
            error = null,
            keyboardType = KeyboardType.Number,
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosTextFieldDropdown(
            modifier = Modifier.fillMaxWidth(),
            value = paymentType,
            onValueChanged = { paymentType = it },
            onOptionSelected = { index, value ->
                paymentType = value
                paymentTypeId = savingsAccountTransactionTemplate.paymentTypeOptions[index].id
            },
            label = stringResource(Res.string.feature_savings_payment_type),
            options = savingsAccountTransactionTemplate.paymentTypeOptions.map { it.name },
            readOnly = true,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Button(
                modifier = Modifier.heightIn(46.dp),
                onClick = { navigateBack.invoke() },
            ) {
                Text(text = stringResource(Res.string.feature_savings_cancel))
            }

            Button(
                modifier = Modifier.heightIn(46.dp),
                onClick = {
                    if (isAmountValid(
                            amount,
                            showSnackbar = showSnackbar,
                        )
                    ) {
                        showReviewTransactionDialog = true
                        // todo if user is offline set user offline
//                        setUserOffline.invoke()
                    }
                },
            ) {
                Text(text = stringResource(Res.string.feature_savings_review_transaction))
            }
        }
    }
}

private fun isAmountValid(
    amount: String,
    showSnackbar: (StringResource) -> Unit,
): Boolean {
    if (amount.isEmpty()) {
        showSnackbar.invoke(Res.string.feature_savings_amount_message_field_required)
        return false
    }

    try {
        if (amount.toFloat() == 0f) {
            showSnackbar.invoke(Res.string.feature_savings_error_amount_can_not_be_empty)
            return false
        }
    } catch (e: NumberFormatException) {
        showSnackbar.invoke(Res.string.feature_savings_error_invalid_amount)
        return false
    }

    return true
}

@Composable
private fun FarApartTextItem(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            style = MaterialTheme.typography.bodyLarge,
            text = title,
            color = Black,
        )

        Text(
            style = MaterialTheme.typography.bodyLarge,
            text = value,
            color = DarkGray,
        )
    }
}

class SavingsAccountTransactionScreenPreviewProvider :
    PreviewParameterProvider<SavingsAccountTransactionUiState> {

    override val values: Sequence<SavingsAccountTransactionUiState>
        get() = sequenceOf(
            SavingsAccountTransactionUiState.ShowSavingAccountTemplate(
                SavingsAccountTransactionTemplateEntity(
                    paymentTypeOptions = listOf(),
                ),
            ),
            SavingsAccountTransactionUiState.ShowProgressbar,
            SavingsAccountTransactionUiState.ShowSavingAccountTransactionDoesNotExistInDatabase,
            SavingsAccountTransactionUiState.ShowSavingAccountTransactionExistInDatabase,
            SavingsAccountTransactionUiState.ShowError("Failed to fetch"),
            SavingsAccountTransactionUiState.ShowTransactionSuccessfullyDone(
                SavingsAccountTransactionResponse(),
            ),
        )
}

@Composable
@Preview
private fun PreviewSavingsAccountTransactionScreen(
    @PreviewParameter(SavingsAccountTransactionScreenPreviewProvider::class)
    savingsAccountTransactionUiState: SavingsAccountTransactionUiState,
) {
    SavingsAccountTransactionScreen(
        clientName = "Jean Charles",
        savingsAccountNumber = "Jakarta000000172",
        uiState = savingsAccountTransactionUiState,
        navigateBack = { },
        onRetry = { },
        transactionType = "type",
        loadSavingAccountTemplate = { },
        setUserOffline = {},
        onProcessTransaction = { },
    )
}
