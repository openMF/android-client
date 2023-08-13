package com.mifos.repositories

import com.mifos.api.datamanager.DataManagerSavings
import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.objects.accounts.savings.SavingsAccountTransactionResponse
import com.mifos.objects.templates.savings.SavingsAccountTransactionTemplate
import rx.Observable
import javax.inject.Inject

class SavingsAccountTransactionRepositoryImp @Inject constructor(private val dataManagerSavings: DataManagerSavings) :
    SavingsAccountTransactionRepository {

    override fun getSavingsAccountTransactionTemplate(
        type: String?,
        savingsAccountId: Int,
        transactionType: String?
    ): Observable<SavingsAccountTransactionTemplate> {
        return dataManagerSavings.getSavingsAccountTransactionTemplate(
            type,
            savingsAccountId,
            transactionType
        )
    }

    override fun processTransaction(
        savingsAccountType: String?,
        savingsAccountId: Int,
        transactionType: String?,
        request: SavingsAccountTransactionRequest
    ): Observable<SavingsAccountTransactionResponse> {
        return dataManagerSavings.processTransaction(
            savingsAccountType,
            savingsAccountId,
            transactionType,
            request
        )
    }

    override fun getSavingsAccountTransaction(savingAccountId: Int): Observable<SavingsAccountTransactionRequest> {
        return dataManagerSavings.getSavingsAccountTransaction(savingAccountId)
    }


}