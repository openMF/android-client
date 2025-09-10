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
import androidclient.feature.loan.generated.resources.fund
import androidclient.feature.loan.generated.resources.link_savings
import androidclient.feature.loan.generated.resources.loan_officer
import androidclient.feature.loan.generated.resources.loan_purpose
import androidclient.feature.loan.generated.resources.next
import androidclient.feature.loan.generated.resources.product_name
import androidclient.feature.loan.generated.resources.savings_linkage
import androidclient.feature.loan.generated.resources.step_details
import androidclient.feature.loan.generated.resources.submission_date
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun DetailsPage(
    state: NewLoanAccountState,
    onAction: (NewLoanAccountAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val submissionDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
    )

    val expectedDisbursementDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= Clock.System.now().toEpochMilliseconds()
            }
        },
    )

    if (state.showSubmissionDatePick) {
        DatePickerDialog(
            onDismissRequest = {
                onAction(NewLoanAccountAction.OnSubmissionDatePick(false))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAction(NewLoanAccountAction.OnSubmissionDatePick(false))
                        submissionDatePickerState.selectedDateMillis?.let {
                            onAction(NewLoanAccountAction.OnSubmissionDateChange(DateHelper.getDateAsStringFromLong(it)))
                        }
                    },
                ) { Text(stringResource(Res.string.feature_loan_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onAction(NewLoanAccountAction.OnSubmissionDatePick(false))
                    },
                ) { Text(stringResource(Res.string.feature_loan_cancel)) }
            },
        ) {
            DatePicker(state = submissionDatePickerState)
        }
    }

    if (state.showExpectedDisbursementDatePick) {
        DatePickerDialog(
            onDismissRequest = {
                onAction(NewLoanAccountAction.OnExpectedDisbursementDatePick(false))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAction(NewLoanAccountAction.OnExpectedDisbursementDatePick(false))
                        expectedDisbursementDatePickerState.selectedDateMillis?.let {
                            onAction(NewLoanAccountAction.OnExpectedDisbursementDateChange(DateHelper.getDateAsStringFromLong(it)))
                        }
                    },
                ) { Text(stringResource(Res.string.feature_loan_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onAction(NewLoanAccountAction.OnExpectedDisbursementDatePick(false))
                    },
                ) { Text(stringResource(Res.string.feature_loan_cancel)) }
            },
        ) {
            DatePicker(state = expectedDisbursementDatePickerState)
        }
    }

    Column(Modifier.fillMaxSize()) {
        Column(
            modifier = modifier.weight(1f).verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = stringResource(Res.string.step_details),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(DesignToken.padding.large))

            MifosTextFieldDropdown(
                value = if (state.loanProductSelected == -1) {
                    ""
                } else {
                    state.productLoans[state.loanProductSelected].name ?: ""
                },
                onValueChanged = {},
                onOptionSelected = { index, value ->
                    onAction(NewLoanAccountAction.OnProductNameChange(index))
                },
                options = state.productLoans.map {
                    it.name ?: ""
                },
                label = stringResource(Res.string.product_name),
            )

            if (state.loanTemplate != null) {
                MifosOutlinedTextField(
                    value = state.externalId,
                    onValueChange = {
                        onAction(NewLoanAccountAction.OnExternalIdChange(it))
                    },
                    label = stringResource(Res.string.external_id),
                    config = MifosTextFieldConfig(
                        isError = state.externalIdError != null,
                        errorText = if (state.externalIdError != null)stringResource(state.externalIdError) else null,
                    ),
                )
                Spacer(Modifier.height(DesignToken.padding.large))
                MifosTextFieldDropdown(
                    value = if (state.loanOfficerIndex == -1) {
                        ""
                    } else {
                        state.loanTemplate.loanOfficerOptions[state.loanOfficerIndex].displayName.toString()
                    },
                    onValueChanged = {},
                    onOptionSelected = { index, value ->
                        onAction(NewLoanAccountAction.OnLoanOfficerChange(index))
                    },
                    options = state.loanTemplate.loanOfficerOptions.map { it.displayName.toString() },
                    label = stringResource(Res.string.loan_officer),
                )

                if (state.loanTemplate.loanPurposeOptions.isNotEmpty()) {
                    MifosTextFieldDropdown(
                        value = if (state.loanPurposeIndex == -1) {
                            ""
                        } else {
                            state.loanTemplate.loanPurposeOptions[state.loanPurposeIndex].name.toString()
                        },
                        onValueChanged = {},
                        onOptionSelected = { index, value ->
                            onAction(NewLoanAccountAction.OnLoanPurposeChange(index))
                        },
                        options = state.loanTemplate.loanPurposeOptions.map { it.name.toString() },
                        label = stringResource(Res.string.loan_purpose),
                    )
                }

                if (state.loanTemplate.fundOptions.isNotEmpty()) {
                    MifosTextFieldDropdown(
                        value = if (state.fundIndex == -1) {
                            ""
                        } else {
                            state.loanTemplate.fundOptions[state.fundIndex].name.toString()
                        },
                        onValueChanged = {},
                        onOptionSelected = { index, value ->
                            onAction(NewLoanAccountAction.OnFundChange(index))
                        },
                        options = state.loanTemplate.fundOptions.map { it.name.toString() },
                        label = stringResource(Res.string.fund),
                    )
                }

                MifosDatePickerTextField(
                    value = state.submissionDate,
                    label = stringResource(Res.string.submission_date),
                    openDatePicker = {
                        onAction(NewLoanAccountAction.OnSubmissionDatePick(true))
                    },
                )
                Spacer(Modifier.height(DesignToken.padding.large))
                MifosDatePickerTextField(
                    value = state.expectedDisbursementDate,
                    label = stringResource(Res.string.expected_disbursement),
                    openDatePicker = {
                        onAction(NewLoanAccountAction.OnExpectedDisbursementDatePick(true))
                    },
                )
                Spacer(Modifier.height(DesignToken.padding.large))
                Text(
                    text = stringResource(Res.string.savings_linkage),
                    style = MifosTypography.labelLargeEmphasized,
                )
                Spacer(Modifier.height(DesignToken.padding.large))
                MifosTextFieldDropdown(
                    value = if (state.linkSavingsIndex == -1) {
                        ""
                    } else {
                        state.loanTemplate.accountLinkingOptions[state.linkSavingsIndex].productName.toString()
                    },
                    onValueChanged = {},
                    onOptionSelected = { index, value ->
                        onAction(NewLoanAccountAction.OnLinkSavingsChange(index))
                    },
                    options = state.loanTemplate.accountLinkingOptions.map { it.productName.toString() },
                    label = stringResource(Res.string.link_savings),
                )
                Spacer(Modifier.height(DesignToken.padding.medium))
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = state.isCheckedStandingInstructions,
                        onCheckedChange = {
                            onAction(NewLoanAccountAction.OnStandingInstructionsChange(it))
                        },
                    )
                    Text(
                        text = stringResource(Res.string.create_standing_instructions),
                        style = MifosTypography.labelLarge,
                    )
                }
                Spacer(Modifier.height(DesignToken.padding.large))
            }
        }
        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.back),
            secondBtnText = stringResource(Res.string.next),
            onFirstBtnClick = {
                onAction(NewLoanAccountAction.NavigateBack)
            },
            onSecondBtnClick = {
                onAction(NewLoanAccountAction.OnDetailsSubmit)
            },
            isSecondButtonEnabled = state.isDetailsNextEnabled,
            modifier = Modifier.padding(top = DesignToken.padding.small),
        )
    }
}
