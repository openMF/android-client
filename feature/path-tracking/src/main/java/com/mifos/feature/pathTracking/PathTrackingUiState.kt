/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.pathTracking

import com.mifos.core.modelobjects.users.UserLocation

/**
 * Created by Aditya Gupta on 06/08/23.
 */

sealed class PathTrackingUiState {

    data object Loading : PathTrackingUiState()

    data class Error(val message: Int) : PathTrackingUiState()

    data class PathTracking(val userLocations: List<UserLocation>) : PathTrackingUiState()
}
