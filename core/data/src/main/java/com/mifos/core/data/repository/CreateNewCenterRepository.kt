package com.mifos.core.data.repository

import com.mifos.core.data.CenterPayload
import com.mifos.core.objects.response.SaveResponse
import rx.Observable

/**
 * Created by Aditya Gupta on 10/08/23.
 */
interface CreateNewCenterRepository {
    fun createCenter(centerPayload: CenterPayload): Observable<SaveResponse>
}