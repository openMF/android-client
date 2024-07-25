package com.mifos.core.data.repository

import com.mifos.core.data.ChargesPayload
import com.mifos.core.objects.client.ChargeCreationResponse
import com.mifos.core.objects.templates.clients.ChargeTemplate

/**
 * Created by Aditya Gupta on 13/08/23.
 */
interface ChargeDialogRepository {

    suspend fun getAllChargesV2(clientId: Int): ChargeTemplate

    suspend fun createCharges(
        clientId: Int,
        payload: ChargesPayload
    ): ChargeCreationResponse
}