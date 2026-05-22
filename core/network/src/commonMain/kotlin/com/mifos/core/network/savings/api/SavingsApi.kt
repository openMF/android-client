/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.savings.api

import com.mifos.core.model.GenericResponse
import com.mifos.core.model.objects.account.loan.SavingsApproval
import com.mifos.core.model.objects.account.saving.SavingsAccountTransactionResponse
import com.mifos.core.model.objects.organisations.ProductSavings
import com.mifos.core.model.objects.payloads.SavingsPayload
import com.mifos.core.network.APIEndPoint
import com.mifos.room.savings.entity.SavingProductsTemplate
import com.mifos.room.savings.entity.SavingsAccountTransactionRequestEntity
import com.mifos.room.savings.entity.SavingsAccountTransactionTemplateEntity
import com.mifos.room.savings.entity.SavingsAccountWithAssociationsEntity
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

/** Fineract `savings` (savings-account) domain endpoints. */
interface SavingsApi {

    /**
     * Retrieve a savings account by id, optionally including associations
     * (`all` / `transactions` / `charges`).
     */
    @GET("{savingsAccountType}/{savingsAccountId}")
    suspend fun getSavingsAccountWithAssociations(
        @Path("savingsAccountType") savingsAccountType: String,
        @Path("savingsAccountId") savingsAccountId: Int,
        @Query("associations") association: String?,
    ): SavingsAccountWithAssociationsEntity

    /**
     * Retrieve the transaction template (e.g. for `Deposit` / `Withdrawal` previews).
     */
    @GET("{savingsAccountType}/{savingsAccountId}/transactions/template")
    suspend fun getSavingsAccountTransactionTemplate(
        @Path("savingsAccountType") savingsAccountType: String,
        @Path("savingsAccountId") savingsAccountId: Int,
        @Query("command") transactionType: String?,
    ): SavingsAccountTransactionTemplateEntity

    /** Submit a savings-account transaction (deposit, withdrawal, etc.). */
    @POST("{savingsAccountType}/{savingsAccountId}/transactions")
    suspend fun processTransaction(
        @Path("savingsAccountType") savingsAccountType: String,
        @Path("savingsAccountId") savingsAccountId: Int,
        @Query("command") transactionType: String?,
        @Body savingsAccountTransactionRequest: SavingsAccountTransactionRequestEntity?,
    ): SavingsAccountTransactionResponse

    /** Activate a savings account. */
    @POST(APIEndPoint.CREATE_SAVINGS_ACCOUNTS + "/{savingsAccountId}/?command=activate")
    suspend fun activate(
        @Path("savingsAccountId") savingsAccountId: Int,
        @Body genericRequest: HashMap<String, String>,
    ): GenericResponse

    /** Approve a pending savings application. */
    @POST(APIEndPoint.CREATE_SAVINGS_ACCOUNTS + "/{savingsAccountId}?command=approve")
    suspend fun approveSavingsApplication(
        @Path("savingsAccountId") savingsAccountId: Int,
        @Body savingsApproval: SavingsApproval?,
    ): GenericResponse

    /** List all savings products. */
    @GET(APIEndPoint.CREATE_SAVINGS_PRODUCTS)
    suspend fun getAllSavingsProducts(): List<ProductSavings>

    /** Create a new savings account. */
    @POST(APIEndPoint.CREATE_SAVINGS_ACCOUNTS)
    suspend fun createSavingsAccount(@Body savingsPayload: SavingsPayload?): GenericResponse

    /** Retrieve the savings-product template (defaults for creation form). */
    @GET(APIEndPoint.CREATE_SAVINGS_PRODUCTS + "/template")
    suspend fun getSavingsProductTemplate(): SavingProductsTemplate

    /** Retrieve a client-scoped savings creation template for a specific product. */
    @GET(APIEndPoint.CREATE_SAVINGS_ACCOUNTS + "/template")
    suspend fun getClientSavingsTemplate(
        @Query("clientId") clientId: Int,
        @Query("productId") productId: Int,
    ): SavingProductsTemplate

    /** Retrieve a group-scoped savings creation template for a specific product. */
    @GET(APIEndPoint.CREATE_SAVINGS_ACCOUNTS + "/template")
    suspend fun getGroupSavingsTemplate(
        @Query("groupId") groupId: Int,
        @Query("productId") productId: Int,
    ): SavingProductsTemplate
}
