package com.mifos.core.data.repository_imp

import com.mifos.core.data.repository.CenterDetailsRepository
import com.mifos.core.network.datamanager.DataManagerCenter
import com.mifos.core.network.datamanager.DataManagerRunReport
import com.mifos.core.objects.group.CenterInfo
import com.mifos.core.objects.group.CenterWithAssociations
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */
class CenterDetailsRepositoryImp @Inject constructor(
    private val dataManagerCenter: DataManagerCenter,
    private val dataManagerRunReport: DataManagerRunReport
) : CenterDetailsRepository {

    override suspend fun getCentersGroupAndMeeting(id: Int): Flow<CenterWithAssociations> {
        return flow { emit(dataManagerCenter.getCentersGroupAndMeeting(id)) }
    }

    override suspend fun getCenterSummaryInfo(
        centerId: Int,
        genericResultSet: Boolean
    ): Flow<List<CenterInfo>> {
        return flow { emit(dataManagerRunReport.getCenterSummaryInfo(centerId, genericResultSet)) }
    }
}