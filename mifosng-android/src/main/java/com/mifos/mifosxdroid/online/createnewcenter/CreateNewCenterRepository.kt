package com.mifos.mifosxdroid.online.createnewcenter

import com.mifos.objects.organisation.Office
import com.mifos.objects.response.SaveResponse
import com.mifos.services.data.CenterPayload
import rx.Observable

/**
 * Created by Aditya Gupta on 10/08/23.
 */
interface CreateNewCenterRepository {

    fun offices(): Observable<List<Office>>

    fun createCenter(centerPayload: CenterPayload): Observable<SaveResponse>
}