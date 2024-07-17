package com.mifos.feature.loan.group_loan_account

import com.mifos.core.objects.templates.loans.GroupLoanTemplate

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class GroupLoanAccountUiState {

    data object Loading : GroupLoanAccountUiState()

    data class Error(val message: Int) : GroupLoanAccountUiState()

    data object GroupLoanAccountCreatedSuccessfully : GroupLoanAccountUiState()

    data class GroupLoanAccountTemplate(val groupLoanTemplate: GroupLoanTemplate) :
        GroupLoanAccountUiState()
}