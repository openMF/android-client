/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services;

import com.mifos.api.GenericResponse;
import com.mifos.api.model.APIEndPoint;
import com.mifos.objects.accounts.loan.LoanApproval;
import com.mifos.objects.accounts.loan.LoanDisbursement;
import com.mifos.objects.accounts.loan.LoanRepaymentRequest;
import com.mifos.objects.accounts.loan.LoanRepaymentResponse;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.accounts.loan.Loans;
import com.mifos.objects.client.Charges;
import com.mifos.objects.client.Page;
import com.mifos.objects.organisation.ProductLoans;
import com.mifos.objects.templates.loans.LoanRepaymentTemplate;
import com.mifos.services.data.GroupLoanPayload;
import com.mifos.services.data.LoansPayload;

import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * @author fomenkoo
 */
public interface LoanService {

    @GET(APIEndPoint.LOANS + "/{loanId}?associations=all")
    void getLoanByIdWithAllAssociations(@Path("loanId") int loanId, Callback<LoanWithAssociations> loanCallback);

    @GET(APIEndPoint.LOANS + "/{loanId}/transactions/template?command=repayment")
    void getLoanRepaymentTemplate(@Path("loanId") int loanId,
                                  Callback<LoanRepaymentTemplate> loanRepaymentTemplateCallback);


    //  Mandatory Fields
    //  1. String approvedOnDate
    @POST(APIEndPoint.LOANS + "/{loanId}?command=approve")
    void approveLoanApplication(@Path("loanId") int loanId,
                                @Body LoanApproval loanApproval,
                                Callback<GenericResponse> genericResponseCallback);

    //  Mandatory Fields
    //  String actualDisbursementDate
    @POST(APIEndPoint.LOANS + "/{loanId}/?command=disburse")
    public void disburseLoan(@Path("loanId") int loanId,
                             @Body LoanDisbursement loanDisbursement,
                             Callback<GenericResponse> genericResponseCallback);

    @POST(APIEndPoint.LOANS + "/{loanId}/transactions?command=repayment")
    void submitPayment(@Path("loanId") int loanId,
                       @Body LoanRepaymentRequest loanRepaymentRequest,
                       Callback<LoanRepaymentResponse> loanRepaymentResponseCallback);

    @GET(APIEndPoint.LOANS + "/{loanId}?associations=repaymentSchedule")
    void getLoanRepaymentSchedule(@Path("loanId") int loanId,
                                  Callback<LoanWithAssociations> loanWithRepaymentScheduleCallback);

    @GET(APIEndPoint.LOANS + "/{loanId}?associations=transactions")
    void getLoanWithTransactions(@Path("loanId") int loanId,
                                 Callback<LoanWithAssociations> loanWithAssociationsCallback);

    @GET(APIEndPoint.CREATELOANSPRODUCTS)
    void getAllLoans(Callback<List<ProductLoans>> listOfLoansCallback);


    @POST(APIEndPoint.CREATELOANSACCOUNTS)
    void createLoansAccount(@Body LoansPayload loansPayload, Callback<Loans> callback);


    @GET(APIEndPoint.CREATELOANSACCOUNTS + "/template?templateType=individual")
    void getLoansAccountTemplate(@Query("clientId") int clientId, @Query("productId") int productId, Callback<Response> loanCallback);


    @GET(APIEndPoint.LOANS + "/{loanId}/transactions/template?command=disburse")
    void getLoanTemplate(@Path("loanId") int loanId, Callback<Response> loanTemplateCallback);

    @POST(APIEndPoint.CREATELOANSACCOUNTS)
    void createGroupLoansAccount(@Body GroupLoanPayload loansPayload, Callback<Loans> callback);


    @GET(APIEndPoint.CREATELOANSACCOUNTS + "/template?templateType=group")
    void getGroupLoansAccountTemplate(@Query("groupId") int groupId, @Query("productId") int productId, Callback<Response> grouploanCallback);

    @GET(APIEndPoint.LOANS + "/{loanId}" + APIEndPoint.CHARGES)
    void getListOfLoanCharges(@Path("loanId") int loanId, Callback<Page<Charges>> loanchargeListCallback);


    @GET(APIEndPoint.CLIENTS + "/{clientId}" + APIEndPoint.CHARGES)
    void getListOfCharges(@Path("clientId") int clientId, Callback<Page<Charges>> chargeListCallback);


}

