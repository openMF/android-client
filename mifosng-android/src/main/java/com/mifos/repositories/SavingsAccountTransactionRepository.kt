package com.mifos.repositories

import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.objects.accounts.savings.SavingsAccountTransactionResponse
import com.mifos.objects.templates.savings.SavingsAccountTransactionTemplate
import rx.Observable

interface SavingsAccountTransactionRepository {

    fun getSavingsAccountTransactionTemplate(
        type: String?, savingsAccountId: Int, transactionType: String?
    ): Observable<SavingsAccountTransactionTemplate>


    fun processTransaction(
        savingsAccountType: String?, savingsAccountId: Int, transactionType: String?,
        request: SavingsAccountTransactionRequest
    ): Observable<SavingsAccountTransactionResponse>

    fun getSavingsAccountTransaction(
        savingAccountId: Int
    ): Observable<SavingsAccountTransactionRequest>

}