package com.mifos.mifosxdroid.online.savingaccountsummary

import com.mifos.core.objects.accounts.savings.SavingsAccountWithAssociations

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class SavingsAccountSummaryUiState {

    object ShowProgressbar : SavingsAccountSummaryUiState()

    data class ShowFetchingError(val message: Int) : SavingsAccountSummaryUiState()

    data class ShowSavingAccount(val savingsAccountWithAssociations: SavingsAccountWithAssociations) :
        SavingsAccountSummaryUiState()
}
