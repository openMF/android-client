package com.mifos.mifosxdroid.online.createnewcenter

import com.mifos.core.data.CenterPayload
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.response.SaveResponse
import rx.Observable

/**
 * Created by Aditya Gupta on 10/08/23.
 */
interface CreateNewCenterRepository {

    fun offices(): Observable<List<Office>>

    fun createCenter(centerPayload: CenterPayload): Observable<SaveResponse>
}