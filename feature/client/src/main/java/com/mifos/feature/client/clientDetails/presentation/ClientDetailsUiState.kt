package com.mifos.feature.client.clientDetails.presentation

import com.mifos.core.objects.accounts.ClientAccounts
import com.mifos.core.objects.client.Client
import okhttp3.ResponseBody

/**
 * Created by Aditya Gupta on 06/08/23.
 */
sealed class ClientDetailsUiState {

    data class ShowUploadImageSuccessfully(val response: ResponseBody?, val imagePath: String?) : ClientDetailsUiState()

    data object ShowClientImageDeletedSuccessfully : ClientDetailsUiState()

    data object Loading : ClientDetailsUiState()

    data class ShowClientDetails(val client: Client?,val clientAccounts: ClientAccounts?) : ClientDetailsUiState()

    data class ShowError(val message: String) : ClientDetailsUiState()
}