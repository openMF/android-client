package com.mifos.feature.client.createNewClient

import com.mifos.core.objects.templates.clients.ClientsTemplate

/**
 * Created by Aditya Gupta on 10/08/23.
 */
sealed class CreateNewClientUiState {

    data object ShowProgressbar : CreateNewClientUiState()

    data class ShowProgress(val message: String) : CreateNewClientUiState()

    data class ShowError(val message: Int) : CreateNewClientUiState()

    data class ShowStringError(val message: String) : CreateNewClientUiState()

    data class OnImageUploadSuccess(val message: Int) : CreateNewClientUiState()
    
    data class ShowClientTemplate(val clientsTemplate: ClientsTemplate) : CreateNewClientUiState()

    data class ShowClientCreatedSuccessfully(val message: Int) : CreateNewClientUiState()

    data class SetClientId(val id: Int) : CreateNewClientUiState()

    data class ShowWaitingForCheckerApproval(val message: Int) : CreateNewClientUiState()

}