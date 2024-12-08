package com.mifos.core.data.repository_imp

import com.mifos.core.data.repository.ActivateRepository
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerCenter
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.network.datamanager.DataManagerGroups
import com.mifos.core.objects.client.ActivatePayload
import org.openapitools.client.models.PostCentersCenterIdResponse
import org.openapitools.client.models.PostClientsClientIdResponse
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

    override suspend fun activateClient(
        clientId: Int,
        clientActivate: ActivatePayload?
    ): PostClientsClientIdResponse {
        return dataManagerClient.activateClient(clientId, clientActivate)
    }

    override suspend fun activateCenter(
        centerId: Int,
        activatePayload: ActivatePayload?
    ): PostCentersCenterIdResponse {
        return dataManagerCenter.activateCenter(centerId, activatePayload)
    }

    override fun activateGroup(
        groupId: Int,
        activatePayload: ActivatePayload?
    ): Observable<GenericResponse> {
        return dataManagerGroups.activateGroup(groupId, activatePayload)
    }
}