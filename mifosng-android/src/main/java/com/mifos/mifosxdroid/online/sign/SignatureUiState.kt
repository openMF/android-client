package com.mifos.mifosxdroid.online.sign

import com.mifos.core.network.GenericResponse

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class SignatureUiState {

    data object ShowProgressbar : SignatureUiState()

    data class ShowError(val message: Int) : SignatureUiState()

    data class ShowSignatureUploadedSuccessfully(val genericResponse: GenericResponse?) :
        SignatureUiState()

}
