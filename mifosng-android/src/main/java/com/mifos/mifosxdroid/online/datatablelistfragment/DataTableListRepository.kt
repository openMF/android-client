package com.mifos.mifosxdroid.online.datatablelistfragment

import com.mifos.objects.accounts.loan.Loans
import com.mifos.objects.client.Client
import com.mifos.objects.client.ClientPayload
import com.mifos.services.data.GroupLoanPayload
import com.mifos.services.data.LoansPayload
import rx.Observable

/**
 * Created by Aditya Gupta on 10/08/23.
 */
interface DataTableListRepository {

    fun createLoansAccount(loansPayload: LoansPayload?): Observable<Loans>

    fun createGroupLoansAccount(loansPayload: GroupLoanPayload?): Observable<Loans>

    fun createClient(clientPayload: ClientPayload): Observable<Client>
}