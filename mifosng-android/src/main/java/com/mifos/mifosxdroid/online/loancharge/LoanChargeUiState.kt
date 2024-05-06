package com.mifos.mifosxdroid.online.loancharge

import com.mifos.core.objects.client.Charges

/**
 * Created by Aditya Gupta on 10/08/23.
 */
sealed class LoanChargeUiState {

    data object ShowProgressbar : LoanChargeUiState()

    data class ShowFetchingError(val message: String) : LoanChargeUiState()

    data class ShowLoanChargesList(val chargesPage: MutableList<Charges>) : LoanChargeUiState()


}