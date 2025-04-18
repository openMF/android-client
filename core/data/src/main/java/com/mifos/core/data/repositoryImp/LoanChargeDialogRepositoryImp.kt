/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.data.repository.LoanChargeDialogRepository
import com.mifos.core.model.objects.clients.ChargeCreationResponse
import com.mifos.core.model.objects.payloads.ChargesPayload
import com.mifos.core.network.DataManager
import io.ktor.client.statement.HttpResponse

/**
 * Created by Aditya Gupta on 16/08/23.
 */
class LoanChargeDialogRepositoryImp(
    private val dataManager: DataManager,
) : LoanChargeDialogRepository {

    override suspend fun getAllChargesV3(loanId: Int): HttpResponse {
        return dataManager.getAllChargesV3(loanId)
    }

    override suspend fun createLoanCharges(
        loanId: Int,
        chargesPayload: ChargesPayload,
    ): ChargeCreationResponse {
        return dataManager.createLoanCharges(loanId, chargesPayload)
    }
}
