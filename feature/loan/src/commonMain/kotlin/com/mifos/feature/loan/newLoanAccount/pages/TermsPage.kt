/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.newLoanAccount.pages

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.back
import androidclient.feature.loan.generated.resources.create_standing_instructions
import androidclient.feature.loan.generated.resources.expected_disbursement
import androidclient.feature.loan.generated.resources.external_id
import androidclient.feature.loan.generated.resources.feature_loan_cancel
import androidclient.feature.loan.generated.resources.feature_loan_select
import androidclient.feature.loan.generated.resources.loan_officer
import androidclient.feature.loan.generated.resources.next
import androidclient.feature.loan.generated.resources.step_details
import androidclient.feature.loan.generated.resources.step_terms
import androidclient.feature.loan.generated.resources.submission_date
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosTextFieldConfig
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.feature.loan.newLoanAccount.NewLoanAccountAction
import com.mifos.feature.loan.newLoanAccount.NewLoanAccountState
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsPage(
    state: NewLoanAccountState,
    onAction: (NewLoanAccountAction) -> Unit,
    modifier: Modifier = Modifier,
) {

    val firstRepaymentOnDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
    )

    val interestChargedFromDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
    )

    if (state.showFirstRepaymentDatePick) {
        DatePickerDialog(
            onDismissRequest = {
                onAction(NewLoanAccountAction.OnFirstRepaymentDatePick(false))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAction(NewLoanAccountAction.OnFirstRepaymentDatePick(false))
                        firstRepaymentOnDatePickerState.selectedDateMillis?.let {
                            onAction(NewLoanAccountAction.OnFirstRepaymentDateChange(DateHelper.getDateAsStringFromLong(it)))
                        }
                    },
                ) { Text(stringResource(Res.string.feature_loan_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onAction(NewLoanAccountAction.OnFirstRepaymentDatePick(false))
                    },
                ) { Text(stringResource(Res.string.feature_loan_cancel)) }
            },
        ) {
            DatePicker(state = firstRepaymentOnDatePickerState)
        }
    }

    if (state.showInterestChargedFromDatePick) {
        DatePickerDialog(
            onDismissRequest = {
                onAction(NewLoanAccountAction.OnInterestChargedFromDatePick(false))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAction(NewLoanAccountAction.OnInterestChargedFromDatePick(false))
                        interestChargedFromDatePickerState.selectedDateMillis?.let {
                            onAction(NewLoanAccountAction.OnInterestChargedFromChange(DateHelper.getDateAsStringFromLong(it)))
                        }
                    },
                ) { Text(stringResource(Res.string.feature_loan_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onAction(NewLoanAccountAction.OnInterestChargedFromDatePick(false))
                    },
                ) { Text(stringResource(Res.string.feature_loan_cancel)) }
            },
        ) {
            DatePicker(state = interestChargedFromDatePickerState)
        }
    }

    Column(Modifier.fillMaxSize()) {
        Column(
            modifier = modifier.weight(1f).verticalScroll(rememberScrollState()),
        ) {

            Text(
                text = "Terms",
                style = MifosTypography.labelLargeEmphasized,
            )

            Spacer(Modifier.height(DesignToken.padding.large))

            MifosOutlinedTextField(
                value = state.principalAmount.toString(),
                onValueChange = {
                    onAction(NewLoanAccountAction.OnPrincipalAmountChange(it.toIntOrNull()?:0))
                },
                label = "Principal",
            )
            Spacer(Modifier.height(DesignToken.padding.large))

            Text(
                text = "Term Options?",
                style = MifosTypography.labelLargeEmphasized,
            )

            Spacer(Modifier.height(DesignToken.padding.large))

            MifosOutlinedTextField(
                value = (state.noOfRepayments*state.repaidEvery).toString(),
                onValueChange = {},
                label = "Loan Term",
                config= MifosTextFieldConfig(
                    readOnly = true,
                    enabled = false
                )
            )
            Spacer(Modifier.height(DesignToken.padding.medium))

            MifosTextFieldDropdown(
                value = if (state.termFrequencyIndex == -1) {
                    ""
                } else {
                    state.loanTemplate?.termFrequencyTypeOptions[state.termFrequencyIndex]?.value?:""
                },
                onValueChanged = {},
                onOptionSelected = { index, value ->
                    onAction(NewLoanAccountAction.OnTermFrequencyIndexChange(index))
                },
                options = state.loanTemplate?.termFrequencyTypeOptions?.map { it.value?:"" }?: emptyList(),
                label = "Term Frequency",
            )

            Text(
                text = "Repayments",
                style = MifosTypography.labelLargeEmphasized,
            )

            Spacer(Modifier.height(DesignToken.padding.large))

            MifosOutlinedTextField(
                value = state.noOfRepayments.toString(),
                onValueChange = {
                    onAction(NewLoanAccountAction.OnNoOfRepaymentsChange(it.toIntOrNull()?:0))
                },
                label = "Number of Repayments",
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            MifosDatePickerTextField(
                value = state.firstRepaymentDate,
                label = "First Repayment Date",
                openDatePicker = {
                    onAction(NewLoanAccountAction.OnFirstRepaymentDatePick(true))
                },
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            MifosDatePickerTextField(
                value = state.interestChargedFromDate,
                label = "Interest Charged From",
                openDatePicker = {
                    onAction(NewLoanAccountAction.OnInterestChargedFromDatePick(true))
                },
            )

            Spacer(Modifier.height(DesignToken.padding.large))

            Text(
                text = "Repaid Every?",
                style = MifosTypography.labelLargeEmphasized,
            )

            Spacer(Modifier.height(DesignToken.padding.large))

            MifosOutlinedTextField(
                value = state.repaidEvery.toString(),
                onValueChange = {
                    onAction(NewLoanAccountAction.OnRepaidEveryChange(it.toIntOrNull()?:0))
                },
                label = "Repaid Every",
            )

            Spacer(Modifier.height(DesignToken.padding.medium))

            MifosTextFieldDropdown(
                value = if (state.termFrequencyIndex == -1) {
                    ""
                } else {
                    state.loanTemplate?.termFrequencyTypeOptions[state.termFrequencyIndex]?.value?:""
                },
                onValueChanged = {},
                onOptionSelected = { index, value -> },
                options = state.loanTemplate?.termFrequencyTypeOptions?.map { it.value?:"" }?: emptyList(),
                label = "Frequency",
                readOnly = true
            )

            if(state.termFrequencyIndex != -1 && (state.loanTemplate?.termFrequencyTypeOptions[state.termFrequencyIndex]?.value
                    ?: "") == "Months"
            ){
                MifosTextFieldDropdown(
                    value = if (state.selectedOnIndex == -1) {
                        ""
                    } else {
                        state.loanTemplate?.repaymentFrequencyNthDayTypeOptions[state.selectedOnIndex]?.value?:""
                    },
                    onValueChanged = {},
                    onOptionSelected = { index, value ->
                        onAction(NewLoanAccountAction.OnSelectedOnIndexChange(index))
                    },
                    options = state.loanTemplate?.repaymentFrequencyNthDayTypeOptions?.map { it.value?:"" }?: emptyList(),
                    label = "Select On",
                )

                MifosTextFieldDropdown(
                    value = if (state.selectedDayIndex == -1) {
                        ""
                    } else {
                        state.loanTemplate?.repaymentFrequencyDaysOfWeekTypeOptions[state.selectedDayIndex]?.value?:""
                    },
                    onValueChanged = {},
                    onOptionSelected = { index, value ->
                        onAction(NewLoanAccountAction.OnSelectedDayIndexChange(index))
                    },
                    options = state.loanTemplate?.repaymentFrequencyDaysOfWeekTypeOptions?.map { it.value?:"" }?: emptyList(),
                    label = "Select Day",
                )

                Spacer(Modifier.height(DesignToken.padding.large))

            }
            Text(
                text = "Nominal interest rate",
                style = MifosTypography.labelLargeEmphasized,
            )

            Spacer(Modifier.height(DesignToken.padding.large))

            MifosOutlinedTextField(
                value = state.nominalInterestRate.toString(),
                onValueChange = {
                    onAction(NewLoanAccountAction.OnNominalInterestRateChange(it.toIntOrNull()?:0))
                },
                label = "Nominal interest rate (in %)",
                config= MifosTextFieldConfig(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            )

            Spacer(Modifier.height(DesignToken.padding.medium))

            MifosTextFieldDropdown(
                value = if (state.nominalFrequencyIndex == -1) {
                    ""
                } else {
                    state.loanTemplate?.interestRateFrequencyTypeOptions[state.nominalFrequencyIndex]?.value?:""
                },
                onValueChanged = {},
                onOptionSelected = { index, value ->
                    onAction(NewLoanAccountAction.OnNominalFrequencyIndexChange(index))
                },
                options = state.loanTemplate?.interestRateFrequencyTypeOptions?.map { it.value?:"" }?: emptyList(),
                label = "Frequency",
            )

            MifosTextFieldDropdown(
                value = if (state.nominalInterestMethodIndex == -1) {
                    ""
                } else {
                    state.loanTemplate?.interestTypeOptions[state.nominalInterestMethodIndex]?.value?:""
                },
                onValueChanged = {},
                onOptionSelected = { index, value ->
                    onAction(NewLoanAccountAction.OnNominalMethodIndexChange(index))
                },
                options = state.loanTemplate?.interestTypeOptions?.map { it.value?:"" }?: emptyList(),
                label = "Interest Method",
            )

            MifosTextFieldDropdown(
                value = if (state.nominalAmortizationIndex == -1) {
                    ""
                } else {
                    state.loanTemplate?.amortizationTypeOptions[state.nominalAmortizationIndex]?.value?:""
                },
                onValueChanged = {},
                onOptionSelected = { index, value ->
                    onAction(NewLoanAccountAction.OnNominalAmortizationIndexChange(index))
                },
                options = state.loanTemplate?.amortizationTypeOptions?.map { it.value?:"" }?: emptyList(),
                label = "Amortization",
            )

            Row(
                Modifier.fillMaxWidth()
                    .clickable{
                        onAction(NewLoanAccountAction.OnEqualAmortizationCheckChange(!state.isCheckedEqualAmortization))
                    },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = state.isCheckedEqualAmortization,
                    onCheckedChange = {
                        onAction(NewLoanAccountAction.OnEqualAmortizationCheckChange(it))
                    },
                )
                Text(
                    text = "Is Equal Amortization",
                    style = MifosTypography.labelLarge,
                )
            }

            Spacer(Modifier.height(DesignToken.padding.large))

            Text(
                text = "Loan Schedule",
                style = MifosTypography.labelLargeEmphasized,
            )

            Spacer(Modifier.height(DesignToken.padding.large))

            Text(
                text = "Loan Schedule Type : Cumulative",
                style = MifosTypography.labelLargeEmphasized,
            )

            Spacer(Modifier.height(DesignToken.padding.large))

            MifosTextFieldDropdown(
                value = if (state.repaymentStrategyIndex == -1) {
                    ""
                } else {
                    state.loanTemplate?.transactionProcessingStrategyOptions[state.repaymentStrategyIndex]?.name?:""
                },
                onValueChanged = {},
                onOptionSelected = { index, value ->
                    onAction(NewLoanAccountAction.OnRepaymentStrategyIndexChange(index))
                },
                options = state.loanTemplate?.transactionProcessingStrategyOptions?.map { it.name?:"" }?: emptyList(),
                label = "Repayment Strategy",
            )

            MifosOutlinedTextField(
                value = state.balloonRepaymentAmount.toString(),
                onValueChange = {
                    onAction(NewLoanAccountAction.OnBalloonRepaymentAmountChange(it.toIntOrNull()?:0))
                },
                label = "Balloon Repayment Amount",
                config= MifosTextFieldConfig(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            )

            Spacer(Modifier.height(DesignToken.padding.large))

            Text(
                text = "Interest Calculations",
                style = MifosTypography.labelLargeEmphasized,
            )

            Spacer(Modifier.height(DesignToken.padding.large))

            MifosTextFieldDropdown(
                value = if (state.interestCalculationPeriodIndex == -1) {
                    ""
                } else {
                    state.loanTemplate?.interestCalculationPeriodTypeOptions[state.interestCalculationPeriodIndex]?.value?:""
                },
                onValueChanged = {},
                onOptionSelected = { index, value ->
                    onAction(NewLoanAccountAction.OnInterestCalculationPeriodIndexChange(index))
                },
                options = state.loanTemplate?.interestCalculationPeriodTypeOptions?.map { it.value?:"" }?: emptyList(),
                label = "Interest Calculation Period",
            )

            Row(
                Modifier.fillMaxWidth()
                    .clickable{
                        onAction(NewLoanAccountAction.OnInterestPartialPeriodCheckChange(!state.isCheckedEqualAmortization))
                    },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = state.isCheckedInterestPartialPeriod,
                    onCheckedChange = {
                        onAction(NewLoanAccountAction.OnInterestPartialPeriodCheckChange(it))
                    },
                )
                Text(
                    text = "Calculate interest for exact days in partial period",
                    style = MifosTypography.labelLarge,
                )
            }

            MifosOutlinedTextField(
                value = state.arrearsTolerance.toString(),
                onValueChange = {
                    onAction(NewLoanAccountAction.OnArrearsToleranceChange(it.toIntOrNull()?:0))
                },
                label = "Arrears Tolerance",
                config= MifosTextFieldConfig(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            )

            MifosOutlinedTextField(
                value = state.interestFreePeriod.toString(),
                onValueChange = {
                    onAction(NewLoanAccountAction.OnInterestFreePeriodChange(it.toIntOrNull()?:0))
                },
                label = "Interest Free Period",
                config= MifosTextFieldConfig(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            )

        }
        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.back),
            secondBtnText = stringResource(Res.string.next),
            onFirstBtnClick = {
                onAction(NewLoanAccountAction.NavigateBack)
            },
            onSecondBtnClick = {
                onAction(NewLoanAccountAction.NextStep)
            },
            isSecondButtonEnabled = state.isDetailsNextEnabled,
            modifier = Modifier.padding(top = DesignToken.padding.small),
        )
    }
}
