package com.mifos.repositories

import com.mifos.objects.group.GroupPayload
import com.mifos.objects.organisation.Office
import com.mifos.objects.response.SaveResponse
import rx.Observable

/**
 * Created by Aditya Gupta on 10/08/23.
 */
interface CreateNewGroupRepository {

    fun offices(): Observable<List<Office>>

    fun createGroup(groupPayload: GroupPayload): Observable<SaveResponse>
}