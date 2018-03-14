package com.mifos.mifosxdroid.online.loanaccountsummary;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.accounts.loan.LoanWithAssociations;

/**
 * Created by Rajan Maurya on 07/06/16.
 */
public interface LoanAccountSummaryMvpView extends MvpView {

    void showLoanById(LoanWithAssociations loanWithAssociations);

    void showFetchingError(String s);
}
