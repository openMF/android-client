package com.mifos.feature.groups.create_new_group

import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.response.SaveResponse

/**
 * Created by Aditya Gupta on 10/08/23.
 */
sealed class CreateNewGroupUiState {

    data object ShowProgressbar : CreateNewGroupUiState()

    data class ShowFetchingError(val message: String) : CreateNewGroupUiState()

    data class ShowOffices(val offices: List<Office>) : CreateNewGroupUiState()

    data class ShowGroupCreatedSuccessfully(val saveResponse: SaveResponse) :
        CreateNewGroupUiState()
}