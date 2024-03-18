package com.mifos.mifosxdroid.dialogfragments.documentdialog

import com.mifos.core.network.GenericResponse

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class DocumentDialogUiState {

    object ShowProgressbar : DocumentDialogUiState()

    data class ShowUploadError(val message: String) : DocumentDialogUiState()

    data class ShowDocumentedCreatedSuccessfully(val genericResponse: GenericResponse) :
        DocumentDialogUiState()


    data class ShowError(val message: String) : DocumentDialogUiState()

    data class ShowDocumentUpdatedSuccessfully(val genericResponse: GenericResponse) :
        DocumentDialogUiState()
}