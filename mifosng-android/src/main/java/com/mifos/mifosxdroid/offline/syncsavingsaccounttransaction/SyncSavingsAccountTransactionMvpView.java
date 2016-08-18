package com.mifos.mifosxdroid.offline.syncsavingsaccounttransaction;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.PaymentTypeOption;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest;

import java.util.List;

/**
 * Created by Rajan Maurya on 19/08/16.
 */
public interface SyncSavingsAccountTransactionMvpView extends MvpView {

    void showSavingsAccountTransactions(List<SavingsAccountTransactionRequest> transactions);

    void showEmptySavingsAccountTransactions(int synMessage);

    void showPaymentTypeOptions(List<PaymentTypeOption> paymentTypeOptions);

    void checkNetworkConnectionAndSync();

    void showOfflineModeDialog();

    void showError(int message);

}
