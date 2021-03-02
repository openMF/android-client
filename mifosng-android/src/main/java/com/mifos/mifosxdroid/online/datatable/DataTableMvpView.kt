package com.mifos.mifosxdroid.online.datatable

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.noncore.DataTable

/**
 * Created by Rajan Maurya on 12/02/17.
 */
interface DataTableMvpView : MvpView {
    fun showUserInterface()
    fun showDataTables(dataTables: List<DataTable>?)
    fun showEmptyDataTables()
    fun showResetVisibility()
    fun showError(message: Int)
}