package com.mifos.core.data.repository_imp

import com.mifos.core.data.repository.CreateNewGroupRepository
import com.mifos.core.network.datamanager.DataManagerGroups
import com.mifos.core.network.datamanager.DataManagerOffices
import com.mifos.core.objects.group.GroupPayload
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.response.SaveResponse
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