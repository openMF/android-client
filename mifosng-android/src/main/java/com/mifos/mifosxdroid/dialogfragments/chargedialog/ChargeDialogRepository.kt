package com.mifos.mifosxdroid.dialogfragments.chargedialog

import com.mifos.objects.client.ChargeCreationResponse
import com.mifos.objects.templates.clients.ChargeTemplate
import com.mifos.services.data.ChargesPayload
import rx.Observable

/**
 * Created by Aditya Gupta on 13/08/23.
 */
interface ChargeDialogRepository {

    fun getAllChargesV2(clientId: Int): Observable<ChargeTemplate>

    fun createCharges(
        clientId: Int,
        payload: ChargesPayload?
    ): Observable<ChargeCreationResponse>
}