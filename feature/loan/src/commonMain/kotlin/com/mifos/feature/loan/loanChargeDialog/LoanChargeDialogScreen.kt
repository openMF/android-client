/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
@file:OptIn(ExperimentalMaterial3Api::class)

package com.mifos.feature.loan.loanChargeDialog

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_amount
import androidclient.feature.loan.generated.resources.feature_loan_charge_cancel
import androidclient.feature.loan.generated.resources.feature_loan_charge_created_successfully
import androidclient.feature.loan.generated.resources.feature_loan_charge_dialog
import androidclient.feature.loan.generated.resources.feature_loan_charge_due_date
import androidclient.feature.loan.generated.resources.feature_loan_charge_name
import androidclient.feature.loan.generated.resources.feature_loan_charge_select
import androidclient.feature.loan.generated.resources.feature_loan_charge_submit
import androidclient.feature.loan.generated.resources.feature_loan_failed_to_load_loan_charges
import androidclient.feature.loan.generated.resources.feature_loan_locale
import androidclient.feature.loan.generated.resources.feature_loan_message_field_required
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.model.objects.payloads.ChargesPayload
import com.mifos.room.entities.client.ChargesEntity
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun LoanChargeDialogScreen(
    loanId: Int,
    onSuccess: () -> Unit,
    onDismiss: () -> Unit,
    viewModel: LoanChargeDialogViewModel = koinViewModel(),
) {
    val state by viewModel.loanChargeDialogUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loanAllChargesV3(loanId)
    }

    LoanChargeDialogScreen(
        state = state,
        onCreate = {
            viewModel.createLoanCharges(loanId, it)
        },
        onSuccess = onSuccess,
        onDismiss = onDismiss,
    )
}

@Composable
internal fun LoanChargeDialogScreen(
    state: LoanChargeDialogUiState,
    onCreate: (ChargesPayload) -> Unit,
    onSuccess: () -> Unit,
    onDismiss: () -> Unit,
) {
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val scope = rememberCoroutineScope()
    var amount by rememberSaveable { mutableStateOf("") }
    var amountError by rememberSaveable { mutableStateOf(false) }
    val locale by rememberSaveable { mutableStateOf("en") }
    var dueDate by rememberSaveable { mutableLongStateOf(Clock.System.now().toEpochMilliseconds()) }
    val dueDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = dueDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= Clock.System.now().toEpochMilliseconds()
            }
        },
    )
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    fun validateInput(): Boolean {
        if (amount.isEmpty()) {
            amountError = true
            return false
        }
        return true
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        dueDatePickerState.selectedDateMillis?.let {
                            dueDate = it
                        }
                    },
                ) { Text(stringResource(Res.string.feature_loan_charge_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    },
                ) { Text(stringResource(Res.string.feature_loan_charge_cancel)) }
            },
        ) {
            DatePicker(state = dueDatePickerState)
        }
    }

    Dialog(
        onDismissRequest = { onDismiss() },
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
        ) {
            Box(
                contentAlignment = Alignment.Center,
            ) {
                when (state) {
                    is LoanChargeDialogUiState.AllChargesV3 -> {
                        var name by rememberSaveable { mutableStateOf(state.list[0].name) }
                        var chargeId by rememberSaveable {
                            mutableIntStateOf(
                                state.list[0].id ?: 0,
                            )
                        }

                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = stringResource(Res.string.feature_loan_charge_dialog),
                                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                )
                                IconButton(onClick = { onDismiss() }) {
                                    Icon(
                                        imageVector = MifosIcons.Close,
                                        contentDescription = "",
                                        modifier = Modifier
                                            .width(30.dp)
                                            .height(30.dp),
                                    )
                                }
                            }

                            name?.let { itemValue ->
                                MifosTextFieldDropdown(
                                    value = itemValue,
                                    onValueChanged = { value ->
                                        name = value
                                    },
                                    label = stringResource(Res.string.feature_loan_charge_name),
                                    readOnly = true,
                                    onOptionSelected = { index, value ->

                                        state.list[index].id.let {
                                            chargeId = it
                                        }
                                        name = value
                                    },
                                    options = state.list.map { it.name.toString() },
                                )
                            }

                            MifosOutlinedTextField(
                                value = amount,
                                onValueChange = { value ->
                                    amount = value
                                    amountError = false
                                },
                                label = stringResource(Res.string.feature_loan_amount),
                                error = if (amountError) {
                                    stringResource(
                                        Res.string
                                            .feature_loan_message_field_required,
                                    )
                                } else {
                                    null
                                },
                                trailingIcon = {
                                    if (amountError) {
                                        Icon(
                                            imageVector = MifosIcons.Error,
                                            contentDescription = null,
                                        )
                                    }
                                },
                            )

                            MifosDatePickerTextField(
                                value = DateHelper.getDateAsStringFromLong(
                                    dueDate,
                                ),
                                label = stringResource(Res.string.feature_loan_charge_due_date),
                                openDatePicker = {
                                    showDatePicker = true
                                },
                            )

                            MifosOutlinedTextField(
                                value = locale,
                                onValueChange = {},
                                label = stringResource(Res.string.feature_loan_locale),
                                error = null,
                                readOnly = true,
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (validateInput()) {
                                        val payload = ChargesPayload().apply {
                                            this.amount = amount
                                            this.locale = locale
                                            this.dateFormat = "dd-MM-yyyy"
                                            this.chargeId = chargeId
                                            this.dueDate = DateHelper.getDateAsStringFromLong(
                                                dueDate,
                                            )
                                        }
                                        onCreate(payload)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                            ) {
                                Text(
                                    text = stringResource(
                                        Res.string
                                            .feature_loan_charge_submit,
                                    ),
                                )
                            }
                        }
                    }

                    is LoanChargeDialogUiState.Error -> MifosSweetError(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        message = stringResource(state.message),
                    ) {
                    }

                    is LoanChargeDialogUiState.Loading -> MifosCircularProgress(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(300.dp),
                    )

                    is LoanChargeDialogUiState.LoanChargesCreatedSuccessfully -> {
                        val message = stringResource(
                            Res.string
                                .feature_loan_charge_created_successfully,
                        )
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = message,
                            )
                        }
                        onSuccess()
                    }
                }
            }
        }
    }
}

private class LoanChargeDialogUiStateProvider : PreviewParameterProvider<LoanChargeDialogUiState> {

    override val values: Sequence<LoanChargeDialogUiState>
        get() = sequenceOf(
            LoanChargeDialogUiState.AllChargesV3(sampleChargeList),
            LoanChargeDialogUiState.Error(Res.string.feature_loan_failed_to_load_loan_charges),
            LoanChargeDialogUiState.Loading,
            LoanChargeDialogUiState.LoanChargesCreatedSuccessfully,
        )
}

@Preview
@Composable
private fun LoanChargeDialogScreenPreview(
    @PreviewParameter(LoanChargeDialogUiStateProvider::class) state: LoanChargeDialogUiState,
) {
    LoanChargeDialogScreen(state = state, onCreate = {}, onSuccess = { }, onDismiss = {})
}

val sampleChargeList = List(10) {
    ChargesEntity(name = "name $it")
}
