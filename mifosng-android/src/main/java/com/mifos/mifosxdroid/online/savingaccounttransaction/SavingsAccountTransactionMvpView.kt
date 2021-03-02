package com.mifos.mifosxdroid.online.savingaccounttransaction

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.accounts.savings.SavingsAccountTransactionResponse
import com.mifos.objects.templates.savings.SavingsAccountTransactionTemplate

/**
 * Created by Rajan Maurya on 07/06/16.
 */
interface SavingsAccountTransactionMvpView : MvpView {
    fun showSavingAccountTemplate(savingsAccountTransactionTemplate: SavingsAccountTransactionTemplate?)
    fun showTransactionSuccessfullyDone(savingsAccountTransactionResponse: SavingsAccountTransactionResponse?)
    fun checkSavingAccountTransactionStatusInDatabase()
    fun showSavingAccountTransactionExistInDatabase()
    fun showSavingAccountTransactionDoesNotExistInDatabase()
    fun showError(errorMessage: Int)
}