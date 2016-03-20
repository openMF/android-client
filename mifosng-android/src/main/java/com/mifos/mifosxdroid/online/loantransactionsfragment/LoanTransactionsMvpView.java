package com.mifos.mifosxdroid.online.loantransactionsfragment;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.accounts.loan.LoanWithAssociations;

/**
 * Created by Rajan Maurya on 18/3/16.
 */
public interface LoanTransactionsMvpView extends MvpView {

    void showLoanTransactions(LoanWithAssociations loanWithAssociations);

    void ResponseError(String s);
}
