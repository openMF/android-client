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

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.FixedDepositRepository
import com.mifos.core.network.datamanager.DataManagerFixedDeposit
import com.mifos.core.network.model.fixedDeposit.FixedDepositPayload
import com.mifos.core.network.model.fixedDeposit.FixedDepositTemplate
import kotlinx.coroutines.flow.Flow

class FixedDepositRepositoryImpl(private val dataManagerFixedDeposit: DataManagerFixedDeposit) :
    FixedDepositRepository {

    override fun getFixedDepositTemplate(
        clientId: Int,
        productId: Int?,
    ): Flow<DataState<FixedDepositTemplate>> {
        return dataManagerFixedDeposit.getFixedDepositTemplate(clientId, productId)
            .asDataStateFlow()
    }

    override fun createFixedDepositAccount(fixedDepositPayload: FixedDepositPayload): Flow<DataState<Unit>> {
        return dataManagerFixedDeposit.createFixedDepositAccount(fixedDepositPayload).asDataStateFlow()
    }
}
