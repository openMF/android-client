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

import com.mifos.core.data.SavingsPayload
import com.mifos.core.data.repository.SavingsAccountRepository
import com.mifos.core.network.datamanager.DataManagerSavings
import com.mifos.core.objects.client.Savings
import com.mifos.core.objects.organisation.ProductSavings
import com.mifos.core.objects.templates.savings.SavingProductsTemplate
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class SavingsAccountRepositoryImp @Inject constructor(private val dataManagerSavings: DataManagerSavings) :
    SavingsAccountRepository {
    override fun savingsAccounts(): Observable<List<ProductSavings>> {
        return dataManagerSavings.savingsAccounts
    }

    override fun savingsAccountTemplate(): Observable<SavingProductsTemplate> {
        return dataManagerSavings.savingsAccountTemplate
    }

    override fun getClientSavingsAccountTemplateByProduct(
        clientId: Int,
        productId: Int,
    ): Observable<SavingProductsTemplate> {
        return dataManagerSavings.getClientSavingsAccountTemplateByProduct(clientId, productId)
    }

    override fun getGroupSavingsAccountTemplateByProduct(
        groupId: Int,
        productId: Int,
    ): Observable<SavingProductsTemplate> {
        return dataManagerSavings.getGroupSavingsAccountTemplateByProduct(groupId, productId)
    }

    override fun createSavingsAccount(savingsPayload: SavingsPayload?): Observable<Savings> {
        return dataManagerSavings.createSavingsAccount(savingsPayload)
    }
}
