package com.mifos.mifosxdroid.online.savingaccountsummary;

import com.mifos.api.GenericResponse;
import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations;

/**
 * Created by Rajan Maurya on 07/06/16.
 */
public interface SavingsAccountSummaryMvpView extends MvpView {

    void showSavingAccount(SavingsAccountWithAssociations savingsAccountWithAssociations);

    void showSavingsActivatedSuccessfully(GenericResponse genericResponse);

    void showFetchingError(int errorMessage);

    void showFetchingError(String errorMessage);
}
