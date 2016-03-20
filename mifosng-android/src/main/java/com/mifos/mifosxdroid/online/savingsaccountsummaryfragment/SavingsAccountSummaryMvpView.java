package com.mifos.mifosxdroid.online.savingsaccountsummaryfragment;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations;
import com.mifos.objects.noncore.DataTable;

import java.util.List;

/**
 * Created by Rajan Maurya on 19/3/16.
 */
public interface SavingsAccountSummaryMvpView extends MvpView {

    void showSavingAccount(SavingsAccountWithAssociations savingsAccountWithAssociations);

    void ResponseFailedSavingAccount(String s);

    void showSavingDataTable(List<DataTable> dataTables);

    void ResponseErrorLoading(String s);
}
