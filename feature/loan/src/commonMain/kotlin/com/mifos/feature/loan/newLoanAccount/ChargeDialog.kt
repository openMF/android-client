package com.mifos.feature.loan.newLoanAccount

import androidclient.feature.loan.generated.resources.feature_loan_cancel
import androidclient.feature.loan.generated.resources.feature_loan_select
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosBasicDialog
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosTextFieldConfig
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.theme.DesignToken
import core.designsystem.generated.resources.Res
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChargeDialog(
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
    onDateChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
) {
    MifosBasicDialog(
        title = title,
        confirmText = confirmText,
        dismissText = dismissText,
        onConfirm = onConfirm,
        onDismissRequest = onDismiss,
        isConfirmEnabled= chargeAmount.isNotEmpty() && chargeType.isNotEmpty(),
        content = {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
            )

            Column {
                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { onDatePick(false) },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    onDatePick(false)
                                    datePickerState.selectedDateMillis?.let {
                                        onDateChange(DateHelper.getDateAsStringFromLong(it))
                                    }
                                },
                            ) { Text("Ok") }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { onDatePick(false) },
                            ) { Text("Cancel") }
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
                    label = "Name",
                )

                MifosDatePickerTextField(
                    value = selectedDate,
                    label = "Date",
                    openDatePicker = { onDatePick(true) },
                )

                Spacer(Modifier.height(DesignToken.padding.large))

                MifosOutlinedTextField(
                    value = chargeAmount,
                    onValueChange = onAmountChange,
                    label = "Amount",
                    config = MifosTextFieldConfig(
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                        )
                    ),
                )

                Spacer(Modifier.height(DesignToken.padding.large))

                MifosOutlinedTextField(
                    value = chargeType,
                    onValueChange = {},
                    label = "Type",
                    config = MifosTextFieldConfig(
                        readOnly = true,
                        enabled = false
                    ),
                )

                Spacer(Modifier.height(DesignToken.padding.large))

                MifosOutlinedTextField(
                    value = chargeCollectedOn,
                    onValueChange = {},
                    label = "Collected On",
                    config = MifosTextFieldConfig(
                        readOnly = true,
                        enabled = false
                    ),
                )
            }
        },
    )
}
