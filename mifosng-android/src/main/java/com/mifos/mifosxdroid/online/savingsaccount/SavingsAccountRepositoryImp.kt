package com.mifos.mifosxdroid.online.savingsaccount

import com.mifos.api.datamanager.DataManagerSavings
import com.mifos.objects.client.Savings
import com.mifos.objects.organisation.ProductSavings
import com.mifos.objects.templates.savings.SavingProductsTemplate
import com.mifos.services.data.SavingsPayload
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
        productId: Int
    ): Observable<SavingProductsTemplate> {
        return dataManagerSavings.getClientSavingsAccountTemplateByProduct(clientId, productId)
    }

    override fun getGroupSavingsAccountTemplateByProduct(
        groupId: Int,
        productId: Int
    ): Observable<SavingProductsTemplate> {
        return getGroupSavingsAccountTemplateByProduct(groupId, productId)
    }

    override fun createSavingsAccount(savingsPayload: SavingsPayload?): Observable<Savings> {
        return dataManagerSavings.createSavingsAccount(savingsPayload)
    }
}