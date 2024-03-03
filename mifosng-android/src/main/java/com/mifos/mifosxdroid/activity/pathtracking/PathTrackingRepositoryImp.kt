package com.mifos.mifosxdroid.activity.pathtracking

import com.mifos.api.datamanager.DataManagerDataTable
import com.mifos.objects.user.UserLocation
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */
class PathTrackingRepositoryImp @Inject constructor(private val dataManagerDataTable: DataManagerDataTable) :
    PathTrackingRepository {

    override fun getUserPathTracking(userId: Int): Observable<List<UserLocation>> {
        return dataManagerDataTable.getUserPathTracking(userId)
    }

}