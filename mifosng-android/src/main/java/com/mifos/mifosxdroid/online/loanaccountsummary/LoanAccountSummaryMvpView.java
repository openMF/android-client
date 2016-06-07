package com.mifos.mifosxdroid.online.loanaccountsummary;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.noncore.DataTable;

import java.util.List;

/**
 * Created by Rajan Maurya on 07/06/16.
 */
public interface LoanAccountSummaryMvpView extends MvpView {

    void showLoanDataTable(List<DataTable> dataTables);

    void showLoanById(LoanWithAssociations loanWithAssociations);

    void showFetchingError(String s);
}
