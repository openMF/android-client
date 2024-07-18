package com.mifos.mifosxdroid.online.centerlist

import com.mifos.core.network.datamanager.DataManagerCenter
import com.mifos.core.objects.client.Page
import com.mifos.core.objects.group.Center
import com.mifos.core.objects.group.CenterWithAssociations
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */
class CenterListRepositoryImp @Inject constructor(private val dataManagerCenter: DataManagerCenter) :
    CenterListRepository {

    override fun getCenters(paged: Boolean, offset: Int, limit: Int): Observable<Page<Center>> {
        return dataManagerCenter.getCenters(paged, offset, limit)
    }

    override suspend fun getCentersGroupAndMeeting(id: Int): CenterWithAssociations {
        return dataManagerCenter.getCentersGroupAndMeeting(id)
    }

    override fun allDatabaseCenters(): Observable<Page<Center>> {
        return dataManagerCenter.allDatabaseCenters
    }


}