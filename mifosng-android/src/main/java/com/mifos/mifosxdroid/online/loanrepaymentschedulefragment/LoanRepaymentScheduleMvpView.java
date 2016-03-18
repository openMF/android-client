package com.mifos.mifosxdroid.online.loanrepaymentschedulefragment;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.accounts.loan.LoanWithAssociations;

/**
 * Created by Rajan Maurya on 18/3/16.
 */
public interface LoanRepaymentScheduleMvpView extends MvpView {

    void showLoanRepaySchedule(LoanWithAssociations loanWithAssociations);

    void ResponseError(String s);
}
