package com.mifos.mifosxdroid.online.loanaccountsummaryfragment;

import com.mifos.api.GenericResponse;
import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.noncore.DataTable;

import java.util.List;

/**
 * Created by Rajan Maurya on 18/3/16.
 */
public interface LoanAccountSummaryMvpView extends MvpView {

    void showLoanByid(LoanWithAssociations loanWithAssociations);

    void ResponseError(String s);

    void showloanDataTableList(List<DataTable> dataTables);

    void approveloan(GenericResponse genericResponse);

    void disputeLoan(GenericResponse genericResponse);
}
