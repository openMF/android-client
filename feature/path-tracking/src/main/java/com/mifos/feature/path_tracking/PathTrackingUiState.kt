package com.mifos.feature.path_tracking

import com.mifos.core.objects.user.UserLocation

/**
 * Created by Aditya Gupta on 06/08/23.
 */

sealed class PathTrackingUiState {

    data object Loading : PathTrackingUiState()

    data class Error(val message: Int) : PathTrackingUiState()

    data class PathTracking(val userLocations: List<UserLocation>) : PathTrackingUiState()

}