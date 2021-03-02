package com.mifos.mifosxdroid.online.datatabledata

import com.google.gson.JsonArray
import com.mifos.mifosxdroid.base.MvpView

/**
 * Created by Rajan Maurya on 7/6/16.
 */
interface DataTableDataMvpView : MvpView {
    fun showDataTableInfo(jsonElements: JsonArray?)
    fun showDataTableDeletedSuccessfully()
    fun showEmptyDataTable()
    fun showFetchingError(message: Int)
    fun showFetchingError(s: String?)
}