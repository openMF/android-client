package com.mifos.feature.recurringDeposit.recurringAccountApproval


import androidclient.feature.recurringdeposit.generated.resources.Res
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_account
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_approval_date
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_approval_reason
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_approve_account
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_approved_on
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_cancel
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_save
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_select_date
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.model.objects.template.recurring.approval.RecurringDepositApproval
import com.mifos.core.network.GenericResponse
import com.mifos.core.ui.components.MifosAlertDialog
import com.mifos.core.ui.components.MifosProgressIndicator
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


/**
 * Recurring Deposit Account Approval Screen
 */
@Composable
internal fun RecurringDepositAccountApprovalScreen(
    navigateBack: () -> Unit,
    viewModel: RecurringDepositAccountApprovalViewModel = koinViewModel(),
) {
    val uiState by viewModel.recurringDepositAccountApprovalUiState.collectAsStateWithLifecycle()

    RecurringDepositAccountApprovalScreen(
        uiState = uiState,
        navigateBack = navigateBack,
        approveAccount = { viewModel.approveRecurringDepositApplication(it) },
    )
}

@Composable
internal fun RecurringDepositAccountApprovalScreen(
    uiState: RecurringDepositAccountApprovalUiState,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    approveAccount: (RecurringDepositApproval) -> Unit,
) {
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    MifosScaffold(
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        title = stringResource(Res.string.feature_recurring_deposit_approve_account),
        onBackPressed = navigateBack,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            when (uiState) {
                RecurringDepositAccountApprovalUiState.Initial -> {
                    RecurringDepositAccountApprovalContent(approveAccount = approveAccount)
                }

                is RecurringDepositAccountApprovalUiState.ShowError -> {
                    MifosSweetError(
                        message = uiState.message,
                        isRetryEnabled = false,
                        onclick = {},
                    )
                }

                RecurringDepositAccountApprovalUiState.ShowProgressbar -> {
                    MifosProgressIndicator()
                }

                is RecurringDepositAccountApprovalUiState.ShowRecurringDepositAccountApprovedSuccessfully ->
                    MifosAlertDialog(
                        dialogTitle = "OK",
                        dialogText = stringResource(Res.string.feature_recurring_deposit_account),
                        dismissText = null,
                        onConfirmation = {
                            navigateBack.invoke()
                        },
                        onDismissRequest = {},
                    )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
private fun RecurringDepositAccountApprovalContent(
    approveAccount: (recurringDepositApproval: RecurringDepositApproval) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    var approvalDate by rememberSaveable {
        mutableLongStateOf(Clock.System.now().toEpochMilliseconds())
    }

    var reasonForApproval by rememberSaveable {
        mutableStateOf("")
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = approvalDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= Clock.System.now().toEpochMilliseconds()
            }
        },
    )

    var showDatePickerDialog by rememberSaveable {
        mutableStateOf(false)
    }
    if (showDatePickerDialog) {
        DatePickerDialog(
            onDismissRequest = {
                showDatePickerDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            approvalDate = it
                        }
                        showDatePickerDialog = false
                    },
                ) { Text(stringResource(Res.string.feature_recurring_deposit_select_date)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePickerDialog = false
                    },
                ) { Text(stringResource(Res.string.feature_recurring_deposit_cancel)) }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .fillMaxSize(),
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            style = MaterialTheme.typography.bodyLarge,
            text = stringResource(Res.string.feature_recurring_deposit_approved_on),
            modifier = Modifier.padding(start = 16.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosDatePickerTextField(
            value = DateHelper.getDateAsStringFromLong(approvalDate),
            label = stringResource(Res.string.feature_recurring_deposit_approval_date),
        ) {
            showDatePickerDialog = true
        }

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = reasonForApproval,
            onValueChange = { reasonForApproval = it },
            label = stringResource(Res.string.feature_recurring_deposit_approval_reason),
            error = null,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .heightIn(44.dp),
            onClick = {
                approveAccount.invoke(
                    RecurringDepositApproval(
                        locale = "en",
                        dateFormat = "dd MMMM yyyy",
                        approvedOnDate = DateHelper.getDateAsStringForApproval(approvalDate), // Format: "21 January 2026"
                        note = reasonForApproval
                    )
                )
            },
        ) {
            Text(text = stringResource(Res.string.feature_recurring_deposit_save))
        }
    }
}

fun rememberDatePickerState(initialSelectedDateMillis: Long, selectableDates: Any) {}

// UI State
sealed class RecurringDepositAccountApprovalUiState {
    data object Initial : RecurringDepositAccountApprovalUiState()
    data object ShowProgressbar : RecurringDepositAccountApprovalUiState()
    data class ShowRecurringDepositAccountApprovedSuccessfully(val response: GenericResponse) : RecurringDepositAccountApprovalUiState()
    data class ShowError(val message: String) : RecurringDepositAccountApprovalUiState()
}

// Data class for approval
data class RecurringDepositApproval(
    val approvedOnDate: String,
    val note: String,
)

class RecurringDepositAccountApprovalScreenPreviewProvider :
    PreviewParameterProvider<RecurringDepositAccountApprovalUiState> {

    override val values: Sequence<RecurringDepositAccountApprovalUiState>
        get() = sequenceOf(
            RecurringDepositAccountApprovalUiState.Initial,
            RecurringDepositAccountApprovalUiState.ShowProgressbar,
            RecurringDepositAccountApprovalUiState.ShowRecurringDepositAccountApprovedSuccessfully(GenericResponse()),
            RecurringDepositAccountApprovalUiState.ShowError("Error"),
        )
}

@Composable
@Preview
private fun PreviewRecurringDepositAccountApprovalScreen(
    @PreviewParameter(RecurringDepositAccountApprovalScreenPreviewProvider::class)
    recurringDepositAccountApprovalUiState: RecurringDepositAccountApprovalUiState,
) {
    RecurringDepositAccountApprovalScreen(
        uiState = recurringDepositAccountApprovalUiState,
        navigateBack = { },
    ) {
    }
}