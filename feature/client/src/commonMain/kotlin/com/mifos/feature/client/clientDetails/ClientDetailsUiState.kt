package com.mifos.feature.client.clientDetails

/**
 * Created by Aditya Gupta on 06/08/23.
 */
sealed class ClientDetailsUiState {

    data class ShowUploadImageSuccessfully(val response: String, val imagePath: String?) :
        ClientDetailsUiState()

    data object ShowClientImageDeletedSuccessfully : ClientDetailsUiState()

    data object Empty : ClientDetailsUiState()

    data class ShowError(val message: String) : ClientDetailsUiState()
}