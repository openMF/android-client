package com.mifos.feature.data_table.dataTableRowDialog

/**
 * Created by Aditya Gupta on 13/08/23.
 */
sealed class DataTableRowDialogUiState {

    data object Initial : DataTableRowDialogUiState()

    data object Loading : DataTableRowDialogUiState()

    data class Error(val message: Int) : DataTableRowDialogUiState()

    data object DataTableEntrySuccessfully : DataTableRowDialogUiState()
}
