/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import com.mifos.core.`object`.clients.ChargeCreationResponse
import com.mifos.core.payloads.ChargesPayload
import okhttp3.ResponseBody

/**
 * Created by Aditya Gupta on 16/08/23.
 */
interface LoanChargeDialogRepository {

    suspend fun getAllChargesV3(loanId: Int): ResponseBody

    suspend fun createLoanCharges(
        loanId: Int,
        chargesPayload: ChargesPayload,
    ): ChargeCreationResponse
}
