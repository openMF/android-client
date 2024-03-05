package com.mifos.mifosxdroid.online.centerlist

import com.mifos.core.objects.client.Page
import com.mifos.core.objects.group.Center
import com.mifos.core.objects.group.CenterWithAssociations
import rx.Observable

/**
 * Created by Aditya Gupta on 06/08/23.
 */
interface CenterListRepository {

    fun getCenters(paged: Boolean, offset: Int, limit: Int): Observable<Page<Center>>

    fun getCentersGroupAndMeeting(id: Int): Observable<CenterWithAssociations>

    fun allDatabaseCenters(): Observable<Page<Center>>
}