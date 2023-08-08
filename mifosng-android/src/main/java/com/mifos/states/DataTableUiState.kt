package com.mifos.states

import com.mifos.objects.noncore.DataTable

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class DataTableUiState {

    object ShowProgressbar : DataTableUiState()

    object ShowResetVisibility : DataTableUiState()

    data class ShowError(val message: Int) : DataTableUiState()

    data class ShowDataTables(val dataTables: List<DataTable>) : DataTableUiState()

    object ShowEmptyDataTables : DataTableUiState()
}
