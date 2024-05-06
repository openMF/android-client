package com.mifos.mifosxdroid.online.clientdetails

import com.mifos.core.objects.accounts.ClientAccounts
import com.mifos.core.objects.client.Client
import okhttp3.ResponseBody

/**
 * Created by Aditya Gupta on 06/08/23.
 */
sealed class ClientDetailsUiState {

    data class ShowUploadImageProgressbar(val state: Boolean) : ClientDetailsUiState()

    data class ShowUploadImageFailed(val message: String) : ClientDetailsUiState()

    data class ShowUploadImageSuccessfully(val response: ResponseBody?, val imagePath: String?) :
        ClientDetailsUiState()

    data class ShowFetchingError(val message: String) : ClientDetailsUiState()

    object ShowClientImageDeletedSuccessfully : ClientDetailsUiState()

    data class ShowProgressbar(val boolean: Boolean) : ClientDetailsUiState()

    data class ShowClientAccount(val clientAccounts: ClientAccounts) : ClientDetailsUiState()

    data class ShowClientInformation(val client: Client) : ClientDetailsUiState()
}