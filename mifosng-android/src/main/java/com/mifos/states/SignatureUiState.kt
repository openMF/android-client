package com.mifos.states

import com.mifos.api.GenericResponse

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class SignatureUiState {

    object ShowProgressbar : SignatureUiState()

    data class ShowError(val message: Int) : SignatureUiState()

    data class ShowSignatureUploadedSuccessfully(val genericResponse: GenericResponse?) :
        SignatureUiState()

}
