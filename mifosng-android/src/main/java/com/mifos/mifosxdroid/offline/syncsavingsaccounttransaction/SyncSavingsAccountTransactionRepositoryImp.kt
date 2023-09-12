package com.mifos.mifosxdroid.offline.syncsavingsaccounttransaction

import com.mifos.api.datamanager.DataManagerLoan
import com.mifos.api.datamanager.DataManagerSavings
import com.mifos.objects.PaymentTypeOption
import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.objects.accounts.savings.SavingsAccountTransactionResponse
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 16/08/23.
 */
class SyncSavingsAccountTransactionRepositoryImp @Inject constructor(
    private val dataManagerSavings: DataManagerSavings,
    private val dataManagerLoan: DataManagerLoan
) : SyncSavingsAccountTransactionRepository {

    override fun allSavingsAccountTransactions(): Observable<List<SavingsAccountTransactionRequest>> {
        return dataManagerSavings.allSavingsAccountTransactions
    }

    override fun paymentTypeOption(): Observable<List<PaymentTypeOption>> {
        return dataManagerLoan.paymentTypeOption
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

    override fun deleteAndUpdateTransactions(savingsAccountId: Int): Observable<List<SavingsAccountTransactionRequest>> {
        return dataManagerSavings.deleteAndUpdateTransactions(savingsAccountId)
    }

    override fun updateLoanRepaymentTransaction(savingsAccountTransactionRequest: SavingsAccountTransactionRequest): Observable<SavingsAccountTransactionRequest> {
        return dataManagerSavings.updateLoanRepaymentTransaction(savingsAccountTransactionRequest)
    }
}