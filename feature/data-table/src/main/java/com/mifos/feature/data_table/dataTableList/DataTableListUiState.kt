package com.mifos.feature.data_table.dataTableList

import com.mifos.core.objects.client.Client

/**
 * Created by Aditya Gupta on 10/08/23.
 */
sealed class DataTableListUiState {
    data object Loading: DataTableListUiState()
    data class ShowMessage(val messageResId: Int? = null, val message: String? = null): DataTableListUiState()
    data class Success(val messageResId: Int? = null, val client: Client? = null): DataTableListUiState()
}