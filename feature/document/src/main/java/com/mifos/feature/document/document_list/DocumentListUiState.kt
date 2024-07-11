package com.mifos.feature.document.document_list

import com.mifos.core.objects.noncore.Document

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class DocumentListUiState {

    data object Loading : DocumentListUiState()

    data class Error(val message: Int) : DocumentListUiState()

    data class DocumentList(val documents: List<Document>) : DocumentListUiState()
}
