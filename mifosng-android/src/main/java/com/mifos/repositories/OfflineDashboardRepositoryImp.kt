package com.mifos.repositories

import com.mifos.api.datamanager.DataManagerCenter
import com.mifos.api.datamanager.DataManagerClient
import com.mifos.api.datamanager.DataManagerGroups
import com.mifos.api.datamanager.DataManagerLoan
import com.mifos.api.datamanager.DataManagerSavings
import com.mifos.objects.accounts.loan.LoanRepaymentRequest
import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.objects.client.ClientPayload
import com.mifos.objects.group.GroupPayload
import com.mifos.services.data.CenterPayload
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 16/08/23.
 */
class OfflineDashboardRepositoryImp @Inject constructor(
    private val dataManagerClient: DataManagerClient,
    private val dataManagerGroups: DataManagerGroups,
    private val dataManagerCenter: DataManagerCenter,
    private val dataManagerLoan: DataManagerLoan,
    private val dataManagerSavings: DataManagerSavings
) : OfflineDashboardRepository {

    override fun allDatabaseClientPayload(): Observable<List<ClientPayload>> {
        return dataManagerClient.allDatabaseClientPayload
    }

    override fun allDatabaseGroupPayload(): Observable<List<GroupPayload>> {
        return dataManagerGroups.allDatabaseGroupPayload
    }

    override fun allDatabaseCenterPayload(): Observable<List<CenterPayload>> {
        return dataManagerCenter.allDatabaseCenterPayload
    }

    override fun databaseLoanRepayments(): Observable<List<LoanRepaymentRequest>> {
        return dataManagerLoan.databaseLoanRepayments
    }

    override fun allSavingsAccountTransactions(): Observable<List<SavingsAccountTransactionRequest>> {
        return dataManagerSavings.allSavingsAccountTransactions
    }
}