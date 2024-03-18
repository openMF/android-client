package com.mifos.mifosxdroid.online.documentlist

import com.mifos.core.objects.noncore.Document
import okhttp3.ResponseBody

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class DocumentListUiState {

    object ShowProgressbar : DocumentListUiState()

    data class ShowFetchingError(val message: Int) : DocumentListUiState()

    data class ShowDocumentList(val documents: List<Document>) : DocumentListUiState()

    data class ShowDocumentFetchSuccessfully(val responseBody: ResponseBody?) :
        DocumentListUiState()

    object ShowDocumentRemovedSuccessfully : DocumentListUiState()
}
