/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services;

import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionResponse;
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations;
import com.mifos.objects.templates.savings.SavingsAccountTransactionTemplate;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * @author fomenkoo
 */
public interface SavingsAccountService {
    /**
     * @param savingsAccountId                       - savingsAccountId for which information is requested
     * @param association                            - Mention Type of Association Needed, Like :- all, transactions etc.
     * savingsAccountWithAssociationsCallback - callback to receive the response
     *                                               <p/>
     *                                               Use this method to retrieve the Savings Account With Associations
     */
    @GET("/{savingsAccountType}/{savingsAccountId}")
    Observable<SavingsAccountWithAssociations> getSavingsAccountWithAssociations(@Path("savingsAccountType") String savingsAccountType,
                                           @Path("savingsAccountId") int savingsAccountId,
                                           @Query("associations") String association);

    /**
     * @param savingsAccountId                          - savingsAccountId for which information is requested
     * savingsAccountTransactionTemplateCallback - Savings Account Transaction Template Callback
     *                                                  <p/>
     *                                                  Use this method to retrieve the Savings Account Transaction Template
     */
    @GET("/{savingsAccountType}/{savingsAccountId}/transactions/template")
    Observable<SavingsAccountTransactionTemplate> getSavingsAccountTransactionTemplate(@Path("savingsAccountType") String savingsAccountType,
                                              @Path("savingsAccountId") int savingsAccountId,
                                              @Query("command") String transactionType);

    @POST("/{savingsAccountType}/{savingsAccountId}/transactions")
    Observable<SavingsAccountTransactionResponse> processTransaction(@Path("savingsAccountType") String savingsAccountType,
                            @Path("savingsAccountId") int savingsAccountId,
                            @Query("command") String transactionType,
                            @Body SavingsAccountTransactionRequest savingsAccountTransactionRequest);
}
