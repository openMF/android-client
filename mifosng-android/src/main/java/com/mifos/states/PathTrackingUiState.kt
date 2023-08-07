package com.mifos.states

import com.mifos.objects.user.UserLocation

/**
 * Created by Aditya Gupta on 06/08/23.
 */
sealed class PathTrackingUiState {

    data class ShowProgress(val state: Boolean) : PathTrackingUiState()

    data class ShowPathTracking(val userLocations: List<UserLocation>) : PathTrackingUiState()

    object ShowEmptyPathTracking : PathTrackingUiState()

    object ShowError : PathTrackingUiState()

}