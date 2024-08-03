package com.mifos.feature.savings.sync_account_transaction

import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionRequest

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class SyncSavingsAccountTransactionUiState {
    data object Loading : SyncSavingsAccountTransactionUiState()

    data class ShowError(val message: Int) : SyncSavingsAccountTransactionUiState()

    data class ShowSavingsAccountTransactions(
        val savingsList: MutableList<SavingsAccountTransactionRequest>,
        val paymentTypeOptions: List<com.mifos.core.objects.PaymentTypeOption>
    ) :
        SyncSavingsAccountTransactionUiState()

    data class ShowEmptySavingsAccountTransactions(val message: Int) :
        SyncSavingsAccountTransactionUiState()
}