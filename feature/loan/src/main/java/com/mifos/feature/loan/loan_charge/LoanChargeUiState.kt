package com.mifos.feature.loan.loan_charge

import com.mifos.core.objects.client.Charges

/**
 * Created by Aditya Gupta on 10/08/23.
 */
sealed class LoanChargeUiState {

    data object Loading : LoanChargeUiState()

    data class Error(val message: Int) : LoanChargeUiState()

    data class LoanChargesList(val loanCharges: List<Charges>) : LoanChargeUiState()
}