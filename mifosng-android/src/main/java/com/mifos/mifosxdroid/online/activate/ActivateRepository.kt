package com.mifos.mifosxdroid.online.activate

import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.client.ActivatePayload
import org.apache.fineract.client.models.PostCentersCenterIdResponse
import org.apache.fineract.client.models.PostClientsClientIdResponse
import rx.Observable

/**
 * Created by Aditya Gupta on 06/08/23.
 */

interface ActivateRepository {

    fun activateClient(
        clientId: Int,
        clientActivate: ActivatePayload?
    ): Observable<PostClientsClientIdResponse>

    fun activateCenter(
        centerId: Int,
        activatePayload: ActivatePayload?
    ): Observable<PostCentersCenterIdResponse>

    fun activateGroup(
        groupId: Int,
        activatePayload: ActivatePayload?
    ): Observable<GenericResponse>

}