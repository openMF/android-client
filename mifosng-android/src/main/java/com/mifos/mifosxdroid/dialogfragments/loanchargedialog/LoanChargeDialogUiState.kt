package com.mifos.mifosxdroid.dialogfragments.loanchargedialog

import com.mifos.core.objects.client.ChargeCreationResponse
import okhttp3.ResponseBody

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class LoanChargeDialogUiState {

    object ShowProgressbar : LoanChargeDialogUiState()

    data class ShowError(val message: String) : LoanChargeDialogUiState()

    data class ShowAllChargesV3(val response: ResponseBody) : LoanChargeDialogUiState()

    data class ShowLoanChargesCreatedSuccessfully(val chargeCreationResponse: ChargeCreationResponse) :
        LoanChargeDialogUiState()
}
