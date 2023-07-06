package com.mifos.mifosxdroid.offline.syncsavingsaccounttransaction

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.PaymentTypeOption
import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest

/**
 * Created by Rajan Maurya on 19/08/16.
 */
interface SyncSavingsAccountTransactionMvpView : MvpView {
    fun showSavingsAccountTransactions(transactions: List<SavingsAccountTransactionRequest>)
    fun showEmptySavingsAccountTransactions(synMessage: Int)
    fun showPaymentTypeOptions(paymentTypeOptions: List<PaymentTypeOption>)
    fun checkNetworkConnectionAndSync()
    fun showOfflineModeDialog()
    fun showError(message: Int)
}