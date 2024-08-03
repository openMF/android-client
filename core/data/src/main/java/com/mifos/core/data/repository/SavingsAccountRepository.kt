package com.mifos.core.data.repository

import com.mifos.core.data.SavingsPayload
import com.mifos.core.objects.client.Savings
import com.mifos.core.objects.organisation.ProductSavings
import com.mifos.core.objects.templates.savings.SavingProductsTemplate
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