package com.mifos.mifosxdroid.online.loanrepaymentschedule;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.accounts.loan.LoanWithAssociations;

/**
 * Created by Rajan Maurya on 08/06/16.
 */
public interface LoanRepaymentScheduleMvpView extends MvpView {

    void showLoanRepaySchedule(LoanWithAssociations loanWithAssociations);

    void showFetchingError(String s);
}
