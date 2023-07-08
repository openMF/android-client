package com.mifos.mifosxdroid.dialogfragments.datatablerowdialog

import com.mifos.api.GenericResponse
import com.mifos.mifosxdroid.base.MvpView

/**
 * Created by Rajan Maurya on 08/06/16.
 */
interface DataTableRowDialogMvpView : MvpView {
    fun showDataTableEntrySuccessfully(genericResponse: GenericResponse)
    fun showError(message: String)
}