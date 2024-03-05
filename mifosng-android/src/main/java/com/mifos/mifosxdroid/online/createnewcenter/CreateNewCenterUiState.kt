package com.mifos.mifosxdroid.online.createnewcenter

import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.response.SaveResponse

/**
 * Created by Aditya Gupta on 10/08/23.
 */
sealed class CreateNewCenterUiState {

    data object ShowProgressbar : CreateNewCenterUiState()

    data class ShowFetchingError(val message: Int) : CreateNewCenterUiState()

    data class ShowOffices(val offices: List<Office>) : CreateNewCenterUiState()

    data class ShowFetchingErrorString(val message: String) : CreateNewCenterUiState()

    data class CenterCreatedSuccessfully(val saveResponse: SaveResponse) : CreateNewCenterUiState()
}
