package com.mifos.mifosxdroid.offline.offlinedashbarod

import com.mifos.core.data.CenterPayload
import com.mifos.core.network.datamanager.DataManagerCenter
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.network.datamanager.DataManagerGroups
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.core.network.datamanager.DataManagerSavings
import com.mifos.core.objects.accounts.loan.LoanRepaymentRequest
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.core.objects.client.ClientPayload
import com.mifos.core.objects.group.GroupPayload
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