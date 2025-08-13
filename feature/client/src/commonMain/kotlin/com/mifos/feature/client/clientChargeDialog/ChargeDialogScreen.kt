/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.mifos.feature.client.clientChargeDialog

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_charge_amount
import androidclient.feature.client.generated.resources.feature_client_charge_cancel
import androidclient.feature.client.generated.resources.feature_client_charge_dialog
import androidclient.feature.client.generated.resources.feature_client_charge_invalid_amount_format
import androidclient.feature.client.generated.resources.feature_client_charge_locale
import androidclient.feature.client.generated.resources.feature_client_charge_name
import androidclient.feature.client.generated.resources.feature_client_charge_select
import androidclient.feature.client.generated.resources.feature_client_charge_submit
import androidclient.feature.client.generated.resources.feature_client_due_date
import androidclient.feature.client.generated.resources.feature_client_failed_to_load_charges
import androidclient.feature.client.generated.resources.feature_client_message_field_required
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mifos.core.common.utils.Constants.DATE_FORMAT_LONG
import com.mifos.core.common.utils.Constants.LOCALE_EN
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.common.utils.formatDate
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.model.objects.payloads.ChargesPayload
import com.mifos.core.model.objects.template.client.ChargeTemplate
import com.mifos.core.ui.util.DevicePreview
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

@Composable
internal fun ChargeDialogScreen(
    state: ChargeDialogUiState,
    onDismiss: () -> Unit,
    onCreateCharge: (ChargesPayload) -> Unit,
    onChargeCreated: () -> Unit,
    onRetry: () -> Unit,
) {
    Dialog(
        onDismissRequest = { onDismiss() },
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background,
        ) {
            Box(
                modifier = Modifier
                    .height(455.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center,
            ) {
                when (state) {
                    is ChargeDialogUiState.AllChargesV2 -> {
                        ChargeDialogContent(
                            chargeTemplate = state.chargeTemplate,
                            selectedChargeId = state.selectedChargeId,
                            selectedChargeName = state.selectedChargeName,
                            onDismiss = onDismiss,
                            onCreate = onCreateCharge,
                        )
                    }

                    is ChargeDialogUiState.Error -> MifosSweetError(
                        message = stringResource(state.message),
                    ) {
                        onRetry()
                    }

                    is ChargeDialogUiState.Loading -> MifosCircularProgress()

                    is ChargeDialogUiState.ChargesCreatedSuccessfully -> {
                        onChargeCreated()
                    }
                }
            }
        }
    }
}

@Composable
private fun ChargeDialogContent(
    chargeTemplate: ChargeTemplate,
    selectedChargeId: Int,
    selectedChargeName: String,
    onDismiss: () -> Unit,
    onCreate: (ChargesPayload) -> Unit,
) {
    var amount by rememberSaveable { mutableStateOf("") }
    var amountTouched by rememberSaveable { mutableStateOf(false) }

    var chargeName by rememberSaveable { mutableStateOf(selectedChargeName) }
    var chargeNameTouched by rememberSaveable { mutableStateOf(false) }

    val locale by rememberSaveable { mutableStateOf(LOCALE_EN) }
    var dueDate by rememberSaveable { mutableLongStateOf(Clock.System.now().toEpochMilliseconds()) }
    var chargeId by rememberSaveable { mutableIntStateOf(selectedChargeId) }
    val selectedChargeOption = chargeTemplate.chargeOptions.find { it.id == chargeId }
    val currencyDecimalPlaces = selectedChargeOption?.currency?.decimalPlaces?.toInt() ?: 2
    val amountValidation = validateAmount(amount, currencyDecimalPlaces)
    val isAmountValid = amountValidation == AmountValidationResult.VALID

    val dueDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = dueDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= Clock.System.now().toEpochMilliseconds()
            }
        },
    )
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        dueDatePickerState.selectedDateMillis?.let {
                            dueDate = it
                        }
                    },
                ) { Text(stringResource(Res.string.feature_client_charge_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false },
                ) { Text(stringResource(Res.string.feature_client_charge_cancel)) }
            },
        ) {
            DatePicker(state = dueDatePickerState)
        }
    }

    val isChargeNameValid = validateChargeName(chargeName)
    val isFormValid = isAmountValid && isChargeNameValid

    Column(
        modifier = Modifier.padding(20.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.feature_client_charge_dialog),
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = MifosIcons.Close,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .width(30.dp)
                        .height(30.dp),
                )
            }
        }

        MifosTextFieldDropdown(
            value = chargeName,
            onValueChanged = { value ->
                chargeName = value
                chargeNameTouched = true
            },
            label = stringResource(Res.string.feature_client_charge_name),
            readOnly = true,
            onOptionSelected = { index, value ->
                chargeId = chargeTemplate.chargeOptions[index].id ?: -1
                chargeName = value
                chargeNameTouched = true
            },
            options = chargeTemplate.chargeOptions.map { it.name ?: "" },
            errorMessage = if (!isChargeNameValid && chargeNameTouched) {
                stringResource(Res.string.feature_client_message_field_required)
            } else {
                null
            },
        )

        MifosOutlinedTextField(
            value = amount,
            onValueChange = { value ->
                amount = value
                amountTouched = true
            },
            label = stringResource(Res.string.feature_client_charge_amount),
            error = when {
                amountValidation == AmountValidationResult.EMPTY && amountTouched ->
                    stringResource(Res.string.feature_client_message_field_required)
                amountValidation == AmountValidationResult.INVALID_FORMAT && amountTouched ->
                    stringResource(Res.string.feature_client_charge_invalid_amount_format)
                else -> null
            },
            trailingIcon = {
                if (!isAmountValid && amountTouched) {
                    Icon(imageVector = MifosIcons.Error, contentDescription = null)
                }
            },
        )

        MifosDatePickerTextField(
            value = DateHelper.getDateAsStringFromLong(dueDate),
            label = stringResource(Res.string.feature_client_due_date),
            openDatePicker = {
                showDatePicker = true
            },
        )

        MifosOutlinedTextField(
            value = locale,
            onValueChange = {},
            label = stringResource(Res.string.feature_client_charge_locale),
            error = null,
            readOnly = true,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            enabled = isFormValid,
            onClick = {
                amountTouched = true
                chargeNameTouched = true

                if (isFormValid) {
                    val payload = ChargesPayload(
                        amount = amount,
                        locale = locale,
                        dateFormat = DATE_FORMAT_LONG,
                        chargeId = chargeId,
                        dueDate = formatDate(dueDate),
                    )
                    onCreate(payload)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
        ) {
            Text(text = stringResource(Res.string.feature_client_charge_submit))
        }
    }
}

fun validateAmount(amount: String, decimalPlaces: Int): AmountValidationResult {
    if (amount.isBlank()) return AmountValidationResult.EMPTY

    val trimmed = amount.trim()
    val regex = if (decimalPlaces == 0) {
        Regex("^[1-9]\\d*$")
    } else {
        Regex("^\\s*(?=.*[1-9])\\d*(\\.\\d{1,$decimalPlaces})?\\s*$")
    }

    return if (regex.matches(trimmed)) {
        AmountValidationResult.VALID
    } else {
        AmountValidationResult.INVALID_FORMAT
    }
}

fun validateChargeName(name: String): Boolean = name.isNotBlank()

enum class AmountValidationResult {
    EMPTY,
    INVALID_FORMAT,
    VALID,
}

private class ChargeDialogScreenUiStateProvider : PreviewParameterProvider<ChargeDialogUiState> {

    override val values: Sequence<ChargeDialogUiState>
        get() = sequenceOf(
            ChargeDialogUiState.AllChargesV2(
                ChargeTemplate(false, emptyList()),
                selectedChargeName = "Charges Name",
                selectedChargeId = 0,
            ),
            ChargeDialogUiState.Error(Res.string.feature_client_failed_to_load_charges),
            ChargeDialogUiState.Loading,
            ChargeDialogUiState.ChargesCreatedSuccessfully,
        )
}

@DevicePreview
@Composable
private fun ChargeDialogScreenPreview(
    @PreviewParameter(ChargeDialogScreenUiStateProvider::class) state: ChargeDialogUiState,
) {
    ChargeDialogScreen(
        state = state,
        onDismiss = {},
        onCreateCharge = {},
        onChargeCreated = {},
        onRetry = {},
    )
}
