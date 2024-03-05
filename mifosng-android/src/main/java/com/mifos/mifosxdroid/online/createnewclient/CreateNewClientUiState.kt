package com.mifos.mifosxdroid.online.createnewclient

import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.organisation.Staff
import com.mifos.core.objects.templates.clients.ClientsTemplate

/**
 * Created by Aditya Gupta on 10/08/23.
 */
sealed class CreateNewClientUiState {

    object ShowProgressbar : CreateNewClientUiState()

    data class ShowMessage(val message: Int) : CreateNewClientUiState()

    data class ShowClientTemplate(val clientsTemplate: ClientsTemplate?) : CreateNewClientUiState()

    data class ShowOffices(val officeList: List<Office>) : CreateNewClientUiState()

    data class ShowStaffInOffices(val staffList: List<Staff>) : CreateNewClientUiState()

    data class ShowMessageString(val message: String) : CreateNewClientUiState()

    data class ShowClientCreatedSuccessfully(val message: Int) : CreateNewClientUiState()

    data class SetClientId(val id: Int) : CreateNewClientUiState()

    data class ShowWaitingForCheckerApproval(val message: Int) : CreateNewClientUiState()

    data class ShowProgress(val message: String) : CreateNewClientUiState()
}