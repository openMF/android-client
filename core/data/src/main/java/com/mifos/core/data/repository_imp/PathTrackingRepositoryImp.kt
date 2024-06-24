package com.mifos.core.data.repository_imp

import com.mifos.core.data.repository.PathTrackingRepository
import com.mifos.core.network.datamanager.DataManagerDataTable
import com.mifos.core.objects.user.UserLocation
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */
class PathTrackingRepositoryImp @Inject constructor(private val dataManagerDataTable: DataManagerDataTable) :
    PathTrackingRepository {

    override suspend fun getUserPathTracking(userId: Int): List<UserLocation> {
        return dataManagerDataTable.getUserPathTracking(userId)
    }

}