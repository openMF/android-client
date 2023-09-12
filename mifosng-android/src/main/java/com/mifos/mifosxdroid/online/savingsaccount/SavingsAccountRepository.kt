package com.mifos.mifosxdroid.online.savingsaccount

import com.mifos.objects.client.Savings
import com.mifos.objects.organisation.ProductSavings
import com.mifos.objects.templates.savings.SavingProductsTemplate
import com.mifos.services.data.SavingsPayload
import rx.Observable

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface SavingsAccountRepository {

    fun savingsAccounts(): Observable<List<ProductSavings>>

    fun savingsAccountTemplate(): Observable<SavingProductsTemplate>

    fun getClientSavingsAccountTemplateByProduct(
        clientId: Int,
        productId: Int
    ): Observable<SavingProductsTemplate>

    fun getGroupSavingsAccountTemplateByProduct(
        groupId: Int,
        productId: Int
    ): Observable<SavingProductsTemplate>

    fun createSavingsAccount(savingsPayload: SavingsPayload?): Observable<Savings>

}