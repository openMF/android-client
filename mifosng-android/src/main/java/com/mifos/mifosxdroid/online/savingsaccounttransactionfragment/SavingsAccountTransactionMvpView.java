package com.mifos.mifosxdroid.online.savingsaccounttransactionfragment;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionResponse;
import com.mifos.objects.templates.savings.SavingsAccountTransactionTemplate;

/**
 * Created by Rajan Maurya on 19/3/16.
 */
public interface SavingsAccountTransactionMvpView extends MvpView {

    void showSavingAccountTemlate(SavingsAccountTransactionTemplate savingsAccountTransactionTemplate);

    void ResponseError(String s);

    void showProcessTransactionResult(SavingsAccountTransactionResponse savingsAccountTransactionResponse);
}
