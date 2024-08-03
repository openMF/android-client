package com.mifos.feature.savings.account_transaction

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.gson.Gson
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.Network
import com.mifos.core.datastore.PrefManager
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.Black
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.BluePrimaryDark
import com.mifos.core.designsystem.theme.DarkGray
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionResponse
import com.mifos.core.objects.templates.savings.SavingsAccountTransactionTemplate
import com.mifos.feature.savings.R
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Created by Pronay Sarker on 18/07/2024 (8:28 PM)
 */

@Composable
fun SavingsAccountTransactionScreen(
    navigateBack: () -> Unit
) {
    val viewmodel: SavingsAccountTransactionViewModel = hiltViewModel()
    val uiState by viewmodel.savingsAccountTransactionUiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewmodel.checkInDatabaseSavingAccountTransaction()
    }

    SavingsAccountTransactionScreen(
        clientName = viewmodel.clientName,
        uiState = uiState,
        navigateBack = navigateBack,
        onRetry = {
            viewmodel.checkInDatabaseSavingAccountTransaction()
        },
        transactionType = viewmodel.transactionType ?: "",
        loadSavingAccountTemplate = {
            viewmodel.loadSavingAccountTemplate()
        },
        savingsAccountNumber = viewmodel.savingsAccountNumber,
        onProcessTransaction = {
            viewmodel.processTransaction(it)
        },
        setUserOffline = { viewmodel.setUserOffline() }
    )
}

@Composable
fun SavingsAccountTransactionScreen(
    clientName: String?,
    savingsAccountNumber: Int?,
    uiState: SavingsAccountTransactionUiState,
    navigateBack: () -> Unit,
    onRetry: () -> Unit,
    transactionType: String,
    loadSavingAccountTemplate: () -> Unit,
    onProcessTransaction: (savingsAccountTransactionRequest: SavingsAccountTransactionRequest) -> Unit,
    setUserOffline : () -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val topbarTitle = if (transactionType == Constants.SAVINGS_ACCOUNT_TRANSACTION_DEPOSIT) {
        stringResource(id = R.string.feature_savings_savingsAccount) + " " + stringResource(id = R.string.feature_savings_deposit)
    } else {
        stringResource(id = R.string.feature_savings_savingsAccount) + " " + stringResource(id = R.string.feature_savings_withdrawal)
    }

    MifosScaffold(
        snackbarHostState = snackBarHostState,
        title = topbarTitle,
        icon = MifosIcons.arrowBack,
        onBackPressed = navigateBack
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
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
                        setUserOffline = setUserOffline
                    )
                }

                SavingsAccountTransactionUiState.ShowSavingAccountTransactionDoesNotExistInDatabase -> {
                    loadSavingAccountTemplate()
                }

                SavingsAccountTransactionUiState.ShowSavingAccountTransactionExistInDatabase -> {
                    AlertDialog(onDismissRequest = { },
                        title = {
                            Text(
                                style = MaterialTheme.typography.titleLarge,
                                text = stringResource(id = R.string.feature_savings_sync_previous_transaction)
                            )
                        },
                        text = { Text(text = stringResource(id = R.string.feature_savings_dialog_message_sync_savingaccounttransaction)) },
                        confirmButton = {
                            TextButton(onClick = { navigateBack() }) {
                                Text(text = stringResource(id = R.string.feature_savings_dialog_action_ok))
                            }
                        }
                    )
                }

                is SavingsAccountTransactionUiState.ShowTransactionSuccessfullyDone -> {
                    if (uiState.savingsAccountTransactionResponse.resourceId == null) {
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.feature_savings_transaction_saved_in_db),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (transactionType == Constants.SAVINGS_ACCOUNT_TRANSACTION_DEPOSIT) {
                            Toast.makeText(
                                context,
                                context.resources.getString(R.string.feature_savings_deposit_successful_transaction_ID) + uiState.savingsAccountTransactionResponse.resourceId,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (transactionType == Constants.SAVINGS_ACCOUNT_TRANSACTION_WITHDRAWAL) {
                            Toast.makeText(
                                context,
                                context.resources.getString(R.string.feature_savings_withdrawal_successful_transaction_ID) + uiState.savingsAccountTransactionResponse.resourceId,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsAccountTransactionContent(
    clientName: String?,
    savingsAccountNumber: Int?,
    navigateBack: () -> Unit,
    savingsAccountTransactionTemplate: SavingsAccountTransactionTemplate,
    onProcessTransaction: (savingsAccountTransactionRequest: SavingsAccountTransactionRequest) -> Unit,
    setUserOffline: () -> Unit
) {
    var amount by rememberSaveable {
        mutableStateOf("")
    }
    var paymentType by rememberSaveable {
        mutableStateOf("")
    }
    var paymentTypeId by rememberSaveable {
        mutableStateOf(0)
    }

    var transactionDate by rememberSaveable {
        mutableLongStateOf(System.currentTimeMillis())
    }
    var openDatepicker by rememberSaveable {
        mutableStateOf(false)
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = transactionDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= System.currentTimeMillis()
            }
        }
    )
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showReviewTransactionDialog by rememberSaveable {
        mutableStateOf(false)
    }

    if (showReviewTransactionDialog) {
        AlertDialog(onDismissRequest = { showReviewTransactionDialog = false },
            title = {
                Text(text = stringResource(id = R.string.feature_savings_review_transaction_details), style = MaterialTheme.typography.titleLarge)},
            text = {
                Column {
                    Text(
                        text = stringResource(id = R.string.feature_savings_transaction_date) + " : " + SimpleDateFormat(
                            "dd MMMM yyyy",
                            Locale.getDefault()
                        ).format(transactionDate)
                    )
                    Text(text = stringResource(id = R.string.feature_savings_payment_type) + " : " + paymentType)
                    Text(text = stringResource(id = R.string.feature_savings_amount) + " : " + amount)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showReviewTransactionDialog = false
                        val savingsAccountTransactionRequest = SavingsAccountTransactionRequest()

                        savingsAccountTransactionRequest.locale = "en"
                        savingsAccountTransactionRequest.dateFormat = "dd MM yyyy"
                        savingsAccountTransactionRequest.transactionDate = SimpleDateFormat(
                            "dd MMMM yyyy",
                            Locale.getDefault()
                        ).format(transactionDate)
                        savingsAccountTransactionRequest.transactionAmount = amount
                        savingsAccountTransactionRequest.paymentTypeId = paymentTypeId.toString()

                        val builtTransactionRequestAsJson =
                            Gson().toJson(savingsAccountTransactionRequest)
                        Log.i(
                            context.resources.getString(R.string.feature_savings_transaction_body),
                            builtTransactionRequestAsJson
                        )

                        onProcessTransaction.invoke(savingsAccountTransactionRequest)
                    }) {
                    Text(text = stringResource(id = R.string.feature_savings_review_transaction))
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewTransactionDialog = false }) {
                    Text(text = stringResource(id = R.string.feature_savings_cancel))
                }
            }
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
                    }
                ) { Text(stringResource(id = R.string.feature_savings_select_date)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDatepicker = false
                    }
                ) { Text(stringResource(id = R.string.feature_savings_cancel)) }
            }
        )
        {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            text = clientName ?: ""

            // TODO from old fragment
            // 1. Implement QuickContactBadge here
        )

        HorizontalDivider(modifier = Modifier.padding(top = 6.dp))

        FarApartTextItem(
            title = stringResource(id = R.string.feature_savings_account_number),
            value = savingsAccountNumber?.toString() ?: ""
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TODO from old fragment: Add Validation to make sure :
        // 1. Date Is in Correct Format
        // 2. Date Entered is not greater than Date Today i.e Date is not in future

        MifosDatePickerTextField(
            value = SimpleDateFormat(
                "dd-MMMM-yyyy",
                Locale.getDefault()
            ).format(transactionDate), label = R.string.feature_savings_date,
            modifier = Modifier.fillMaxWidth()
        ) {
            openDatepicker = true
        }

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = amount,
            onValueChange = { amount = it },
            label = stringResource(id = R.string.feature_savings_amount),
            error = null,
            keyboardType = KeyboardType.Number
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
            label = R.string.feature_savings_payment_type,
            options = savingsAccountTransactionTemplate.paymentTypeOptions.map { it.name },
            readOnly = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                modifier = Modifier.heightIn(46.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary
                ),
                onClick = { navigateBack.invoke() }) {
                Text(text = stringResource(id = R.string.feature_savings_cancel))
            }

            Button(
                modifier = Modifier.heightIn(46.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary
                ),
                onClick = {
                    if (isAmountValid(context, amount)) {
                        if (Network.isOnline(context = context)) {
                            showReviewTransactionDialog = true
                        } else {
                            setUserOffline.invoke()

                            Toast.makeText(
                                context,
                                context.resources.getString(R.string.feature_savings_error_not_connected_internet),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            ) {
                Text(text = stringResource(id = R.string.feature_savings_review_transaction))
            }
        }

    }
}

fun isAmountValid(
    context: Context,
    amount: String,
): Boolean {
    if (amount.isEmpty()) {
        Toast.makeText(
            context,
            context.resources.getString(R.string.feature_savings_amount) + " " + context.resources.getString(R.string.feature_savings_message_field_required),
            Toast.LENGTH_SHORT
        ).show()

        return false
    }

    try {
        if (amount.toFloat() == 0f) {
            Toast.makeText(
                context,
                context.resources.getString(R.string.feature_savings_error_amount_can_not_be_empty),
                Toast.LENGTH_SHORT
            ).show()

            return false
        }
    } catch (e: NumberFormatException) {
        Toast.makeText(
            context,
            context.resources.getString(R.string.feature_savings_error_invalid_amount),
            Toast.LENGTH_SHORT
        ).show()

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
        horizontalArrangement = Arrangement.SpaceBetween
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
                SavingsAccountTransactionTemplate(
                    paymentTypeOptions = listOf()
                )
            ),
            SavingsAccountTransactionUiState.ShowProgressbar,
            SavingsAccountTransactionUiState.ShowSavingAccountTransactionDoesNotExistInDatabase,
            SavingsAccountTransactionUiState.ShowSavingAccountTransactionExistInDatabase,
            SavingsAccountTransactionUiState.ShowError("Failed to fetch"),
            SavingsAccountTransactionUiState.ShowTransactionSuccessfullyDone(
                SavingsAccountTransactionResponse()
            )
        )
}

@Composable
@Preview(showSystemUi = true)
fun PreviewSavingsAccountTransactionScreen(
    @PreviewParameter(SavingsAccountTransactionScreenPreviewProvider::class) savingsAccountTransactionUiState: SavingsAccountTransactionUiState
) {
    SavingsAccountTransactionScreen(
        clientName = "Jean Charles",
        savingsAccountNumber = 0,
        uiState = savingsAccountTransactionUiState,
        navigateBack = { },
        onRetry = { },
        transactionType = "type",
        loadSavingAccountTemplate = { },
        setUserOffline = {},
        onProcessTransaction = { }
    )
}

