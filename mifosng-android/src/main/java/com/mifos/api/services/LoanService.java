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
                                @Body LoanApprovalRequest loanApprovalRequest,
                                Callback<GenericResponse> genericResponseCallback);

    //  Mandatory Fields
    //  String actualDisbursementDate
    @POST(APIEndPoint.LOANS + "/{loanId}/?command=disburse")
    void disburseLoan(@Path("loanId") int loanId,
                      @Body HashMap<String, Object> genericRequest,
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
}
