package com.mifos.feature.center.create_center

import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.response.SaveResponse

/**
 * Created by Aditya Gupta on 10/08/23.
 */
sealed class CreateNewCenterUiState {

    data object Loading : CreateNewCenterUiState()

    data class Error(val message: Int) : CreateNewCenterUiState()

    data class Offices(val offices: List<Office>) : CreateNewCenterUiState()

    data object CenterCreatedSuccessfully : CreateNewCenterUiState()
}
