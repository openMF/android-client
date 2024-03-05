package com.mifos.mifosxdroid.offline.syncsavingsaccounttransaction

import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionRequest

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class SyncSavingsAccountTransactionUiState {

    data class ShowError(val message: Int) : SyncSavingsAccountTransactionUiState()

    data class ShowSavingsAccountTransactions(val savingsList: MutableList<SavingsAccountTransactionRequest>) :
        SyncSavingsAccountTransactionUiState()

    data class ShowEmptySavingsAccountTransactions(val message: Int) :
        SyncSavingsAccountTransactionUiState()

    object ShowProgressbar : SyncSavingsAccountTransactionUiState()

    data class ShowPaymentTypeOptions(val paymentTypeOptions: List<com.mifos.core.objects.PaymentTypeOption>) :
        SyncSavingsAccountTransactionUiState()
}