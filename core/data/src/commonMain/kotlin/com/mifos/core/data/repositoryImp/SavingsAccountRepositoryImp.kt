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

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.SavingsAccountRepository
import com.mifos.core.model.objects.organisations.ProductSavings
import com.mifos.core.model.objects.payloads.SavingsPayload
import com.mifos.core.network.datamanager.DataManagerSavings
import com.mifos.room.entities.client.Savings
import com.mifos.room.entities.templates.savings.SavingProductsTemplate
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class SavingsAccountRepositoryImp(
    private val dataManagerSavings: DataManagerSavings,
) : SavingsAccountRepository {
    override fun getSavingsAccounts(): Flow<DataState<List<ProductSavings>>> {
        return dataManagerSavings.getSavingsAccounts
            .asDataStateFlow()
    }

    override fun getSavingsAccountTemplate(): Flow<DataState<SavingProductsTemplate>> {
        return dataManagerSavings.getSavingsAccountTemplate
            .asDataStateFlow()
    }

    override fun getClientSavingsAccountTemplateByProduct(
        clientId: Int,
        productId: Int,
    ): Flow<DataState<SavingProductsTemplate>> {
        return dataManagerSavings.getClientSavingsAccountTemplateByProduct(clientId, productId)
            .asDataStateFlow()
    }

    override fun getGroupSavingsAccountTemplateByProduct(
        groupId: Int,
        productId: Int,
    ): Flow<DataState<SavingProductsTemplate>> {
        return dataManagerSavings.getGroupSavingsAccountTemplateByProduct(groupId, productId)
            .asDataStateFlow()
    }

    override fun createSavingsAccount(savingsPayload: SavingsPayload?): Flow<DataState<Savings>> {
        return dataManagerSavings.createSavingsAccount(savingsPayload)
            .asDataStateFlow()
    }
}
