/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.datamanager

import com.mifos.core.common.utils.extractErrorMessage
import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.model.fixedDeposit.FixedDepositPayload
import com.mifos.core.network.model.fixedDeposit.FixedDepositTemplate
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataManagerFixedDeposit(private val baseApiManager: BaseApiManager) {

    fun getFixedDepositTemplate(clientId: Int, productId: Int?): Flow<FixedDepositTemplate> =
        baseApiManager.fixedDepositService.fixedDepositProductTemplate(clientId, productId)

    fun createFixedDepositAccount(fixedDepositPayload: FixedDepositPayload): Flow<Unit> =
        baseApiManager.fixedDepositService.createFixedDepositAccount(fixedDepositPayload).map { response ->
            if (!response.status.isSuccess()) {
                val error = extractErrorMessage(response)

                throw IllegalStateException(error)
            }
        }
}
