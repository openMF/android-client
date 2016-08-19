/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services;

import com.mifos.api.GenericResponse;
import com.mifos.api.model.APIEndPoint;
import com.mifos.objects.accounts.loan.SavingsApproval;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionResponse;
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations;
import com.mifos.objects.templates.savings.SavingsAccountTransactionTemplate;

import java.util.HashMap;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author fomenkoo
 */
public interface SavingsAccountService {

    /**
     * This Service Retrieve a savings application/account. From the REST API :
     * https://demo.openmf.org/fineract-provider/api/v1/savingsaccounts/{savingsAccountIs}
     * ?associations={all or transactions or charges}
     *
     * @param savingsAccountType SavingsAccount Type of SavingsAccount
     * @param savingsAccountId   SavingsAccounts Id
     * @param association        {all or transactions or charges}
     *                           'all': Gets data related to all associations e.g.
     *                           ?associations=all.
     *                           'transactions': Gets data related to transactions on the account
     *                           e.g.
     *                           ?associations=transactions
     *                           'charges':Savings Account charges data.
     * @return SavingsAccountWithAssociations
     */
    @GET("{savingsAccountType}/{savingsAccountId}")
    Observable<SavingsAccountWithAssociations> getSavingsAccountWithAssociations(
            @Path("savingsAccountType") String savingsAccountType,
            @Path("savingsAccountId") int savingsAccountId,
            @Query("associations") String association);

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
    Observable<SavingsAccountTransactionTemplate> getSavingsAccountTransactionTemplate(
            @Path("savingsAccountType") String savingsAccountType,
            @Path("savingsAccountId") int savingsAccountId,
            @Query("command") String transactionType);


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
    Observable<SavingsAccountTransactionResponse> processTransaction(
            @Path("savingsAccountType") String savingsAccountType,
            @Path("savingsAccountId") int savingsAccountId,
            @Query("command") String transactionType,
            @Body SavingsAccountTransactionRequest savingsAccountTransactionRequest);


    @POST(APIEndPoint.CREATESAVINGSACCOUNTS + "/{savingsAccountId}/?command=activate")
    Observable<GenericResponse> activateSavings(@Path("savingsAccountId") int savingsAccountId,
                                                @Body HashMap<String, Object> genericRequest);

    @POST(APIEndPoint.CREATESAVINGSACCOUNTS + "/{savingsAccountId}?command=approve")
    Observable<GenericResponse> approveSavingsApplication(
            @Path("savingsAccountId") int savingsAccountId,
            @Body SavingsApproval savingsApproval);

}
