/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.services

import com.mifos.core.data.SavingsPayload
import com.mifos.core.model.APIEndPoint
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.accounts.loan.SavingsApproval
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionResponse
import com.mifos.core.objects.accounts.savings.SavingsAccountWithAssociations
import com.mifos.core.objects.client.Savings
import com.mifos.core.objects.organisation.ProductSavings
import com.mifos.core.objects.templates.savings.SavingProductsTemplate
import com.mifos.core.objects.templates.savings.SavingsAccountTransactionTemplate
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Observable

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
        @Path("savingsAccountType") savingsAccountType: String?,
        @Path("savingsAccountId") savingsAccountId: Int,
        @Query("associations") association: String?
    ): Observable<SavingsAccountWithAssociations>

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
        @Path("savingsAccountType") savingsAccountType: String?,
        @Path("savingsAccountId") savingsAccountId: Int,
        @Query("command") transactionType: String?
    ): Observable<SavingsAccountTransactionTemplate>

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
    fun processTransaction(
        @Path("savingsAccountType") savingsAccountType: String?,
        @Path("savingsAccountId") savingsAccountId: Int,
        @Query("command") transactionType: String?,
        @Body savingsAccountTransactionRequest: SavingsAccountTransactionRequest?
    ): Observable<SavingsAccountTransactionResponse>

    @POST(APIEndPoint.CREATE_SAVINGS_ACCOUNTS + "/{savingsAccountId}/?command=activate")
    fun activateSavings(
        @Path("savingsAccountId") savingsAccountId: Int,
        @Body genericRequest: HashMap<String, String>
    ): Observable<GenericResponse>

    @POST(APIEndPoint.CREATE_SAVINGS_ACCOUNTS + "/{savingsAccountId}?command=approve")
    fun approveSavingsApplication(
        @Path("savingsAccountId") savingsAccountId: Int,
        @Body savingsApproval: SavingsApproval?
    ): Observable<GenericResponse>

    @get:GET(APIEndPoint.CREATE_SAVINGS_PRODUCTS)
    val allSavingsAccounts: Observable<List<ProductSavings>>

    @POST(APIEndPoint.CREATE_SAVINGS_ACCOUNTS)
    fun createSavingsAccount(@Body savingsPayload: SavingsPayload?): Observable<Savings>

    @get:GET(APIEndPoint.CREATE_SAVINGS_PRODUCTS + "/template")
    val savingsAccountTemplate: Observable<SavingProductsTemplate>

    @GET(APIEndPoint.CREATE_SAVINGS_ACCOUNTS + "/template")
    fun getClientSavingsAccountTemplateByProduct(
        @Query("clientId") clientId: Int,
        @Query("productId") productId: Int
    ): Observable<SavingProductsTemplate>

    @GET(APIEndPoint.CREATE_SAVINGS_ACCOUNTS + "/template")
    fun getGroupSavingsAccountTemplateByProduct(
        @Query("groupId") groupId: Int,
        @Query("productId") productId: Int
    ): Observable<SavingProductsTemplate>
}