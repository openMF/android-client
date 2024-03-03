package com.mifos.mifosxdroid.online.centerdetails

import com.mifos.api.datamanager.DataManagerCenter
import com.mifos.api.datamanager.DataManagerRunReport
import com.mifos.objects.group.CenterInfo
import com.mifos.objects.group.CenterWithAssociations
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */
class CenterDetailsRepositoryImp @Inject constructor(
    private val dataManagerCenter: DataManagerCenter,
    private val dataManagerRunReport: DataManagerRunReport
) : CenterDetailsRepository {

    override fun getCentersGroupAndMeeting(id: Int): Observable<CenterWithAssociations> {
        return dataManagerCenter.getCentersGroupAndMeeting(id)
    }

    override fun getCenterSummaryInfo(
        centerId: Int,
        genericResultSet: Boolean
    ): Observable<List<CenterInfo>> {
        return dataManagerRunReport.getCenterSummaryInfo(centerId, genericResultSet)
    }
}