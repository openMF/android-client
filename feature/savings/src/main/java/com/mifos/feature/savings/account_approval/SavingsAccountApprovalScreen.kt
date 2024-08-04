package com.mifos.feature.savings.account_approval

import android.widget.Toast
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.Network
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.BluePrimaryDark
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.accounts.loan.SavingsApproval
import com.mifos.feature.savings.R
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Created by Pronay Sarker on 12/07/2024 (12:21 AM)
 */

@Composable
fun SavingsAccountApprovalScreen(
    navigateBack: () -> Unit
) {
    val viewModel: SavingsAccountApprovalViewModel = hiltViewModel()
    val uiState by viewModel.savingsAccountApprovalUiState.collectAsStateWithLifecycle()

    SavingsAccountApprovalScreen(
        uiState = uiState,
        navigateBack = navigateBack,
        approveLoan = { viewModel.approveSavingsApplication(it) }
    )
}

@Composable
fun SavingsAccountApprovalScreen(
    uiState: SavingsAccountApprovalUiState,
    navigateBack: () -> Unit,
    approveLoan: (SavingsApproval) -> Unit,
) {
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val context = LocalContext.current

    MifosScaffold(
        snackbarHostState = snackbarHostState,
        title = stringResource(id = R.string.feature_savings_approve_savings),
        onBackPressed = navigateBack,
        icon = MifosIcons.arrowBack
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            when (uiState) {
                SavingsAccountApprovalUiState.Initial -> {
                    SavingsAccountApprovalContent(approveLoan = approveLoan)
                }

                is SavingsAccountApprovalUiState.ShowError -> {
                    MifosSweetError(
                        message = uiState.message,
                        isRetryEnabled = false,
                        onclick = {}
                    )
                }

                SavingsAccountApprovalUiState.ShowProgressbar -> {
                    MifosCircularProgress()
                }

                is SavingsAccountApprovalUiState.ShowSavingAccountApprovedSuccessfully -> {
                    Toast.makeText(
                        context,
                        stringResource(id = R.string.feature_savings_savings_approved),
                        Toast.LENGTH_LONG
                    ).show()
                    navigateBack.invoke()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsAccountApprovalContent(
    approveLoan: (savingsApproval: SavingsApproval) -> Unit
) {
    val scrollState = rememberScrollState()
    var approvalDate by rememberSaveable {
        mutableLongStateOf(System.currentTimeMillis())
    }
    var reasonForApproval by rememberSaveable {
        mutableStateOf("")
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = approvalDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= System.currentTimeMillis()
            }
        }
    )
    val context = LocalContext.current
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
                    }
                ) { Text(stringResource(id = R.string.feature_savings_select_date)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePickerDialog = false
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
            .verticalScroll(scrollState)
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            style = MaterialTheme.typography.bodyLarge,
            text = stringResource(id = R.string.feature_savings_approved_on),
            modifier = Modifier.padding(start = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosDatePickerTextField(
            value = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(
                approvalDate
            ), label = R.string.feature_savings_approval_savings_date
        ) {
            showDatePickerDialog = true
        }

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = reasonForApproval,
            onValueChange = { reasonForApproval = it },
            label = stringResource(id = R.string.feature_savings_savings_approval_reason),
            error = null
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .heightIn(44.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary
            ), onClick = {
                if (Network.isOnline(context)) {
                    approveLoan.invoke(
                        SavingsApproval(
                            approvedOnDate = SimpleDateFormat(
                                "yyyy-MM-dd",
                                Locale.getDefault()
                            ).format(approvalDate),
                            note = reasonForApproval
                        )
                    )
                } else {
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.feature_savings_error_not_connected_internet),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }) {
            Text(text = stringResource(id = R.string.feature_savings_save))
        }
    }
}

class SavingsAccountApprovalScreenPreviewProvider :
    PreviewParameterProvider<SavingsAccountApprovalUiState> {

    override val values: Sequence<SavingsAccountApprovalUiState>
        get() = sequenceOf(
            SavingsAccountApprovalUiState.Initial,
            SavingsAccountApprovalUiState.ShowProgressbar,
            SavingsAccountApprovalUiState.ShowSavingAccountApprovedSuccessfully(GenericResponse()),
            SavingsAccountApprovalUiState.ShowError("Error")
        )
}

@Composable
@Preview(showSystemUi = true)
fun PreviewSavingsAccountApprovalScreen(
    @PreviewParameter(SavingsAccountApprovalScreenPreviewProvider::class) savingsAccountApprovalUiState: SavingsAccountApprovalUiState
) {
    SavingsAccountApprovalScreen(
        uiState = savingsAccountApprovalUiState,
        navigateBack = { }) {

    }
}
