package com.mifos.mifosxdroid.online.centerlist

import com.mifos.core.objects.group.Center
import com.mifos.core.objects.group.CenterWithAssociations

/**
 * Created by Aditya Gupta on 06/08/23.
 */
sealed class CenterListUiState {

    data class ShowProgressbar(val state: Boolean) : CenterListUiState()

    data class ShowMessage(val message: Int) : CenterListUiState()

    object ShowFetchingError : CenterListUiState()

    object UnregisterSwipeAndScrollListener : CenterListUiState()

    data class ShowEmptyCenters(val message: Int) : CenterListUiState()

    data class ShowMoreCenters(val centers: List<Center>) : CenterListUiState()

    data class ShowCenters(val centers: List<Center>) : CenterListUiState()

    data class ShowCentersGroupAndMeeting(
        val centerWithAssociations: CenterWithAssociations?,
        val id: Int
    ) : CenterListUiState()

}