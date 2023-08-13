package com.mifos.repositories

import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.objects.accounts.savings.SavingsAccountTransactionResponse
import com.mifos.objects.templates.savings.SavingsAccountTransactionTemplate
import rx.Observable

/**
 * Created by Aditya Gupta on 13/08/23.
 */
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