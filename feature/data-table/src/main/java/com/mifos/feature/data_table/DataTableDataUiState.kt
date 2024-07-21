package com.mifos.feature.data_table

import com.google.gson.JsonArray

/**
 * Created by Aditya Gupta on 10/08/23.
 */
sealed class DataTableDataUiState {

    data object Loading : DataTableDataUiState()

    data class Error(val message: Int) : DataTableDataUiState()

    data class DataTableInfo(val jsonElements: JsonArray) : DataTableDataUiState()

    data object DataTableDeletedSuccessfully : DataTableDataUiState()
}