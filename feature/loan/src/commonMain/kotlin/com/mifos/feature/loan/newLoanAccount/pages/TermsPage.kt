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
import androidclient.feature.loan.generated.resources.expected_disbursement
import androidclient.feature.loan.generated.resources.external_id
import androidclient.feature.loan.generated.resources.feature_loan_cancel
import androidclient.feature.loan.generated.resources.feature_loan_select
import androidclient.feature.loan.generated.resources.loan_officer
import androidclient.feature.loan.generated.resources.next
import androidclient.feature.loan.generated.resources.step_details
import androidclient.feature.loan.generated.resources.step_terms
import androidclient.feature.loan.generated.resources.submission_date
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
                value = state.noOfRepayments.toString(),
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
