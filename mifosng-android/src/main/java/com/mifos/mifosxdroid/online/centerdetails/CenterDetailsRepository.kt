package com.mifos.mifosxdroid.online.centerdetails

import com.mifos.core.objects.group.CenterInfo
import com.mifos.core.objects.group.CenterWithAssociations
import rx.Observable

/**
 * Created by Aditya Gupta on 06/08/23.
 */
interface CenterDetailsRepository {

    fun getCentersGroupAndMeeting(id: Int): Observable<CenterWithAssociations>

    fun getCenterSummaryInfo(centerId: Int, genericResultSet: Boolean): Observable<List<CenterInfo>>
}