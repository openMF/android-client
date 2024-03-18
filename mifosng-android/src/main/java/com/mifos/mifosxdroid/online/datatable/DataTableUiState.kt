package com.mifos.mifosxdroid.online.datatable

import com.mifos.core.objects.noncore.DataTable

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class DataTableUiState {

    data object ShowProgressbar : DataTableUiState()

    data object ShowResetVisibility : DataTableUiState()

    data class ShowError(val message: Int) : DataTableUiState()

    data class ShowDataTables(val dataTables: List<DataTable>) : DataTableUiState()

    data object ShowEmptyDataTables : DataTableUiState()
}
