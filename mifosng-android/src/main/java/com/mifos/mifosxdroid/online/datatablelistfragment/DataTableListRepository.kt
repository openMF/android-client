package com.mifos.mifosxdroid.online.datatablelistfragment

import com.mifos.core.data.GroupLoanPayload
import com.mifos.core.data.LoansPayload
import com.mifos.core.objects.accounts.loan.Loans
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.ClientPayload
import rx.Observable

/**
 * Created by Aditya Gupta on 10/08/23.
 */
interface DataTableListRepository {

    fun createLoansAccount(loansPayload: LoansPayload?): Observable<Loans>

    fun createGroupLoansAccount(loansPayload: GroupLoanPayload?): Observable<Loans>

    fun createClient(clientPayload: ClientPayload): Observable<Client>
}