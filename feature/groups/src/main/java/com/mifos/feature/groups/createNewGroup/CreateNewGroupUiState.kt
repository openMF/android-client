/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.groups.createNewGroup

import com.mifos.core.model.objects.responses.SaveResponse
import com.mifos.room.entities.organisation.OfficeEntity

/**
 * Created by Aditya Gupta on 10/08/23.
 */
sealed class CreateNewGroupUiState {

    data object ShowProgressbar : CreateNewGroupUiState()

    data class ShowFetchingError(val message: String) : CreateNewGroupUiState()

    data class ShowOffices(val offices: List<OfficeEntity>) : CreateNewGroupUiState()

    data class ShowGroupCreatedSuccessfully(val saveResponse: SaveResponse) :
        CreateNewGroupUiState()
}
