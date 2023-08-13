package com.mifos.repositories

import com.mifos.objects.client.ChargeCreationResponse
import com.mifos.objects.templates.clients.ChargeTemplate
import com.mifos.services.data.ChargesPayload
import rx.Observable

interface ChargeDialogRepository {

    fun getAllChargesV2(clientId: Int): Observable<ChargeTemplate>

    fun createCharges(
        clientId: Int,
        payload: ChargesPayload?
    ): Observable<ChargeCreationResponse>
}