package com.mifos.mifosxdroid.online.loanrepaymentfragment;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.accounts.loan.LoanRepaymentResponse;
import com.mifos.objects.templates.loans.LoanRepaymentTemplate;

/**
 * Created by Rajan Maurya on 18/3/16.
 */
public interface LoanRepaymentMvpView extends MvpView {

    void showloanrepaymenttemplate(LoanRepaymentTemplate loanRepaymentTemplate);

    void ResponseError(String s);

    void showsubmitPaymentResponse(LoanRepaymentResponse loanRepaymentResponse);
}
