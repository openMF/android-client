package com.mifos.mifosxdroid.online.datatablelistfragment

import com.mifos.core.data.GroupLoanPayload
import com.mifos.core.data.LoansPayload
import com.mifos.core.network.DataManager
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.core.objects.accounts.loan.Loans
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.ClientPayload
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class DataTableListRepositoryImp @Inject constructor(
    private val dataManagerLoan: DataManagerLoan, private val dataManager: DataManager,
    private val dataManagerClient: DataManagerClient
) : DataTableListRepository {

    override fun createLoansAccount(loansPayload: LoansPayload?): Observable<Loans> {
        return dataManagerLoan.createLoansAccount(loansPayload)
    }

    override fun createGroupLoansAccount(loansPayload: GroupLoanPayload?): Observable<Loans> {
        return dataManager.createGroupLoansAccount(loansPayload)
    }

    override fun createClient(clientPayload: ClientPayload): Observable<Client> {
        return dataManagerClient.createClient(clientPayload)
    }
}