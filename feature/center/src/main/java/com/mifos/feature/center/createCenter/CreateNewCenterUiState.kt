/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.center.createCenter

import com.mifos.room.entities.organisation.OfficeEntity

/**
 * Created by Aditya Gupta on 10/08/23.
 */
sealed class CreateNewCenterUiState {

    data object Loading : CreateNewCenterUiState()

    data class Error(val message: Int) : CreateNewCenterUiState()

    data class Offices(val offices: List<OfficeEntity>) : CreateNewCenterUiState()

    data object CenterCreatedSuccessfully : CreateNewCenterUiState()
}
