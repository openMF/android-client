package com.mifos.mifosxdroid.online.datatabledata

import com.google.gson.JsonArray

/**
 * Created by Aditya Gupta on 10/08/23.
 */
sealed class DataTableDataUiState {

    object ShowProgressbar : DataTableDataUiState()

    data class ShowFetchingError(val message: Int) : DataTableDataUiState()

    object ShowEmptyDataTable : DataTableDataUiState()

    data class ShowDataTableInfo(val jsonElements: JsonArray) : DataTableDataUiState()

    data class ShowFetchingErrorString(val message: String) : DataTableDataUiState()

    object ShowDataTableDeletedSuccessfully : DataTableDataUiState()
}
