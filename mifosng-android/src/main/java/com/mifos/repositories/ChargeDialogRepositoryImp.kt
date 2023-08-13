package com.mifos.repositories

import com.mifos.api.DataManager
import com.mifos.objects.client.ChargeCreationResponse
import com.mifos.objects.templates.clients.ChargeTemplate
import com.mifos.services.data.ChargesPayload
import rx.Observable
import javax.inject.Inject

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