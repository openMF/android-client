/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.domain.useCases

import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.CenterDetailsRepository
import com.mifos.core.model.objects.groups.CenterInfo
import com.mifos.room.entities.group.CenterWithAssociations
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

class GetCenterDetailsUseCase(
    private val repository: CenterDetailsRepository,
) {

    operator fun invoke(
        centerId: Int,
        genericResultSet: Boolean,
    ): Flow<DataState<Pair<CenterWithAssociations, List<CenterInfo>>>> =
        combine(
            flow { emit(repository.getCentersGroupAndMeeting(centerId)) },
            repository.getCenterSummaryInfo(centerId, genericResultSet),
        ) { centerGroup, centerInfoState ->

            when (centerInfoState) {
                is DataState.Error -> DataState.Error(centerInfoState.exception)
                DataState.Loading -> DataState.Loading
                is DataState.Success -> DataState.Success(Pair(centerGroup, centerInfoState.data))
            }
        }
}
