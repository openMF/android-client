package com.mifos.mifosxdroid.online.loantransactions;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.accounts.loan.LoanWithAssociations;

/**
 * Created by Rajan Maurya on 7/6/16.
 */
public interface LoanTransactionsMvpView extends MvpView {

    void showLoanTransaction(LoanWithAssociations loanWithAssociations);

    void showFetchingError(String s);
}
