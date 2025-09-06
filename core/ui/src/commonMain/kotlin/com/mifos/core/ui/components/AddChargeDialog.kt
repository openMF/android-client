/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.ui.components

import androidclient.core.ui.generated.resources.Res
import androidclient.core.ui.generated.resources.amount
import androidclient.core.ui.generated.resources.cancel
import androidclient.core.ui.generated.resources.collected_on
import androidclient.core.ui.generated.resources.date
import androidclient.core.ui.generated.resources.name
import androidclient.core.ui.generated.resources.ok
import androidclient.core.ui.generated.resources.type
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.mifos.core.designsystem.component.MifosBottomSheet
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosTextFieldConfig
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTheme
import com.mifos.core.designsystem.theme.MifosTypography
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddChargeBottomSheet(
    title: String,
    confirmText: String,
    dismissText: String,
    showDatePicker: Boolean,
    selectedChargeName: String,
    selectedDate: String,
    chargeAmount: String,
    chargeType: String,
    chargeCollectedOn: String,
    chargeOptions: List<String>,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    onChargeSelected: (Int, String) -> Unit,
    onDatePick: (Boolean) -> Unit,
    onDateChange: (Long) -> Unit,
    onAmountChange: (String) -> Unit,
) {
    MifosBottomSheet(
        onDismiss = onDismiss,
        content = {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
            )

            Column(
                Modifier.padding(DesignToken.padding.large),
            ) {
                Text(text = title, style = MifosTypography.titleMediumEmphasized)

                Spacer(Modifier.height(DesignToken.padding.large))
                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { onDatePick(false) },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    onDatePick(false)
                                    datePickerState.selectedDateMillis?.let {
                                        onDateChange(it)
                                    }
                                },
                            ) { Text(stringResource(Res.string.ok)) }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { onDatePick(false) },
                            ) { Text(stringResource(Res.string.cancel)) }
                        },
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }

                MifosTextFieldDropdown(
                    value = selectedChargeName,
                    onValueChanged = {},
                    onOptionSelected = onChargeSelected,
                    options = chargeOptions,
                    label = stringResource(Res.string.name),
                )

                MifosDatePickerTextField(
                    value = selectedDate,
                    label = stringResource(Res.string.date),
                    openDatePicker = { onDatePick(true) },
                )

                Spacer(Modifier.height(DesignToken.padding.large))

                MifosOutlinedTextField(
                    value = chargeAmount,
                    onValueChange = onAmountChange,
                    label = stringResource(Res.string.amount),
                    config = MifosTextFieldConfig(
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                        ),
                    ),
                )

                Spacer(Modifier.height(DesignToken.padding.large))

                MifosOutlinedTextField(
                    value = chargeType,
                    onValueChange = {},
                    label = stringResource(Res.string.type),
                    config = MifosTextFieldConfig(
                        readOnly = true,
                        enabled = false,
                    ),
                )

                Spacer(Modifier.height(DesignToken.padding.large))

                MifosOutlinedTextField(
                    value = chargeCollectedOn,
                    onValueChange = {},
                    label = stringResource(Res.string.collected_on),
                    config = MifosTextFieldConfig(
                        readOnly = true,
                        enabled = false,
                    ),
                )

                Spacer(Modifier.height(DesignToken.padding.large))

                MifosTwoButtonRow(
                    firstBtnText = dismissText,
                    secondBtnText = confirmText,
                    onFirstBtnClick = onDismiss,
                    onSecondBtnClick = onConfirm,
                    isSecondButtonEnabled = chargeAmount.isNotEmpty() && chargeType.isNotEmpty(),
                )
            }
        },
    )
}

@Preview
@Composable
private fun AddChargeBottomSheetPreview() {
    val sampleChargeOptions = listOf("Bank Fee", "Overdue Fee", "Processing Fee")

    MifosTheme {
        AddChargeBottomSheet(
            title = "Add Charge",
            confirmText = "Confirm",
            dismissText = "Cancel",
            showDatePicker = false,
            selectedChargeName = sampleChargeOptions[0],
            selectedDate = "2025-09-06",
            chargeAmount = "1500",
            chargeType = "Flat",
            chargeCollectedOn = "2025-09-06",
            chargeOptions = sampleChargeOptions,
            onConfirm = { },
            onDismiss = { },
            onChargeSelected = { _, _ -> },
            onDatePick = { },
            onDateChange = { },
            onAmountChange = {},
        )
    }
}
