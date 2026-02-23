/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanRepaymentSchedule

import com.mifos.core.model.objects.account.loan.RepaymentScheduleTableData

sealed class LoanRepaymentScheduleUiState {

    data object ShowProgressbar : LoanRepaymentScheduleUiState()

    data class ShowFetchingError(val message: String) : LoanRepaymentScheduleUiState()

    data class ShowLoanRepaymentSchedule(
        val tableData: RepaymentScheduleTableData,
    ) : LoanRepaymentScheduleUiState()
}

/**
 * Pre-formatted table data for the repayment schedule display and PDF export.
 * All values are pre-formatted as strings for direct use in UI and HTML generation.
 */
data class RepaymentScheduleTableData(
    val accountNo: String,
    val clientName: String,
    val productName: String,
    val disbursementDate: String,
    val loanAmount: String,
    val principalPaid: String,
    val installmentsPaid: String,
    val totalInstallments: String,
    val currencyCode: String?,
    val periods: List<PeriodData>,
    val totals: TotalsData,
) {
    data class PeriodData(
        val number: String,
        val days: String,
        val dueDate: String,
        val paidDate: String,
        val isPaid: Boolean,
        val balanceOfLoan: String,
        val principalDue: String,
        val interest: String,
        val fees: String,
        val penalties: String,
        val due: String,
        val paid: String,
        val inAdvance: String,
        val late: String,
        val outstanding: String,
    )

    data class TotalsData(
        val principalDue: String,
        val interest: String,
        val fees: String,
        val penalties: String,
        val due: String,
        val paid: String,
        val inAdvance: String,
        val late: String,
        val outstanding: String,
    )
}
