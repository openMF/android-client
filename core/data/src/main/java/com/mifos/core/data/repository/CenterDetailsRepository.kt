package com.mifos.core.data.repository

import com.mifos.core.objects.group.CenterInfo
import com.mifos.core.objects.group.CenterWithAssociations
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 06/08/23.
 */
interface CenterDetailsRepository {

    suspend fun getCentersGroupAndMeeting(id: Int): Flow<CenterWithAssociations>

    suspend fun getCenterSummaryInfo(
        centerId: Int,
        genericResultSet: Boolean
    ): Flow<List<CenterInfo>>
}