package com.mifos.feature.savings.account_transaction

import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionResponse
import com.mifos.core.objects.templates.savings.SavingsAccountTransactionTemplate

/**
 * Created by Aditya Gupta on 13/08/23.
 */
sealed class SavingsAccountTransactionUiState {

    data object ShowProgressbar : SavingsAccountTransactionUiState()

    data class ShowError(val message: String) : SavingsAccountTransactionUiState()

    data class ShowSavingAccountTemplate(val savingsAccountTransactionTemplate: SavingsAccountTransactionTemplate) :
        SavingsAccountTransactionUiState()


    data class ShowTransactionSuccessfullyDone(val savingsAccountTransactionResponse: SavingsAccountTransactionResponse) :
        SavingsAccountTransactionUiState()

    data object ShowSavingAccountTransactionExistInDatabase : SavingsAccountTransactionUiState()

    data object ShowSavingAccountTransactionDoesNotExistInDatabase : SavingsAccountTransactionUiState()
}
