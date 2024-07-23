package com.mifos.feature.client.clientSignature

import com.mifos.core.network.GenericResponse

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class SignatureUiState {

    data object Initial : SignatureUiState()

    data object Loading : SignatureUiState()

    data class Error(val message: Int) : SignatureUiState()

    data object SignatureUploadedSuccessfully : SignatureUiState()
}
