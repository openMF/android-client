package com.mifos.mifosxdroid.dialogfragments.chargedialog

import com.mifos.api.DataManager
import com.mifos.objects.client.ChargeCreationResponse
import com.mifos.objects.templates.clients.ChargeTemplate
import com.mifos.services.data.ChargesPayload
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 13/08/23.
 */
class ChargeDialogRepositoryImp @Inject constructor(private val dataManager: DataManager) :
    ChargeDialogRepository {

    override fun getAllChargesV2(clientId: Int): Observable<ChargeTemplate> {
        return dataManager.getAllChargesV2(clientId)
    }

    override fun createCharges(
        clientId: Int,
        payload: ChargesPayload?
    ): Observable<ChargeCreationResponse> {
        return dataManager.createCharges(clientId, payload)
    }
}