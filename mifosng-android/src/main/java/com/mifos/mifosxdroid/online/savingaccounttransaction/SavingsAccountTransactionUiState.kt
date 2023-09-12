package com.mifos.mifosxdroid.online.savingaccounttransaction

import com.mifos.objects.accounts.savings.SavingsAccountTransactionResponse
import com.mifos.objects.templates.savings.SavingsAccountTransactionTemplate

/**
 * Created by Aditya Gupta on 13/08/23.
 */
sealed class SavingsAccountTransactionUiState {

    object ShowProgressbar : SavingsAccountTransactionUiState()

    data class ShowError(val message: String) : SavingsAccountTransactionUiState()

    data class ShowSavingAccountTemplate(val savingsAccountTransactionTemplate: SavingsAccountTransactionTemplate) :
        SavingsAccountTransactionUiState()

    data class ShowTransactionSuccessfullyDone(val savingsAccountTransactionResponse: SavingsAccountTransactionResponse) :
        SavingsAccountTransactionUiState()

    object ShowSavingAccountTransactionExistInDatabase : SavingsAccountTransactionUiState()
}
