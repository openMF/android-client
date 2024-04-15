package com.mifos.feature.client.clientDetails.ui

import okhttp3.ResponseBody

/**
 * Created by Aditya Gupta on 06/08/23.
 */
sealed class ClientDetailsUiState {

    data class ShowUploadImageSuccessfully(val response: ResponseBody?, val imagePath: String?) :
        ClientDetailsUiState()

    data object ShowClientImageDeletedSuccessfully : ClientDetailsUiState()

    data object Empty : ClientDetailsUiState()

    data class ShowError(val message: String) : ClientDetailsUiState()
}