package com.mifos.repositories

import com.mifos.api.GenericResponse
import com.mifos.api.datamanager.DataManagerCenter
import com.mifos.api.datamanager.DataManagerClient
import com.mifos.api.datamanager.DataManagerGroups
import com.mifos.objects.client.ActivatePayload
import org.apache.fineract.client.models.PostClientsClientIdResponse
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */
class ActivateRepositoryImp @Inject constructor(
    private val dataManagerClient: DataManagerClient,
    private val dataManagerCenter: DataManagerCenter,
    private val dataManagerGroups: DataManagerGroups
) : ActivateRepository {

    override fun activateClient(
        clientId: Int,
        clientActivate: ActivatePayload?
    ): Observable<PostClientsClientIdResponse> {
        return dataManagerClient.activateClient(clientId, clientActivate)
    }

    override fun activateCenter(
        centerId: Int,
        activatePayload: ActivatePayload?
    ): Observable<GenericResponse> {
        return dataManagerCenter.activateCenter(centerId, activatePayload)
    }

    override fun activateGroup(
        groupId: Int,
        activatePayload: ActivatePayload?
    ): Observable<GenericResponse> {
        return dataManagerGroups.activateGroup(groupId, activatePayload)
    }
}