/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanTransaction

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_cancel
import androidclient.feature.loan.generated.resources.feature_loan_export_transactions
import androidclient.feature.loan.generated.resources.feature_loan_from_date
import androidclient.feature.loan.generated.resources.feature_loan_generate_report
import androidclient.feature.loan.generated.resources.feature_loan_invalid_date_range
import androidclient.feature.loan.generated.resources.feature_loan_select
import androidclient.feature.loan.generated.resources.feature_loan_to_date
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosButton
import com.mifos.core.designsystem.component.MifosCustomDialog
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedButton
import org.jetbrains.compose.resources.stringResource
import template.core.base.designsystem.theme.KptTheme
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
internal fun ExportTransactionsDialog(
    state: ExportDialogState,
    onAction: (LoanTransactionsAction) -> Unit,
) {
    val fromDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.fromDate ?: Clock.System.now().toEpochMilliseconds(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= Clock.System.now().toEpochMilliseconds()
            }
        },
    )

    val toDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.toDate ?: Clock.System.now().toEpochMilliseconds(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= Clock.System.now().toEpochMilliseconds()
            }
        },
    )

    if (state.showFromDatePicker) {
        DatePickerDialog(
            onDismissRequest = { onAction(LoanTransactionsAction.ShowFromDatePicker(false)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAction(LoanTransactionsAction.ShowFromDatePicker(false))
                        fromDatePickerState.selectedDateMillis?.let {
                            onAction(LoanTransactionsAction.FromDateSelected(it))
                        }
                    },
                ) { Text(stringResource(Res.string.feature_loan_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = { onAction(LoanTransactionsAction.ShowFromDatePicker(false)) },
                ) { Text(stringResource(Res.string.feature_loan_cancel)) }
            },
        ) {
            DatePicker(state = fromDatePickerState)
        }
    }

    if (state.showToDatePicker) {
        DatePickerDialog(
            onDismissRequest = { onAction(LoanTransactionsAction.ShowToDatePicker(false)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAction(LoanTransactionsAction.ShowToDatePicker(false))
                        toDatePickerState.selectedDateMillis?.let {
                            onAction(LoanTransactionsAction.ToDateSelected(it))
                        }
                    },
                ) { Text(stringResource(Res.string.feature_loan_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = { onAction(LoanTransactionsAction.ShowToDatePicker(false)) },
                ) { Text(stringResource(Res.string.feature_loan_cancel)) }
            },
        ) {
            DatePicker(state = toDatePickerState)
        }
    }

    MifosCustomDialog(
        onDismiss = { onAction(LoanTransactionsAction.DismissExportDialog) },
    ) {
        Surface(
            shape = KptTheme.shapes.medium,
            color = KptTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth(0.95f),
        ) {
            Column(modifier = Modifier.padding(KptTheme.spacing.lg)) {
                Text(
                    text = stringResource(Res.string.feature_loan_export_transactions),
                    style = KptTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(KptTheme.spacing.md))

                MifosDatePickerTextField(
                    value = state.fromDate?.let {
                        DateHelper.getDateAsStringFromLong(it)
                    }.orEmpty(),
                    label = stringResource(Res.string.feature_loan_from_date),
                    openDatePicker = {
                        onAction(LoanTransactionsAction.ShowFromDatePicker(true))
                    },
                )

                Spacer(modifier = Modifier.height(KptTheme.spacing.sm))

                MifosDatePickerTextField(
                    value = state.toDate?.let {
                        DateHelper.getDateAsStringFromLong(it)
                    }.orEmpty(),
                    label = stringResource(Res.string.feature_loan_to_date),
                    errorMessage = if (state.isInvalidDateRange) {
                        stringResource(Res.string.feature_loan_invalid_date_range)
                    } else {
                        null
                    },
                    openDatePicker = {
                        onAction(LoanTransactionsAction.ShowToDatePicker(true))
                    },
                )

                Spacer(modifier = Modifier.height(KptTheme.spacing.md))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    MifosOutlinedButton(
                        onClick = { onAction(LoanTransactionsAction.DismissExportDialog) },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = stringResource(Res.string.feature_loan_cancel),
                            style = KptTheme.typography.labelLarge,
                            maxLines = 1,
                        )
                    }

                    Spacer(modifier = Modifier.width(KptTheme.spacing.md))

                    MifosButton(
                        onClick = {
                            onAction(LoanTransactionsAction.GenerateReportClicked)
                        },
                        enabled = state.isValidDateRange,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = stringResource(Res.string.feature_loan_generate_report),
                            style = KptTheme.typography.labelLarge,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}
