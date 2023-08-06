package com.mifos.repositories

import com.mifos.api.GenericResponse
import com.mifos.objects.client.ActivatePayload
import rx.Observable

/**
 * Created by Aditya Gupta on 06/08/23.
 */

interface ActivateRepository {

    fun activateClient(
        clientId: Int,
        clientActivate: ActivatePayload?
    ): Observable<GenericResponse>

    fun activateCenter(
        centerId: Int,
        activatePayload: ActivatePayload?
    ): Observable<GenericResponse>

    fun activateGroup(
        groupId: Int,
        activatePayload: ActivatePayload?
    ): Observable<GenericResponse>

}