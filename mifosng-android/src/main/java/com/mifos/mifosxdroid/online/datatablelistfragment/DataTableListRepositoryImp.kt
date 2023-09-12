package com.mifos.mifosxdroid.online.datatablelistfragment

import com.mifos.api.DataManager
import com.mifos.api.datamanager.DataManagerClient
import com.mifos.api.datamanager.DataManagerLoan
import com.mifos.objects.accounts.loan.Loans
import com.mifos.objects.client.Client
import com.mifos.objects.client.ClientPayload
import com.mifos.services.data.GroupLoanPayload
import com.mifos.services.data.LoansPayload
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