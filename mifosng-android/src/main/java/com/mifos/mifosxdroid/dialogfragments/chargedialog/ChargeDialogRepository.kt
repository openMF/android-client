package com.mifos.mifosxdroid.dialogfragments.chargedialog

import com.mifos.core.data.ChargesPayload
import com.mifos.core.objects.client.ChargeCreationResponse
import com.mifos.core.objects.templates.clients.ChargeTemplate
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