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
import com.mifos.objects.organisation.LoanProducts;
import com.mifos.objects.templates.loans.GroupLoanTemplate;
import com.mifos.objects.templates.loans.LoanRepaymentTemplate;
import com.mifos.objects.templates.loans.LoanTemplate;
import com.mifos.objects.templates.loans.LoanTransactionTemplate;
import com.mifos.services.data.GroupLoanPayload;
import com.mifos.services.data.LoansPayload;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author fomenkoo
 */
public interface LoanService {

    @GET(APIEndPoint.LOANS + "/{loanId}?associations=all&exclude=guarantors,futureSchedule")
    Observable<LoanWithAssociations> getLoanByIdWithAllAssociations(@Path("loanId") int loanId);

    @GET(APIEndPoint.LOANS + "/{loanId}/transactions/template?command=repayment")
    Observable<LoanRepaymentTemplate> getLoanRepaymentTemplate(@Path("loanId") int loanId);


    //  Mandatory Fields
    //  1. String approvedOnDate
    @POST(APIEndPoint.LOANS + "/{loanId}?command=approve")
    Observable<GenericResponse> approveLoanApplication(@Path("loanId") int loanId,
                                                       @Body LoanApproval loanApproval);

    //  Mandatory Fields
    //  String actualDisbursementDate
    @POST(APIEndPoint.LOANS + "/{loanId}/?command=disburse")
    Observable<GenericResponse> disburseLoan(@Path("loanId") int loanId,
                                             @Body LoanDisbursement loanDisbursement);

    @POST(APIEndPoint.LOANS + "/{loanId}/transactions?command=repayment")
    Observable<LoanRepaymentResponse> submitPayment(
            @Path("loanId") int loanId,
            @Body LoanRepaymentRequest loanRepaymentRequest);

    @GET(APIEndPoint.LOANS + "/{loanId}?associations=repaymentSchedule")
    Observable<LoanWithAssociations> getLoanRepaymentSchedule(@Path("loanId") int loanId);

    @GET(APIEndPoint.LOANS + "/{loanId}?associations=transactions")
    Observable<LoanWithAssociations> getLoanWithTransactions(@Path("loanId") int loanId);

    @GET(APIEndPoint.CREATELOANSPRODUCTS)
    Observable<List<LoanProducts>> getAllLoans();


    @POST(APIEndPoint.CREATELOANSACCOUNTS)
    Observable<Loans> createLoansAccount(@Body LoansPayload loansPayload);


    @GET(APIEndPoint.CREATELOANSACCOUNTS + "/template?templateType=individual")
    Observable<LoanTemplate> getLoansAccountTemplate(@Query("clientId") int clientId,
                                                     @Query("productId") int productId);


    /**
     * For fetching any type of loan template.
     * Example:
     * 1. repayment
     * 2. disburse
     * 3. waiver
     * 4. refundbycash
     * 5. foreclosure
     *
     * @param loanId Loan Id
     * @param command Template Type
     * @return
     */
    @GET(APIEndPoint.LOANS + "/{loanId}/transactions/template")
    Observable<LoanTransactionTemplate> getLoanTransactionTemplate(
            @Path("loanId") int loanId,
            @Query("command") String command);


    @POST(APIEndPoint.CREATELOANSACCOUNTS)
    Observable<Loans> createGroupLoansAccount(@Body GroupLoanPayload loansPayload);

    @GET(APIEndPoint.CREATELOANSACCOUNTS + "/template?templateType=group")
    Observable<GroupLoanTemplate> getGroupLoansAccountTemplate(@Query("groupId") int groupId,
                                                               @Query("productId") int productId);

    @GET(APIEndPoint.LOANS + "/{loanId}/" + APIEndPoint.CHARGES)
    Observable<List<Charges>> getListOfLoanCharges(@Path("loanId") int loanId);


    @GET(APIEndPoint.CLIENTS + "/{clientId}/" + APIEndPoint.CHARGES)
    Observable<Page<Charges>> getListOfCharges(@Path("clientId") int clientId);


}

