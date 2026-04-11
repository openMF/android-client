/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.data.repository.LoanChargeFormRepository
import com.mifos.core.model.objects.clients.ChargeCreationResponse
import com.mifos.core.model.objects.payloads.ChargesPayload
import com.mifos.core.model.objects.template.client.ChargeTemplate
import com.mifos.core.network.datamanager.DataManagerCharge

/**
 * Created by Aditya Gupta on 16/08/23.
 */
class LoanChargeFormRepositoryImp(
    private val dataManager: DataManagerCharge,
) : LoanChargeFormRepository {

    override suspend fun getChargeTemplate(resourceType: String, resourceId: Int): ChargeTemplate {
        return dataManager.getChargeTemplate(resourceType, resourceId)
    }

    override suspend fun createCharges(
        resourceType: String,
        resourceId: Int,
        chargesPayload: ChargesPayload,
    ): ChargeCreationResponse {
        return dataManager.createCharges(resourceType, resourceId, chargesPayload)
    }
}
