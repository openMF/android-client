package com.mifos.feature.loan.loan_repayment_schedule

import com.mifos.core.objects.accounts.loan.LoanWithAssociations

/**
 * Created by Aditya Gupta on 12/08/23.
 */
sealed class LoanRepaymentScheduleUiState {

    data object ShowProgressbar : LoanRepaymentScheduleUiState()

    data class ShowFetchingError(val message: String) : LoanRepaymentScheduleUiState()

    data class ShowLoanRepaySchedule(val loanWithAssociations: LoanWithAssociations) :
        LoanRepaymentScheduleUiState()
}
