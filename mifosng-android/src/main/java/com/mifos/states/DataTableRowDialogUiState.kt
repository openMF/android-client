package com.mifos.states

import com.mifos.api.GenericResponse

/**
 * Created by Aditya Gupta on 13/08/23.
 */
sealed class DataTableRowDialogUiState {

    object ShowProgressbar : DataTableRowDialogUiState()

    data class ShowError(val message: String) : DataTableRowDialogUiState()

    data class ShowDataTableEntrySuccessfully(val genericResponse: GenericResponse) :
        DataTableRowDialogUiState()
}
