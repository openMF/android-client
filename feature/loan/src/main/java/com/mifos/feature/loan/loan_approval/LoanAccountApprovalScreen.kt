package com.mifos.feature.loan.loan_approval

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.BluePrimaryDark
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.accounts.loan.LoanApproval
import com.mifos.core.objects.accounts.loan.LoanWithAssociations
import com.mifos.feature.loan.R
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Created by Pronay Sarker on 09/07/2024 (8:22 AM)
 */

@Composable
fun LoanAccountApprovalScreen(
    loanId: Int,
    loanWithAssociations : LoanWithAssociations,
    navigateBack: () -> Unit,
) {
    val viewModel: LoanAccountApprovalViewModel = hiltViewModel()
    val uiState by viewModel.loanAccountApprovalUiState.collectAsStateWithLifecycle()

    viewModel.loanId = loanId
    viewModel.loanWithAssociations = loanWithAssociations

    LoanAccountApprovalScreen(
        uiState = uiState,
        loanWithAssociations = viewModel.loanWithAssociations,
        navigateBack = navigateBack,
        onLoanApprove = {
            viewModel.approveLoan(it)
        }
    )
}

@Composable
fun LoanAccountApprovalScreen(
    uiState: LoanAccountApprovalUiState,
    loanWithAssociations: LoanWithAssociations?,
    navigateBack: () -> Unit,
    onLoanApprove: (loanApproval: LoanApproval) -> Unit
) {
    val snackBarHostState = remember {
        SnackbarHostState()
    }
    val context = LocalContext.current

    MifosScaffold(
        snackbarHostState = snackBarHostState,
        title = stringResource(id = R.string.feature_loan_approve_loan),
        icon = MifosIcons.arrowBack,
        onBackPressed = navigateBack
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            LoanAccountApprovalContent(
                loanWithAssociations = loanWithAssociations,
                onLoanApprove = onLoanApprove
            )

            when (uiState) {
                is LoanAccountApprovalUiState.Initial -> Unit

                is LoanAccountApprovalUiState.ShowLoanApproveFailed -> {
                    Toast.makeText(context, uiState.message, Toast.LENGTH_LONG).show()
                }

                is LoanAccountApprovalUiState.ShowLoanApproveSuccessfully -> {
                    Toast.makeText(
                        context,
                        stringResource(id = R.string.feature_loan_loan_approved),
                        Toast.LENGTH_LONG
                    ).show()
                    navigateBack.invoke()
                }

                LoanAccountApprovalUiState.ShowProgressbar -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = MaterialTheme.colorScheme.background.copy(
                                    alpha = .7f
                                )
                            )
                    )
                    {
                        MifosCircularProgress()
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanAccountApprovalContent(
    loanWithAssociations: LoanWithAssociations?,
    onLoanApprove: (loanApproval: LoanApproval) -> Unit
) {
    var approvedAmount by rememberSaveable {
        mutableStateOf(loanWithAssociations?.approvedPrincipal.toString())
    }
    var transactionAmount by rememberSaveable {
        mutableStateOf(loanWithAssociations?.approvedPrincipal.toString())
    }
    var note by rememberSaveable {
        mutableStateOf("")
    }
    var approveDate by rememberSaveable {
        mutableLongStateOf(System.currentTimeMillis())
    }
    val currentDisburseDate by rememberSaveable {
        mutableLongStateOf(System.currentTimeMillis())
    }
    var pickApproveDate by rememberSaveable {
        mutableStateOf(false)
    }
    var pickDisbursementDate by rememberSaveable {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val approveDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = approveDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= System.currentTimeMillis()
            }
        }
    )
    val disburseDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = currentDisburseDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= System.currentTimeMillis()
            }
        }
    )
    var disbursementDate by rememberSaveable {
        mutableStateOf(
            loanWithAssociations
                ?.timeline!!.expectedDisbursementDate?.let {
                    DateHelper.getDateAsString(
                        it
                    )
                }
        )
    }

    if (pickApproveDate || pickDisbursementDate) {
        DatePickerDialog(
            onDismissRequest = {
                pickApproveDate = false
                pickDisbursementDate = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (pickApproveDate) approveDatePickerState.selectedDateMillis?.let {
                            approveDate = it
                        }
                        else {
                            disburseDatePickerState.selectedDateMillis?.let {
                                disbursementDate = SimpleDateFormat(
                                    "dd MMMM yyyy",
                                    Locale.getDefault()
                                ).format(
                                    it
                                )
                            }
                        }
                        pickApproveDate = false
                        pickDisbursementDate = false
                    }
                ) { Text(stringResource(id = R.string.feature_loan_select_date)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        pickApproveDate = false
                        pickDisbursementDate = false
                    }
                ) { Text(stringResource(id = R.string.feature_loan_cancel)) }
            }
        )
        {
            DatePicker(state = if (pickApproveDate) approveDatePickerState else disburseDatePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        MifosDatePickerTextField(
            value = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(
                approveDate
            ),
            label = R.string.feature_loan_approved_on,
            openDatePicker = {
                pickApproveDate = true
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosDatePickerTextField(
            value = disbursementDate ?: "null",
            label = R.string.feature_loan_expected_disbursement_on,
            openDatePicker = {
                pickDisbursementDate = true
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = approvedAmount,
            onValueChange = { approvedAmount = it },
            label = stringResource(id = R.string.feature_loan_approved_amount),
            keyboardType = KeyboardType.Number,
            error = null
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = transactionAmount,
            onValueChange = { transactionAmount = it },
            label = stringResource(id = R.string.feature_loan_transaction_amount),
            keyboardType = KeyboardType.Number,
            error = null
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = stringResource(id = R.string.feature_loan_approval_note),
            keyboardType = KeyboardType.Text,
            error = null
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .heightIn(46.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary
            ),
            onClick = {
                if (isFieldValid(amount = approvedAmount, context = context) &&
                    isFieldValid(amount = transactionAmount, context = context)
                ) {
                    if (com.mifos.core.common.utils.Network.isOnline(context)) {
                        val approvedOnDate = SimpleDateFormat(
                            "dd MMMM yyyy",
                            Locale.getDefault()
                        ).format(
                            approveDate
                        )

                        onLoanApprove.invoke(
                            LoanApproval(
                                note = note,
                                approvedOnDate = approvedOnDate,
                                approvedLoanAmount = approvedAmount,
                                expectedDisbursementDate = disbursementDate
                            )
                        )
                    } else {
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.feature_loan_error_not_connected_internet),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }) {
            Text(text = stringResource(id = R.string.feature_loan_submit))
        }
    }
}

fun isFieldValid(amount: String, context: Context): Boolean {
    return when {
        amount.isEmpty() -> {
            Toast.makeText(
                context,
                context.resources.getString(R.string.feature_loan_approval_amount_can_not_be_empty),
                Toast.LENGTH_SHORT
            ).show()

            false
        }

        !isAmountValid(amount) -> {
            Toast.makeText(
                context,
                context.resources.getString(R.string.feature_loan_error_invalid_amount),
                Toast.LENGTH_SHORT
            ).show()

            false
        }

        else -> {
            true
        }
    }
}

fun isAmountValid(amount: String): Boolean {
    return amount.toDoubleOrNull() != null
}

class LoanAccountApprovalScreenPreviewProvider :
    PreviewParameterProvider<LoanAccountApprovalUiState> {
    override val values: Sequence<LoanAccountApprovalUiState>
        get() = sequenceOf(
            LoanAccountApprovalUiState.Initial,
            LoanAccountApprovalUiState.ShowProgressbar,
            LoanAccountApprovalUiState.ShowLoanApproveSuccessfully(GenericResponse()),
            LoanAccountApprovalUiState.ShowLoanApproveFailed("Loan approve failed")
        )
}

@Composable
@Preview(showSystemUi = true)
fun PreviewLoanAccountApprovalScreen(
    @PreviewParameter(LoanAccountApprovalScreenPreviewProvider::class) loanAccountApprovalUiState: LoanAccountApprovalUiState
) {
    LoanAccountApprovalScreen(
        uiState = loanAccountApprovalUiState,
        loanWithAssociations = LoanWithAssociations(),
        navigateBack = { }) {
    }
}

