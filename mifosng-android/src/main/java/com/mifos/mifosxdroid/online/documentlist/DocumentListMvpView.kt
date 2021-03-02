package com.mifos.mifosxdroid.online.documentlist

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.noncore.Document
import okhttp3.ResponseBody

/**
 * Created by Rajan Maurya on 06/06/16.
 */
interface DocumentListMvpView : MvpView {
    fun showDocumentList(documents: List<Document>)
    fun showDocumentFetchSuccessfully(responseBody: ResponseBody?)
    fun checkPermissionAndRequest()
    fun requestPermission()
    fun showDocumentActions(documentId: Int)
    fun checkExternalStorageAndCreateDocument()
    fun showDocumentRemovedSuccessfully()
    fun showDocumentDialog(documentAction: String?)
    fun showEmptyDocuments()
    fun showFetchingError(errorMessage: Int)
}