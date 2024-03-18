package com.mifos.mifosxdroid.online.centerdetails

import com.mifos.core.objects.group.CenterInfo
import com.mifos.core.objects.group.CenterWithAssociations

/**
 * Created by Aditya Gupta on 06/08/23.
 */
sealed class CenterDetailsUiState {

    data class ShowProgressbar(val state: Boolean) : CenterDetailsUiState()

    data class ShowErrorMessage(val message: Int) : CenterDetailsUiState()

    data class ShowMeetingDetails(val centerWithAssociations: CenterWithAssociations) :
        CenterDetailsUiState()

    data class ShowCenterDetails(val centerWithAssociations: CenterWithAssociations) :
        CenterDetailsUiState()

    data class ShowSummaryInfo(val centerInfo: List<CenterInfo>) : CenterDetailsUiState()
}