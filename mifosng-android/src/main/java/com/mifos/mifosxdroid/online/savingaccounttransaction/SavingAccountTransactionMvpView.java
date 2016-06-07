package com.mifos.mifosxdroid.online.savingaccounttransaction;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionResponse;
import com.mifos.objects.templates.savings.SavingsAccountTransactionTemplate;

/**
 * Created by Rajan Maurya on 07/06/16.
 */
public interface SavingAccountTransactionMvpView extends MvpView {

    void showSavingAccountTemplate(SavingsAccountTransactionTemplate
                                           savingsAccountTransactionTemplate);

    void showTransactionSuccessfullyDone(SavingsAccountTransactionResponse
                                                 savingsAccountTransactionResponse);

    void showFetchingError(String s);
}
