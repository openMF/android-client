package com.mifos.feature.savings.account_activate

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
import com.mifos.feature.savings.R
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Created by Pronay Sarker on 12/07/2024 (11:10 AM)
 */

@Composable
fun SavingsAccountActivateScreen(
    navigateBack: () -> Unit
) {
    val viewModel: SavingsAccountActivateViewModel = hiltViewModel()
    val uiState by viewModel.savingsAccountActivateUiState.collectAsStateWithLifecycle()

    SavingsAccountActivateScreen(
        uiState = uiState,
        navigateBack = navigateBack,
        activateSavings = { viewModel.activateSavings(it) }
    )
}

@Composable
fun SavingsAccountActivateScreen(
    uiState: SavingsAccountActivateUiState,
    navigateBack: () -> Unit,
    activateSavings: (hashMap: HashMap<String, String>) -> Unit,
) {
    val snackBarHostState = remember {
        SnackbarHostState()
    }
    val context = LocalContext.current

    MifosScaffold(
        snackbarHostState = snackBarHostState,
        title = stringResource(id = R.string.feature_savings_activate_savings),
        onBackPressed = navigateBack,
        icon = MifosIcons.arrowBack
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            when (uiState) {
                SavingsAccountActivateUiState.Initial -> {
                    SavingsAccountActivateContent(
                        activateSavings = activateSavings
                    )
                }

                is SavingsAccountActivateUiState.ShowError -> {
                    MifosSweetError(
                        message = uiState.message,
                        isRetryEnabled = false,
                        onclick = {}
                    )
                }

                SavingsAccountActivateUiState.ShowProgressbar -> {
                    MifosCircularProgress()
                }

                is SavingsAccountActivateUiState.ShowSavingAccountActivatedSuccessfully -> {
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.feature_savings_savings_account_activated),
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
fun SavingsAccountActivateContent(
    activateSavings: (hashMap: HashMap<String, String>) -> Unit,
) {
    val scrollstate = rememberScrollState()
    val context = LocalContext.current
    var approvalDate by rememberSaveable {
        mutableLongStateOf(System.currentTimeMillis())
    }
    var approvalReason by rememberSaveable {
        mutableStateOf("")
    }
    var showDatePickerDialog by rememberSaveable {
        mutableStateOf(false)
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = approvalDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= System.currentTimeMillis()
            }
        }
    )

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
            .fillMaxSize()
            .verticalScroll(scrollstate)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            style = MaterialTheme.typography.bodyLarge,
            text = stringResource(id = R.string.feature_savings_approved_on),
            modifier = Modifier.padding(start = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosDatePickerTextField(
            value = SimpleDateFormat(
                "dd-MMMM-yyyy",
                Locale.getDefault()
            ).format(approvalDate), label = R.string.feature_savings_approval_savings_date
        ) {
            showDatePickerDialog = true
        }

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = approvalReason,
            onValueChange = { approvalReason = it },
            label = stringResource(id = R.string.feature_savings_savings_approval_reason),
            error = null
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .heightIn(46.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary
            ),
            onClick = {
                if (Network.isOnline(context)) {
                    val hashMap = HashMap<String, String>()
                    hashMap["dateFormat"] = "dd MMMM yyyy"
                    hashMap["activatedOnDate"] = SimpleDateFormat(
                        "dd-MMMM-yyyy",
                        Locale.getDefault()
                    ).format(approvalDate)
                    hashMap["locale"] = "en"

                    activateSavings.invoke(hashMap)
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

class SavingsAccountActivateScreenPreviewProvider :
    PreviewParameterProvider<SavingsAccountActivateUiState> {
    override val values: Sequence<SavingsAccountActivateUiState>
        get() = sequenceOf(
            SavingsAccountActivateUiState.Initial,
            SavingsAccountActivateUiState.ShowProgressbar,
            SavingsAccountActivateUiState.ShowSavingAccountActivatedSuccessfully(GenericResponse()),
            SavingsAccountActivateUiState.ShowError("Error")
        )
}

@Composable
@Preview(showSystemUi = true)
fun PreviewSavingsAccountActivateScreen(
    @PreviewParameter(SavingsAccountActivateScreenPreviewProvider::class) savingsAccountActivateUiState: SavingsAccountActivateUiState
) {
    SavingsAccountActivateScreen(
        uiState = savingsAccountActivateUiState,
        navigateBack = { },
        activateSavings = {}
    )
}
