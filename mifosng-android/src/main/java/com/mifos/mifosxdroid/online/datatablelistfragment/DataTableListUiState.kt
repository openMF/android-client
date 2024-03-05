package com.mifos.mifosxdroid.online.datatablelistfragment

import com.mifos.core.objects.client.Client

/**
 * Created by Aditya Gupta on 10/08/23.
 */
sealed class DataTableListUiState {

    object ShowProgressBar : DataTableListUiState()

    data class ShowMessage(val message: Int) : DataTableListUiState()

    data class ShowMessageString(val message: String) : DataTableListUiState()

    data class ShowClientCreatedSuccessfully(val client: Client) : DataTableListUiState()

    data class ShowWaitingForCheckerApproval(val message: Int) : DataTableListUiState()
}