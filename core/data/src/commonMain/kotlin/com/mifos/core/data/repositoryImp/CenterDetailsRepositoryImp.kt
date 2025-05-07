/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.CenterDetailsRepository
import com.mifos.core.model.objects.groups.CenterInfo
import com.mifos.core.network.datamanager.DataManagerCenter
import com.mifos.core.network.datamanager.DataManagerRunReport
import com.mifos.room.entities.group.CenterWithAssociations
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 06/08/23.
 */
class CenterDetailsRepositoryImp(
    private val dataManagerCenter: DataManagerCenter,
    private val dataManagerRunReport: DataManagerRunReport,
) : CenterDetailsRepository {

    override suspend fun getCentersGroupAndMeeting(id: Int): CenterWithAssociations {
        return dataManagerCenter.getCentersGroupAndMeeting(id)
    }

    override fun getCenterSummaryInfo(
        centerId: Int,
        genericResultSet: Boolean,
    ): Flow<DataState<List<CenterInfo>>> {
        return dataManagerRunReport.getCenterSummaryInfo(centerId, genericResultSet)
            .asDataStateFlow()
    }
}
