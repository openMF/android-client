package com.mifos.feature.client.clientIdentifiersDialog

import com.mifos.core.objects.noncore.IdentifierTemplate

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class ClientIdentifierDialogUiState {

    data object Loading : ClientIdentifierDialogUiState()

    data class Error(val message: Int) : ClientIdentifierDialogUiState()

    data class ClientIdentifierTemplate(val identifierTemplate: IdentifierTemplate) :
        ClientIdentifierDialogUiState()

    data object IdentifierCreatedSuccessfully : ClientIdentifierDialogUiState()
}