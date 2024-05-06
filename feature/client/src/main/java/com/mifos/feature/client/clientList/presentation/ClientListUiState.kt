package com.mifos.feature.client.clientList.presentation

import androidx.paging.PagingData
import com.mifos.core.objects.client.Client
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class ClientListUiState {

    data object Empty : ClientListUiState()

    data class Error(val message: String) : ClientListUiState()

    data class ClientListApi(val list: Flow<PagingData<Client>>) : ClientListUiState()

    data class ClientListDb(val list: List<Client>) : ClientListUiState()
}