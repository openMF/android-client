/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services;

import com.mifos.objects.accounts.loan.LoanApprovalRequest;
import com.mifos.objects.accounts.loan.LoanRepaymentRequest;
import com.mifos.objects.accounts.loan.LoanRepaymentResponse;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.templates.loans.LoanRepaymentTemplate;
import com.mifos.api.GenericResponse;
import com.mifos.api.model.APIEndPoint;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

/**
 * @author fomenkoo
 */
public interface LoanService {

    @GET(APIEndPoint.LOANS + "/{loanId}?associations=all")
    Observable<LoanWithAssociations> getLoanByIdWithAllAssociations(@Path("loanId") int loanId);

    @GET(APIEndPoint.LOANS + "/{loanId}/transactions/template?command=repayment")
    Observable<LoanRepaymentTemplate> getLoanRepaymentTemplate(@Path("loanId") int loanId);


    //  Mandatory Fields
    //  1. String approvedOnDate
    @POST(APIEndPoint.LOANS + "/{loanId}?command=approve")
    Observable<GenericResponse> approveLoanApplication(@Path("loanId") int loanId,
                                @Body LoanApprovalRequest loanApprovalRequest);

    //  Mandatory Fields
    //  String actualDisbursementDate
    @POST(APIEndPoint.LOANS + "/{loanId}/?command=disburse")
    Observable<GenericResponse> disburseLoan(@Path("loanId") int loanId,
                      @Body HashMap<String, Object> genericRequest);

    @POST(APIEndPoint.LOANS + "/{loanId}/transactions?command=repayment")
    Observable<LoanRepaymentResponse> submitPayment(@Path("loanId") int loanId,
                       @Body LoanRepaymentRequest loanRepaymentRequest);

    @GET(APIEndPoint.LOANS + "/{loanId}?associations=repaymentSchedule")
    void getLoanRepaymentSchedule(@Path("loanId") int loanId,
                                  Callback<LoanWithAssociations> loanWithRepaymentScheduleCallback);

    @GET(APIEndPoint.LOANS + "/{loanId}?associations=transactions")
    void getLoanWithTransactions(@Path("loanId") int loanId,
                                 Callback<LoanWithAssociations> loanWithAssociationsCallback);
}
