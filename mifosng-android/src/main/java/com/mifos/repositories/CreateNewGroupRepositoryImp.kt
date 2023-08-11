package com.mifos.repositories

import com.mifos.api.datamanager.DataManagerGroups
import com.mifos.api.datamanager.DataManagerOffices
import com.mifos.objects.group.GroupPayload
import com.mifos.objects.organisation.Office
import com.mifos.objects.response.SaveResponse
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class CreateNewGroupRepositoryImp @Inject constructor(
    private val dataManagerOffices: DataManagerOffices,
    private val dataManagerGroups: DataManagerGroups
) : CreateNewGroupRepository {

    override fun offices(): Observable<List<Office>> {
        return dataManagerOffices.offices
    }

    override fun createGroup(groupPayload: GroupPayload): Observable<SaveResponse> {
        return dataManagerGroups.createGroup(groupPayload)
    }
}