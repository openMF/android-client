package com.mifos.core.data.repository_imp

import com.mifos.core.data.ChargesPayload
import com.mifos.core.data.repository.ChargeDialogRepository
import com.mifos.core.network.DataManager
import com.mifos.core.objects.client.ChargeCreationResponse
import com.mifos.core.objects.templates.clients.ChargeTemplate
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 13/08/23.
 */
class ChargeDialogRepositoryImp @Inject constructor(private val dataManager: DataManager) :
    ChargeDialogRepository {

    override suspend fun getAllChargesV2(clientId: Int): ChargeTemplate {
        return dataManager.getAllChargesV2(clientId)
    }

    override suspend fun createCharges(
        clientId: Int,
        payload: ChargesPayload
    ): ChargeCreationResponse {
        return dataManager.createCharges(clientId, payload)
    }
}