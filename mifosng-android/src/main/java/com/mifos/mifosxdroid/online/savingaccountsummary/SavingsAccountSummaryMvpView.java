package com.mifos.mifosxdroid.online.savingaccountsummary;

import com.mifos.api.GenericResponse;
import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations;
import com.mifos.objects.noncore.DataTable;

import java.util.List;

/**
 * Created by Rajan Maurya on 07/06/16.
 */
public interface SavingsAccountSummaryMvpView extends MvpView {

    void showSavingDataTable(List<DataTable> dataTables);

    void showSavingAccount(SavingsAccountWithAssociations savingsAccountWithAssociations);

    void showErrorFetchingSavingAccount(int message);

    void showSavingsActivatedSuccessfully(GenericResponse genericResponse);

    void showFetchingError(int errorMessage);
}
