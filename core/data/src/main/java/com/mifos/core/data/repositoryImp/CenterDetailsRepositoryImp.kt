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

import com.mifos.core.data.repository.CenterDetailsRepository
import com.mifos.core.entity.group.CenterWithAssociations
import com.mifos.core.network.datamanager.DataManagerCenter
import com.mifos.core.network.datamanager.DataManagerRunReport
import com.mifos.core.objects.groups.CenterInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */
class CenterDetailsRepositoryImp @Inject constructor(
    private val dataManagerCenter: DataManagerCenter,
    private val dataManagerRunReport: DataManagerRunReport,
) : CenterDetailsRepository {

    override suspend fun getCentersGroupAndMeeting(id: Int): Flow<CenterWithAssociations> {
        return flow { emit(dataManagerCenter.getCentersGroupAndMeeting(id)) }
    }

    override suspend fun getCenterSummaryInfo(
        centerId: Int,
        genericResultSet: Boolean,
    ): Flow<List<CenterInfo>> {
        return flow { emit(dataManagerRunReport.getCenterSummaryInfo(centerId, genericResultSet)) }
    }
}
