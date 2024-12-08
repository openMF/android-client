package com.mifos.core.data.repository

import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.client.ActivatePayload
import org.openapitools.client.models.PostCentersCenterIdResponse
import org.openapitools.client.models.PostClientsClientIdResponse
import rx.Observable

/**
 * Created by Aditya Gupta on 06/08/23.
 */

interface ActivateRepository {

    suspend fun activateClient(
        clientId: Int,
        clientActivate: ActivatePayload?
    ): PostClientsClientIdResponse

    suspend fun activateCenter(
        centerId: Int,
        activatePayload: ActivatePayload?
    ): PostCentersCenterIdResponse

    fun activateGroup(
        groupId: Int,
        activatePayload: ActivatePayload?
    ): Observable<GenericResponse>

}