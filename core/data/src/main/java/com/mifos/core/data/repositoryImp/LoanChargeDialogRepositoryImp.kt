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

import com.mifos.core.data.ChargesPayload
import com.mifos.core.data.repository.LoanChargeDialogRepository
import com.mifos.core.network.DataManager
import com.mifos.core.objects.client.ChargeCreationResponse
import okhttp3.ResponseBody
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 16/08/23.
 */
class LoanChargeDialogRepositoryImp @Inject constructor(private val dataManager: DataManager) :
    LoanChargeDialogRepository {

    override suspend fun getAllChargesV3(loanId: Int): ResponseBody {
        return dataManager.getAllChargesV3(loanId)
    }

    override suspend fun createLoanCharges(
        loanId: Int,
        chargesPayload: ChargesPayload,
    ): ChargeCreationResponse {
        return dataManager.createLoanCharges(loanId, chargesPayload)
    }
}
