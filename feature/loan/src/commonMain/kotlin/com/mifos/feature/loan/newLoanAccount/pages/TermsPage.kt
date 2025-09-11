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
import androidclient.feature.loan.generated.resources.add_new
import androidclient.feature.loan.generated.resources.amortization
import androidclient.feature.loan.generated.resources.arrears_tolerance
import androidclient.feature.loan.generated.resources.back
import androidclient.feature.loan.generated.resources.balloon_repayment_amount
import androidclient.feature.loan.generated.resources.calculate_interest_partial
import androidclient.feature.loan.generated.resources.collateral_count
import androidclient.feature.loan.generated.resources.collateral_data
import androidclient.feature.loan.generated.resources.feature_loan_cancel
import androidclient.feature.loan.generated.resources.feature_loan_select
import androidclient.feature.loan.generated.resources.first_repayment_date
import androidclient.feature.loan.generated.resources.frequency
import androidclient.feature.loan.generated.resources.grace_on_interest_payment
import androidclient.feature.loan.generated.resources.grace_on_principal_payment
import androidclient.feature.loan.generated.resources.interest_calculation_period
import androidclient.feature.loan.generated.resources.interest_calculations
import androidclient.feature.loan.generated.resources.interest_charged_from
import androidclient.feature.loan.generated.resources.interest_free_period
import androidclient.feature.loan.generated.resources.interest_method
import androidclient.feature.loan.generated.resources.is_equal_amortization
import androidclient.feature.loan.generated.resources.loan_schedule
import androidclient.feature.loan.generated.resources.loan_schedule_type
import androidclient.feature.loan.generated.resources.loan_term
import androidclient.feature.loan.generated.resources.moratorium
import androidclient.feature.loan.generated.resources.next
import androidclient.feature.loan.generated.resources.no
import androidclient.feature.loan.generated.resources.nominal_interest_rate
import androidclient.feature.loan.generated.resources.nominal_interest_rate_percent
import androidclient.feature.loan.generated.resources.number_of_repayments
import androidclient.feature.loan.generated.resources.on_arrears_ageing
import androidclient.feature.loan.generated.resources.principal
import androidclient.feature.loan.generated.resources.recalculate_interest
import androidclient.feature.loan.generated.resources.repaid_every
import androidclient.feature.loan.generated.resources.repaid_every_label
import androidclient.feature.loan.generated.resources.repayment_strategy
import androidclient.feature.loan.generated.resources.repayments
import androidclient.feature.loan.generated.resources.select_day
import androidclient.feature.loan.generated.resources.select_on
import androidclient.feature.loan.generated.resources.term_frequency
import androidclient.feature.loan.generated.resources.term_options
import androidclient.feature.loan.generated.resources.terms
import androidclient.feature.loan.generated.resources.view
import androidclient.feature.loan.generated.resources.yes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosTextFieldConfig
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosListingComponentOutline
import com.mifos.core.ui.components.MifosListingRowItem
import com.mifos.core.ui.components.MifosRowWithTextAndButton
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.feature.loan.newLoanAccount.NewLoanAccountAction
import com.mifos.feature.loan.newLoanAccount.NewLoanAccountState
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
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
                text = stringResource(Res.string.terms),
                style = MifosTypography.labelLargeEmphasized,
            )

            Spacer(Modifier.height(DesignToken.padding.large))

            MifosOutlinedTextField(
                value = state.principalAmountText,
                onValueChange = {
                    onAction(NewLoanAccountAction.OnPrincipalAmountChange(it.toDoubleOrNull() ?: 0.0, it))
                },
                label = stringResource(Res.string.principal),
                config = MifosTextFieldConfig(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                    ),
                ),
            )
            Spacer(Modifier.height(DesignToken.padding.large))

            Text(
                text = stringResource(Res.string.term_options),
                style = MifosTypography.labelLargeEmphasized,
            )

            Spacer(Modifier.height(DesignToken.padding.large))

            MifosOutlinedTextField(
                value = (state.noOfRepayments * state.repaidEvery).toString(),
                onValueChange = {},
                label = stringResource(Res.string.loan_term),
                config = MifosTextFieldConfig(
                    readOnly = true,
                    enabled = false,
                ),
            )
            Spacer(Modifier.height(DesignToken.padding.medium))

            MifosTextFieldDropdown(
                value = if (state.termFrequencyIndex == -1) {
                    ""
                } else {
                    state.loanTemplate?.termFrequencyTypeOptions[state.termFrequencyIndex]?.value ?: ""
                },
                onValueChanged = {},
                onOptionSelected = { index, value ->
                    onAction(NewLoanAccountAction.OnTermFrequencyIndexChange(index))
                },
                options = state.loanTemplate?.termFrequencyTypeOptions?.map { it.value ?: "" } ?: emptyList(),
                label = stringResource(Res.string.term_frequency),
            )

            Text(
                text = stringResource(Res.string.repayments),
                style = MifosTypography.labelLargeEmphasized,
            )

            Spacer(Modifier.height(DesignToken.padding.large))

            MifosOutlinedTextField(
                value = state.noOfRepayments.toString(),
                onValueChange = {
                    onAction(NewLoanAccountAction.OnNoOfRepaymentsChange(it.toIntOrNull() ?: 0))
                },
                label = stringResource(Res.string.number_of_repayments),
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            MifosDatePickerTextField(
                value = state.firstRepaymentDate,
                label = stringResource(Res.string.first_repayment_date),
                openDatePicker = {
                    onAction(NewLoanAccountAction.OnFirstRepaymentDatePick(true))
                },
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            MifosDatePickerTextField(
                value = state.interestChargedFromDate,
                label = stringResource(Res.string.interest_charged_from),
                openDatePicker = {
                    onAction(NewLoanAccountAction.OnInterestChargedFromDatePick(true))
                },
            )

            Spacer(Modifier.height(DesignToken.padding.large))

            Text(
                text = stringResource(Res.string.repaid_every),
                style = MifosTypography.labelLargeEmphasized,
            )

            Spacer(Modifier.height(DesignToken.padding.large))

            MifosOutlinedTextField(
                value = state.repaidEvery.toString(),
                onValueChange = {
                    onAction(NewLoanAccountAction.OnRepaidEveryChange(it.toIntOrNull() ?: 0))
                },
                label = stringResource(Res.string.repaid_every_label),
            )

            Spacer(Modifier.height(DesignToken.padding.medium))

            MifosTextFieldDropdown(
                value = if (state.termFrequencyIndex == -1) {
                    ""
                } else {
                    state.loanTemplate?.termFrequencyTypeOptions[state.termFrequencyIndex]?.value ?: ""
                },
                onValueChanged = {},
                onOptionSelected = { index, value -> },
                options = state.loanTemplate?.termFrequencyTypeOptions?.map { it.value ?: "" } ?: emptyList(),
                label = stringResource(Res.string.frequency),
                readOnly = true,
            )

            if (state.termFrequencyIndex != -1 && (
                    state.loanTemplate?.termFrequencyTypeOptions[state.termFrequencyIndex]?.value
                        ?: ""
                    ) == "Months"
            ) {
                MifosTextFieldDropdown(
                    value = if (state.selectedOnIndex == -1) {
                        ""
                    } else {
                        state.loanTemplate?.repaymentFrequencyNthDayTypeOptions[state.selectedOnIndex]?.value ?: ""
                    },
                    onValueChanged = {},
                    onOptionSelected = { index, value ->
                        onAction(NewLoanAccountAction.OnSelectedOnIndexChange(index))
                    },
                    options = state.loanTemplate?.repaymentFrequencyNthDayTypeOptions?.map { it.value ?: "" } ?: emptyList(),
                    label = stringResource(Res.string.select_on),
                )

                MifosTextFieldDropdown(
                    value = if (state.selectedDayIndex == -1) {
                        ""
                    } else {
                        state.loanTemplate?.repaymentFrequencyDaysOfWeekTypeOptions[state.selectedDayIndex]?.value ?: ""
                    },
                    onValueChanged = {},
                    onOptionSelected = { index, value ->
                        onAction(NewLoanAccountAction.OnSelectedDayIndexChange(index))
                    },
                    options = state.loanTemplate?.repaymentFrequencyDaysOfWeekTypeOptions?.map { it.value ?: "" } ?: emptyList(),
                    label = stringResource(Res.string.select_day),
                )

                Spacer(Modifier.height(DesignToken.padding.large))
            }
            Text(
                text = stringResource(Res.string.nominal_interest_rate),
                style = MifosTypography.labelLargeEmphasized,
            )

            Spacer(Modifier.height(DesignToken.padding.large))

            MifosOutlinedTextField(
                value = state.nominalInterestRateText,
                onValueChange = {
                    onAction(NewLoanAccountAction.OnNominalInterestRateChange(it.toDoubleOrNull() ?: 0.0, it))
                },
                label = stringResource(Res.string.nominal_interest_rate_percent),
                config = MifosTextFieldConfig(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                    ),
                ),
            )

            Spacer(Modifier.height(DesignToken.padding.medium))

            MifosTextFieldDropdown(
                value = if (state.nominalFrequencyIndex == -1) {
                    ""
                } else {
                    state.loanTemplate?.interestRateFrequencyTypeOptions[state.nominalFrequencyIndex]?.value ?: ""
                },
                onValueChanged = {},
                onOptionSelected = { index, value ->
                    onAction(NewLoanAccountAction.OnNominalFrequencyIndexChange(index))
                },
                options = state.loanTemplate?.interestRateFrequencyTypeOptions?.map { it.value ?: "" } ?: emptyList(),
                label = stringResource(Res.string.frequency),
            )

            MifosTextFieldDropdown(
                value = if (state.nominalInterestMethodIndex == -1) {
                    ""
                } else {
                    state.loanTemplate?.interestTypeOptions[state.nominalInterestMethodIndex]?.value ?: ""
                },
                onValueChanged = {},
                onOptionSelected = { index, value ->
                    onAction(NewLoanAccountAction.OnNominalMethodIndexChange(index))
                },
                options = state.loanTemplate?.interestTypeOptions?.map { it.value ?: "" } ?: emptyList(),
                label = stringResource(Res.string.interest_method),
            )

            MifosTextFieldDropdown(
                value = if (state.nominalAmortizationIndex == -1) {
                    ""
                } else {
                    state.loanTemplate?.amortizationTypeOptions[state.nominalAmortizationIndex]?.value ?: ""
                },
                onValueChanged = {},
                onOptionSelected = { index, value ->
                    onAction(NewLoanAccountAction.OnNominalAmortizationIndexChange(index))
                },
                options = state.loanTemplate?.amortizationTypeOptions?.map { it.value ?: "" } ?: emptyList(),
                label = stringResource(Res.string.amortization),
            )

            Row(
                Modifier.fillMaxWidth()
                    .clickable {
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
                    text = stringResource(Res.string.is_equal_amortization),
                    style = MifosTypography.labelLarge,
                )
            }

            Spacer(Modifier.height(DesignToken.padding.large))

            Text(
                text = stringResource(Res.string.loan_schedule),
                style = MifosTypography.labelLargeEmphasized,
            )

            Spacer(Modifier.height(DesignToken.padding.large))

            MifosListingComponentOutline {
                MifosListingRowItem(
                    key = stringResource(Res.string.loan_schedule_type),
                    keyStyle = MifosTypography.labelMediumEmphasized,
                    value = state.loanTemplate?.loanScheduleType?.value ?: "",
                )
            }

            Spacer(Modifier.height(DesignToken.padding.large))

            MifosTextFieldDropdown(
                value = if (state.repaymentStrategyIndex == -1) {
                    ""
                } else {
                    state.loanTemplate?.transactionProcessingStrategyOptions[state.repaymentStrategyIndex]?.name ?: ""
                },
                onValueChanged = {},
                onOptionSelected = { index, value ->
                    onAction(NewLoanAccountAction.OnRepaymentStrategyIndexChange(index))
                },
                options = state.loanTemplate?.transactionProcessingStrategyOptions?.map { it.name ?: "" } ?: emptyList(),
                label = stringResource(Res.string.repayment_strategy),
            )

            MifosOutlinedTextField(
                value = state.balloonRepaymentAmount.toString(),
                onValueChange = {
                    onAction(NewLoanAccountAction.OnBalloonRepaymentAmountChange(it.toIntOrNull() ?: 0))
                },
                label = stringResource(Res.string.balloon_repayment_amount),
                config = MifosTextFieldConfig(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                    ),
                ),
            )

            Spacer(Modifier.height(DesignToken.padding.large))

            Text(
                text = stringResource(Res.string.interest_calculations),
                style = MifosTypography.labelLargeEmphasized,
            )

            Spacer(Modifier.height(DesignToken.padding.large))

            MifosTextFieldDropdown(
                value = if (state.interestCalculationPeriodIndex == -1) {
                    ""
                } else {
                    state.loanTemplate?.interestCalculationPeriodTypeOptions[state.interestCalculationPeriodIndex]?.value ?: ""
                },
                onValueChanged = {},
                onOptionSelected = { index, value ->
                    onAction(NewLoanAccountAction.OnInterestCalculationPeriodIndexChange(index))
                },
                options = state.loanTemplate?.interestCalculationPeriodTypeOptions?.map { it.value ?: "" } ?: emptyList(),
                label = stringResource(Res.string.interest_calculation_period),
            )

            Row(
                Modifier.fillMaxWidth()
                    .clickable {
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
                    text = stringResource(Res.string.calculate_interest_partial),
                    style = MifosTypography.labelLarge,
                )
            }

            MifosOutlinedTextField(
                value = state.arrearsTolerance.toString(),
                onValueChange = {
                    onAction(NewLoanAccountAction.OnArrearsToleranceChange(it.toIntOrNull() ?: 0))
                },
                label = stringResource(Res.string.arrears_tolerance),
                config = MifosTextFieldConfig(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                    ),
                ),
            )

            Spacer(Modifier.height(DesignToken.padding.large))

            MifosOutlinedTextField(
                value = state.interestFreePeriod.toString(),
                onValueChange = {
                    onAction(NewLoanAccountAction.OnInterestFreePeriodChange(it.toIntOrNull() ?: 0))
                },
                label = stringResource(Res.string.interest_free_period),
                config = MifosTextFieldConfig(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                    ),
                ),
            )

            Spacer(Modifier.height(DesignToken.padding.large))

            Text(
                text = stringResource(Res.string.moratorium),
                style = MifosTypography.labelLargeEmphasized,
            )

            Spacer(Modifier.height(DesignToken.padding.large))

            MifosOutlinedTextField(
                value = state.moratoriumGraceOnPrincipalPayment.toString(),
                onValueChange = {
                    onAction(NewLoanAccountAction.OnMoratoriumGraceOnPrincipalPaymentChange(it.toIntOrNull() ?: 0))
                },
                label = stringResource(Res.string.grace_on_principal_payment),
                config = MifosTextFieldConfig(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                    ),
                ),
            )

            Spacer(Modifier.height(DesignToken.padding.large))

            MifosOutlinedTextField(
                value = state.moratoriumGraceOnInterestPayment.toString(),
                onValueChange = {
                    onAction(NewLoanAccountAction.OnMoratoriumGraceOnInterestPaymentChange(it.toIntOrNull() ?: 0))
                },
                label = stringResource(Res.string.grace_on_interest_payment),
                config = MifosTextFieldConfig(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                    ),
                ),
            )

            Spacer(Modifier.height(DesignToken.padding.large))

            MifosOutlinedTextField(
                value = state.moratoriumOnArrearsAgeing.toString(),
                onValueChange = {
                    onAction(NewLoanAccountAction.OnMoratoriumOnArrearsAgeingChange(it.toIntOrNull() ?: 0))
                },
                label = stringResource(Res.string.on_arrears_ageing),
                config = MifosTextFieldConfig(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                    ),
                ),
            )

            Spacer(Modifier.height(DesignToken.padding.large))

            MifosListingComponentOutline {
                MifosListingRowItem(
                    key = stringResource(Res.string.recalculate_interest),
                    keyStyle = MifosTypography.labelMediumEmphasized,
                    value = if (state.loanTemplate?.isInterestRecalculationEnabled ?: false) {
                        stringResource(Res.string.yes)
                    } else {
                        stringResource(Res.string.no)
                    },
                )
            }

            Spacer(Modifier.height(DesignToken.padding.large))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(Res.string.collateral_data),
                    style = MifosTypography.labelLargeEmphasized,
                )
                Row(
                    Modifier.clickable {
                        onAction(NewLoanAccountAction.ShowAddCollateralDialog)
                    },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = MifosIcons.Add,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(DesignToken.sizes.iconSmall),
                    )

                    Text(
                        text = stringResource(Res.string.add_new),
                        color = MaterialTheme.colorScheme.primary,
                        style = MifosTypography.labelLargeEmphasized,
                    )
                }
            }

            Spacer(Modifier.height(DesignToken.padding.large))

            MifosRowWithTextAndButton(
                onBtnClick = {
                    onAction(NewLoanAccountAction.ShowCollaterals)
                },
                btnText = stringResource(Res.string.view),
                text = stringResource(Res.string.collateral_count, state.addedCollaterals.size),
                btnEnabled = state.addedCollaterals.isNotEmpty(),
            )
        }
        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.back),
            secondBtnText = stringResource(Res.string.next),
            onFirstBtnClick = {
                onAction(NewLoanAccountAction.PreviousStep)
            },
            onSecondBtnClick = {
                onAction(NewLoanAccountAction.NextStep)
            },
            isSecondButtonEnabled = state.isDetailsNextEnabled,
            modifier = Modifier.padding(top = DesignToken.padding.small),
        )
    }
}
