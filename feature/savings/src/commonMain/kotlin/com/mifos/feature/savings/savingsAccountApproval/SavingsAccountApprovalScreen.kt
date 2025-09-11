/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.savings.savingsAccountApproval

import androidclient.feature.savings.generated.resources.Res
import androidclient.feature.savings.generated.resources.feature_savings_approval_savings_date
import androidclient.feature.savings.generated.resources.feature_savings_approve_savings
import androidclient.feature.savings.generated.resources.feature_savings_approved_on
import androidclient.feature.savings.generated.resources.feature_savings_cancel
import androidclient.feature.savings.generated.resources.feature_savings_save
import androidclient.feature.savings.generated.resources.feature_savings_savings_approval_reason
import androidclient.feature.savings.generated.resources.feature_savings_savings_approved
import androidclient.feature.savings.generated.resources.feature_savings_select_date
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
import com.mifos.core.model.objects.account.loan.SavingsApproval
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
 * Created by Pronay Sarker on 12/07/2024 (12:21 AM)
 */
@Composable
internal fun SavingsAccountApprovalScreen(
    navigateBack: () -> Unit,
    viewModel: SavingsAccountApprovalViewModel = koinViewModel(),
) {
    val uiState by viewModel.savingsAccountApprovalUiState.collectAsStateWithLifecycle()

    SavingsAccountApprovalScreen(
        uiState = uiState,
        navigateBack = navigateBack,
        approveLoan = { viewModel.approveSavingsApplication(it) },
    )
}

@Composable
internal fun SavingsAccountApprovalScreen(
    uiState: SavingsAccountApprovalUiState,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    approveLoan: (SavingsApproval) -> Unit,
) {
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    MifosScaffold(
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        title = stringResource(Res.string.feature_savings_approve_savings),
        onBackPressed = navigateBack,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            when (uiState) {
                SavingsAccountApprovalUiState.Initial -> {
                    SavingsAccountApprovalContent(approveLoan = approveLoan)
                }

                is SavingsAccountApprovalUiState.ShowError -> {
                    MifosSweetError(
                        message = uiState.message,
                        isRetryEnabled = false,
                        onclick = {},
                    )
                }

                SavingsAccountApprovalUiState.ShowProgressbar -> {
                    MifosProgressIndicator()
                }

                is SavingsAccountApprovalUiState.ShowSavingAccountApprovedSuccessfully ->
                    MifosAlertDialog(
                        dialogTitle = "OK",
                        dialogText = stringResource(Res.string.feature_savings_savings_approved),
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
private fun SavingsAccountApprovalContent(
    approveLoan: (savingsApproval: SavingsApproval) -> Unit,
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
                ) { Text(stringResource(Res.string.feature_savings_select_date)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePickerDialog = false
                    },
                ) { Text(stringResource(Res.string.feature_savings_cancel)) }
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
            text = stringResource(Res.string.feature_savings_approved_on),
            modifier = Modifier.padding(start = 16.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosDatePickerTextField(
            value = DateHelper.getDateAsStringFromLong(approvalDate),
            label = stringResource(Res.string.feature_savings_approval_savings_date),
        ) {
            showDatePickerDialog = true
        }

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = reasonForApproval,
            onValueChange = { reasonForApproval = it },
            label = stringResource(Res.string.feature_savings_savings_approval_reason),
            error = null,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .heightIn(44.dp),
            onClick = {
                approveLoan.invoke(
                    SavingsApproval(
                        approvedOnDate = approvalDate.toString(),
                        note = reasonForApproval,
                    ),
                )
            },
        ) {
            Text(text = stringResource(Res.string.feature_savings_save))
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
            SavingsAccountApprovalUiState.ShowError("Error"),
        )
}

@Composable
@Preview
private fun PreviewSavingsAccountApprovalScreen(
    @PreviewParameter(SavingsAccountApprovalScreenPreviewProvider::class)
    savingsAccountApprovalUiState: SavingsAccountApprovalUiState,
) {
    SavingsAccountApprovalScreen(
        uiState = savingsAccountApprovalUiState,
        navigateBack = { },
    ) {
    }
}
