package com.mifos.mifosxdroid.offline.offlinedashbarod

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.accounts.loan.LoanRepaymentRequest
import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.objects.client.ClientPayload
import com.mifos.objects.group.GroupPayload
import com.mifos.services.data.CenterPayload

/**
 * Created by Rajan Maurya on 20/07/16.
 */
interface OfflineDashboardMvpView : MvpView {
    fun showClients(clientPayloads: List<ClientPayload>)
    fun showGroups(groupPayloads: List<GroupPayload>)
    fun showCenters(centerPayloads: List<CenterPayload>)
    fun showLoanRepaymentTransactions(loanRepaymentRequests: List<LoanRepaymentRequest>)
    fun showSavingsAccountTransaction(transactions: List<SavingsAccountTransactionRequest>)
    fun showNoPayloadToShow()
    fun showError(s: Int)
}