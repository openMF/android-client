package com.mifos.mifosxdroid.offline.offlinedashbarod

import com.mifos.core.data.CenterPayload
import com.mifos.core.objects.accounts.loan.LoanRepaymentRequest
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.core.objects.client.ClientPayload
import com.mifos.core.objects.group.GroupPayload

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class OfflineDashboardUiState {

    object ShowProgressbar : OfflineDashboardUiState()

    data class ShowError(val message: String) : OfflineDashboardUiState()

    data class ShowClients(val clientPayloads: List<ClientPayload>) : OfflineDashboardUiState()

    data class ShowGroups(val groupPayloads: List<GroupPayload>) : OfflineDashboardUiState()

    data class ShowCenters(val centerPayloads: List<CenterPayload>) : OfflineDashboardUiState()

    data class ShowLoanRepaymentTransactions(val loanRepaymentRequests: List<LoanRepaymentRequest>) :
        OfflineDashboardUiState()

    data class ShowSavingsAccountTransaction(val transactionRequests: List<SavingsAccountTransactionRequest>) :
        OfflineDashboardUiState()
}