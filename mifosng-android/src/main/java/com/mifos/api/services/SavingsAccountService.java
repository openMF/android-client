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

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * @author fomenkoo
 */
public interface SavingsAccountService {
    /**
     * @param savingsAccountId                       - savingsAccountId for which information is requested
     * @param association                            - Mention Type of Association Needed, Like :- all, transactions etc.
     * @param savingsAccountWithAssociationsCallback - callback to receive the response
     *                                               <p/>
     *                                               Use this method to retrieve the Savings Account With Associations
     */
    @GET("/{savingsAccountType}/{savingsAccountId}")
    void getSavingsAccountWithAssociations(@Path("savingsAccountType") String savingsAccountType,
                                           @Path("savingsAccountId") int savingsAccountId,
                                           @Query("associations") String association,
                                           Callback<SavingsAccountWithAssociations> savingsAccountWithAssociationsCallback);

    /**
     * @param savingsAccountId                          - savingsAccountId for which information is requested
     * @param savingsAccountTransactionTemplateCallback - Savings Account Transaction Template Callback
     *                                                  <p/>
     *                                                  Use this method to retrieve the Savings Account Transaction Template
     */
    @GET("/{savingsAccountType}/{savingsAccountId}/transactions/template")
    void getSavingsAccountTransactionTemplate(@Path("savingsAccountType") String savingsAccountType,
                                              @Path("savingsAccountId") int savingsAccountId,
                                              @Query("command") String transactionType,
                                              Callback<SavingsAccountTransactionTemplate> savingsAccountTransactionTemplateCallback);

    @POST("/{savingsAccountType}/{savingsAccountId}/transactions")
    void processTransaction(@Path("savingsAccountType") String savingsAccountType,
                            @Path("savingsAccountId") int savingsAccountId,
                            @Query("command") String transactionType,
                            @Body SavingsAccountTransactionRequest savingsAccountTransactionRequest,
                            Callback<SavingsAccountTransactionResponse> savingsAccountTransactionResponseCallback);


    @POST(APIEndPoint.CREATESAVINGSACCOUNTS + "/{savingsAccountId}/?command=activate")
     void activateSavings(@Path("savingsAccountId") int savingsAccountId,
                                @Body HashMap<String, Object> genericRequest,
                                Callback<GenericResponse> genericResponseCallback);

    @POST(APIEndPoint.CREATESAVINGSACCOUNTS + "/{savingsAccountId}?command=approve")
    void approveSavingsApplication(@Path("savingsAccountId") int savingsAccountId,
                                          @Body SavingsApproval savingsApproval,
                                          Callback<GenericResponse> genericResponseCallback);


}
