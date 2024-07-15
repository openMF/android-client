package com.mifos.feature.loan.loan_charge_dialog

import com.mifos.core.objects.client.Charges

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class LoanChargeDialogUiState {

    data object Loading : LoanChargeDialogUiState()

    data class Error(val message: Int) : LoanChargeDialogUiState()

    data class AllChargesV3(val list: List<Charges>) : LoanChargeDialogUiState()

    data object LoanChargesCreatedSuccessfully :
        LoanChargeDialogUiState()
}
