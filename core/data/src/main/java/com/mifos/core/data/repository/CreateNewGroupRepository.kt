package com.mifos.core.data.repository

import com.mifos.core.datastore.PrefManager
import com.mifos.core.objects.group.GroupPayload
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.response.SaveResponse
import rx.Observable

/**
 * Created by Aditya Gupta on 10/08/23.
 */
interface CreateNewGroupRepository {

    fun offices(): Observable<List<Office>>

    fun createGroup(groupPayload: GroupPayload): Observable<SaveResponse>
}