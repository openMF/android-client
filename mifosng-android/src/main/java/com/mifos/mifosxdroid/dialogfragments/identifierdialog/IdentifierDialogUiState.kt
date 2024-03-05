package com.mifos.mifosxdroid.dialogfragments.identifierdialog

import com.mifos.core.objects.noncore.IdentifierCreationResponse
import com.mifos.core.objects.noncore.IdentifierTemplate

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class IdentifierDialogUiState {

    object ShowProgressbar : IdentifierDialogUiState()

    data class ShowError(val message: String) : IdentifierDialogUiState()

    data class ShowClientIdentifierTemplate(val identifierTemplate: IdentifierTemplate) :
        IdentifierDialogUiState()

    data class ShowIdentifierCreatedSuccessfully(val identifierCreationResponse: IdentifierCreationResponse) :
        IdentifierDialogUiState()
}