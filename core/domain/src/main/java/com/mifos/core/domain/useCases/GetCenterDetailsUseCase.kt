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

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.CenterDetailsRepository
import com.mifos.core.dbobjects.group.CenterWithAssociations
import com.mifos.core.objects.groups.CenterInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.zip
import javax.inject.Inject

class GetCenterDetailsUseCase @Inject constructor(private val repository: CenterDetailsRepository) {

    suspend operator fun invoke(
        centerId: Int,
        genericResultSet: Boolean,
    ): Flow<Resource<Pair<CenterWithAssociations, List<CenterInfo>>>> = flow {
        try {
            emit(Resource.Loading())
            repository.getCentersGroupAndMeeting(centerId)
                .zip(
                    repository.getCenterSummaryInfo(
                        centerId,
                        genericResultSet,
                    ),
                ) { centerGroup, centerInfo ->
                    Pair(centerGroup, centerInfo)
                }.collect {
                    emit(Resource.Success(it))
                }
        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }
}
