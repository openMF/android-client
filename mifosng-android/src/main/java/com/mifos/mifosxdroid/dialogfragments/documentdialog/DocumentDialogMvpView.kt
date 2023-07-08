package com.mifos.mifosxdroid.dialogfragments.documentdialog

import com.mifos.api.GenericResponse
import com.mifos.mifosxdroid.base.MvpView

/**
 * Created by Rajan Maurya on 8/6/16.
 */
interface DocumentDialogMvpView : MvpView {
    fun checkPermissionAndRequest()
    fun requestPermission()
    fun getExternalStorageDocument()
    fun showDocumentedCreatedSuccessfully(genericResponse: GenericResponse)
    fun showDocumentUpdatedSuccessfully()
    fun showError(errorMessage: Int)
    fun showUploadError(errorMessage: String)
}