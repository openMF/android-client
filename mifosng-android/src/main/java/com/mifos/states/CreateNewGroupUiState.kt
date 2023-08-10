package com.mifos.states

import com.mifos.objects.organisation.Office
import com.mifos.objects.response.SaveResponse

/**
 * Created by Aditya Gupta on 10/08/23.
 */
sealed class CreateNewGroupUiState {

    object ShowProgressbar : CreateNewGroupUiState()

    data class ShowFetchingError(val message: String) : CreateNewGroupUiState()

    data class ShowOffices(val offices: List<Office>) : CreateNewGroupUiState()

    data class ShowGroupCreatedSuccessfully(val saveResponse: SaveResponse) :
        CreateNewGroupUiState()
}