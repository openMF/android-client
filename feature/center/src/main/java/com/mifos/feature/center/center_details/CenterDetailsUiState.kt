package com.mifos.feature.center.center_details

import com.mifos.core.objects.group.CenterInfo
import com.mifos.core.objects.group.CenterWithAssociations

/**
 * Created by Aditya Gupta on 06/08/23.
 */
sealed class CenterDetailsUiState {

    data object Loading : CenterDetailsUiState()

    data class Error(val message: Int) : CenterDetailsUiState()

    data class CenterDetails(
        val centerWithAssociations: CenterWithAssociations,
        val centerInfo: CenterInfo
    ) : CenterDetailsUiState()
}