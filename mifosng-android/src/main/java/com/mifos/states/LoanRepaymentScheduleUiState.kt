package com.mifos.states

import com.mifos.objects.accounts.loan.LoanWithAssociations

/**
 * Created by Aditya Gupta on 12/08/23.
 */
sealed class LoanRepaymentScheduleUiState {

    object ShowProgressbar : LoanRepaymentScheduleUiState()

    data class ShowFetchingError(val message: String) : LoanRepaymentScheduleUiState()

    data class ShowLoanRepaySchedule(val loanWithAssociations: LoanWithAssociations) :
        LoanRepaymentScheduleUiState()
}
