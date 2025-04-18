/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.services

import com.mifos.core.model.objects.account.loan.SavingsApproval
import com.mifos.core.model.objects.account.saving.SavingsAccountTransactionResponse
import com.mifos.core.model.objects.organisations.ProductSavings
import com.mifos.core.model.objects.payloads.SavingsPayload
import com.mifos.core.network.GenericResponse
import com.mifos.room.basemodel.APIEndPoint
import com.mifos.room.entities.accounts.savings.SavingsAccountTransactionRequestEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountWithAssociationsEntity
import com.mifos.room.entities.client.Savings
import com.mifos.room.entities.templates.savings.SavingProductsTemplate
import com.mifos.room.entities.templates.savings.SavingsAccountTransactionTemplateEntity
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import kotlinx.coroutines.flow.Flow

/**
 * @author fomenkoo
 */
interface SavingsAccountService {
    /**
     * This Service Retrieve a savings application/account. From the REST API :
     * https://demo.openmf.org/fineract-provider/api/v1/savingsaccounts/{savingsAccountIs}
     * ?associations={all or transactions or charges}
     *
     * @param savingsAccountType SavingsAccount Type of SavingsAccount
     * @param savingsAccountId   SavingsAccounts Id
     * @param association        {all or transactions or charges}
     * 'all': Gets data related to all associations e.g.
     * ?associations=all.
     * 'transactions': Gets data related to transactions on the account
     * e.g.
     * ?associations=transactions
     * 'charges':Savings Account charges data.
     * @return SavingsAccountWithAssociations
     */
    @GET("{savingsAccountType}/{savingsAccountId}")
    fun getSavingsAccountWithAssociations(
        @Path("savingsAccountType") savingsAccountType: String,
        @Path("savingsAccountId") savingsAccountId: Int,
        @Query("associations") association: String?,
    ): Flow<SavingsAccountWithAssociationsEntity>

    /**
     * This Method for Retrieving Savings Account Transaction Template from REST API
     * https://demo.openmf.org/fineract-provider/api/v1/{savingsAccountType}/{savingsAccountId}
     * /transactions/template.
     *
     * @param savingsAccountType SavingsAccount Type Example : 'savingsaccounts'
     * @param savingsAccountId   SavingsAccount Id
     * @param transactionType    Transaction Type Example : 'Deposit', 'Withdrawal'
     * @return SavingsAccountTransactionTemplate
     */
    @GET("{savingsAccountType}/{savingsAccountId}/transactions/template")
    fun getSavingsAccountTransactionTemplate(
        @Path("savingsAccountType") savingsAccountType: String,
        @Path("savingsAccountId") savingsAccountId: Int,
        @Query("command") transactionType: String?,
    ): Flow<SavingsAccountTransactionTemplateEntity>

    /**
     * This Service making POST Request to the REST API :
     * https://demo.openmf.org/fineract-provider/api/v1/{savingsAccountType}/
     * {savingsAccountId}/transactions?command={transactionType}
     *
     * @param savingsAccountType               SavingsAccount Type Example : 'savingsaccounts'
     * @param savingsAccountId                 SavingsAccount Id
     * @param transactionType                  Transaction Type Example : 'Deposit', 'Withdrawal'
     * @param savingsAccountTransactionRequest SavingsAccountTransactionRequest
     * @return SavingsAccountTransactionResponse
     */
    @POST("{savingsAccountType}/{savingsAccountId}/transactions")
    suspend fun processTransaction(
        @Path("savingsAccountType") savingsAccountType: String,
        @Path("savingsAccountId") savingsAccountId: Int,
        @Query("command") transactionType: String?,
        @Body savingsAccountTransactionRequest: SavingsAccountTransactionRequestEntity?,
    ): SavingsAccountTransactionResponse

    @POST(APIEndPoint.CREATE_SAVINGS_ACCOUNTS + "/{savingsAccountId}/?command=activate")
    fun activateSavings(
        @Path("savingsAccountId") savingsAccountId: Int,
        @Body genericRequest: HashMap<String, String>,
    ): Flow<GenericResponse>

    @POST(APIEndPoint.CREATE_SAVINGS_ACCOUNTS + "/{savingsAccountId}?command=approve")
    fun approveSavingsApplication(
        @Path("savingsAccountId") savingsAccountId: Int,
        @Body savingsApproval: SavingsApproval?,
    ): Flow<GenericResponse>

    @GET(APIEndPoint.CREATE_SAVINGS_PRODUCTS)
    fun allSavingsAccounts(): Flow<List<ProductSavings>>

    @POST(APIEndPoint.CREATE_SAVINGS_ACCOUNTS)
    fun createSavingsAccount(@Body savingsPayload: SavingsPayload?): Flow<Savings>

    @GET(APIEndPoint.CREATE_SAVINGS_PRODUCTS + "/template")
    fun savingsAccountTemplate(): Flow<SavingProductsTemplate>

    @GET(APIEndPoint.CREATE_SAVINGS_ACCOUNTS + "/template")
    fun getClientSavingsAccountTemplateByProduct(
        @Query("clientId") clientId: Int,
        @Query("productId") productId: Int,
    ): Flow<SavingProductsTemplate>

    @GET(APIEndPoint.CREATE_SAVINGS_ACCOUNTS + "/template")
    fun getGroupSavingsAccountTemplateByProduct(
        @Query("groupId") groupId: Int,
        @Query("productId") productId: Int,
    ): Flow<SavingProductsTemplate>
}
