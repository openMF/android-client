/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.data.offline.impl

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kpt.core.base.data.annotation.FromStore
import kpt.core.base.data.annotation.RepositoryBinding
import kpt.core.data.center.impl.toCenter
import kpt.core.data.offline.OfflineDownloadRepository
import kpt.core.model.center.Center
import kpt.core.model.center.CenterAccounts
import kpt.core.model.center.CenterWithAssociations
import kpt.core.model.client.Client
import kpt.core.model.client.ClientAccounts
import kpt.core.model.group.Group
import kpt.core.model.group.GroupAccounts
import kpt.core.model.group.GroupWithAssociations
import kpt.core.model.loan.LoanAccount
import kpt.core.model.loan.LoanRepaymentTemplate
import kpt.core.model.objects.account.loan.loanWithAssociations.LoanWithAssociations
import kpt.core.model.objects.error.serverErrorMessage
import kpt.core.model.offline.DownloadFailure
import kpt.core.model.offline.DownloadProgress
import kpt.core.model.offline.DownloadTarget
import kpt.core.model.savings.SavingsAccount
import kpt.core.model.savings.SavingsAccountTransactionTemplate
import kpt.core.model.savings.SavingsAccountWithAssociations
import kpt.core.store.config.AppStoreIds
import kpt.core.store.savings.GetSavingsAccountTransactionTemplateKey
import kpt.core.store.savings.SavingsAccountKey
import org.mobilenativefoundation.store.core5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.MutableStore
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreWriteRequest
import org.mobilenativefoundation.store.store5.impl.extensions.fresh

/**
 * The walk described on [OfflineDownloadRepository]. Every `fresh` below uses the key its screen's
 * stream uses (see e.g. `ClientRepositoryImpl.clientStream`, `LoanRepositoryImpl.repaymentTemplateStream`,
 * `SavingsAccountRepositoryImpl.savingsAccountStream` / `transactionTemplate`) — that is the whole point.
 */
@RepositoryBinding(binds = OfflineDownloadRepository::class)
internal class OfflineDownloadRepositoryImpl(
    @FromStore(AppStoreIds.CenterGetCenterWithGroupMembersAndCollectionMeetingCalendar)
    private val centerStore: Store<Int, CenterWithAssociations>,
    @FromStore(AppStoreIds.CenterGetCenterAccounts) private val centerAccountsStore: Store<Int, CenterAccounts>,
    @FromStore(AppStoreIds.CenterItem) private val centerItemStore: MutableStore<Int, Center>,
    @FromStore(AppStoreIds.GroupGetGroupWithAssociations) private val groupStore: Store<Int, GroupWithAssociations>,
    @FromStore(AppStoreIds.GroupGetGroup) private val groupRowStore: Store<Int, Group>,
    @FromStore(AppStoreIds.GroupGetGroupAccounts) private val groupAccountsStore: Store<Int, GroupAccounts>,
    @FromStore(AppStoreIds.GroupItem) private val groupItemStore: MutableStore<Int, Group>,
    @FromStore(AppStoreIds.ClientGetClient) private val clientStore: Store<Int, Client>,
    @FromStore(AppStoreIds.ClientGetClientAccounts) private val clientAccountsStore: Store<Int, ClientAccounts>,
    @FromStore(AppStoreIds.Loans) private val loanStore: Store<Int, LoanWithAssociations>,
    @FromStore(AppStoreIds.LoanRepaymentTemplates) private val repaymentTemplateStore: Store<Int, LoanRepaymentTemplate>,
    @FromStore(AppStoreIds.SavingsAccounts) private val savingsStore: Store<SavingsAccountKey, SavingsAccountWithAssociations>,
    @FromStore(AppStoreIds.SavingsGetSavingsAccountTransactionTemplate)
    private val savingsTemplateStore: Store<GetSavingsAccountTransactionTemplateKey, SavingsAccountTransactionTemplate>,
) : OfflineDownloadRepository {

    override fun downloadCenters(centers: List<DownloadTarget>): Flow<DownloadProgress> =
        walk(centers) { center -> downloadCenter(center) }

    override fun downloadGroups(groups: List<DownloadTarget>): Flow<DownloadProgress> =
        walk(groups) { group -> downloadGroup(group.id, markDownloaded = true) }

    /** Runs [each] per target, emitting progress after every record and once more when finished. */
    private fun walk(targets: List<DownloadTarget>, each: suspend Walk.(DownloadTarget) -> Unit): Flow<DownloadProgress> = flow {
        val walk = Walk(this, DownloadProgress(total = targets.size))
        walk.emitNow()
        for (target in targets) {
            walk.update { it.copy(current = target.name) }
            walk.current = target.name
            walk.each(target)
            walk.update { it.copy(completed = it.completed + 1) }
        }
        walk.update { it.copy(current = null, finished = true) }
    }

    private suspend fun Walk.downloadCenter(center: DownloadTarget) {
        val withGroups = fetch("center") { centerStore.fresh(center.id) } ?: return
        fetch("center accounts") { centerAccountsStore.fresh(center.id) }?.let { accounts ->
            downloadAccounts(accounts.loanAccounts + accounts.memberLoanAccounts, accounts.savingsAccounts)
        }
        for (group in withGroups.groupMembers) {
            group.id?.let { downloadGroup(it, markDownloaded = false) }
        }
        markDownloaded("center") {
            centerItemStore.write(StoreWriteRequest.of<Int, Center, Any>(key = center.id, value = withGroups.toCenter().copy(id = center.id, sync = true)))
        }
    }

    /** [markDownloaded] only for a group the officer SELECTED — a center's groups ride on the center's flag. */
    private suspend fun Walk.downloadGroup(groupId: Int, markDownloaded: Boolean) {
        val withClients = fetch("group #$groupId") { groupStore.fresh(groupId) } ?: return
        fetch("group #$groupId accounts") { groupAccountsStore.fresh(groupId) }?.let { accounts ->
            downloadAccounts(accounts.loanAccounts, accounts.savingsAccounts)
        }
        for (client in withClients.clientMembers) {
            client.id?.let { downloadClient(it, client.displayName) }
        }
        if (markDownloaded) {
            markDownloaded("group #$groupId") {
                val row = groupRowStore.fresh(groupId)
                groupItemStore.write(StoreWriteRequest.of<Int, Group, Any>(key = groupId, value = row.copy(id = groupId, sync = true)))
            }
        }
    }

    private suspend fun Walk.downloadClient(clientId: Int, name: String?) {
        val label = name ?: "client #$clientId"
        fetch(label) { clientStore.fresh(clientId) } ?: return
        fetch("$label accounts") { clientAccountsStore.fresh(clientId) }?.let { accounts ->
            downloadAccounts(accounts.loanAccounts, accounts.savingsAccounts)
        }
    }

    /** Only ACTIVE accounts — what an officer can collect against — as the original did. */
    private suspend fun Walk.downloadAccounts(loans: List<LoanAccount>, savings: List<SavingsAccount>) {
        for (loan in loans) {
            val id = loan.id ?: continue
            if (loan.status?.active != true) continue
            fetch("loan #$id") { loanStore.fresh(id) }
            fetch("loan #$id repayment template") { repaymentTemplateStore.fresh(id) }
        }
        for (account in savings) {
            val id = account.id ?: continue
            if (account.status?.active != true) continue
            val type = account.depositType?.endpoint ?: SAVINGS_ENDPOINT
            fetch("savings #$id") { savingsStore.fresh(SavingsAccountKey(type, id)) }
            for (command in TRANSACTION_COMMANDS) {
                fetch("savings #$id $command template") {
                    savingsTemplateStore.fresh(GetSavingsAccountTransactionTemplateKey(type, id, command))
                }
            }
        }
    }

    private companion object {
        /** `ServerTypes.SAVINGS.endpoint` — an account that does not say otherwise is a plain savings account. */
        const val SAVINGS_ENDPOINT = "savingsaccounts"

        /** The commands the savings transaction screen asks a template for (`TransactionKind`). */
        val TRANSACTION_COMMANDS = listOf("deposit", "withdrawal")
    }
}

/** Progress accumulator: counts records, records failures under the current target, and emits. */
private class Walk(private val collector: FlowCollector<DownloadProgress>, private var progress: DownloadProgress) {
    var current: String = ""

    suspend fun emitNow() = collector.emit(progress)

    suspend fun update(change: (DownloadProgress) -> DownloadProgress) {
        progress = change(progress)
        collector.emit(progress)
    }

    /**
     * One record. A failure is recorded and the walk goes on — one closed account must not cost the
     * officer the rest of the center. Returns null when it failed, so dependants are skipped.
     */
    suspend fun <T : Any> fetch(what: String, block: suspend () -> T): T? = try {
        block().also { update { it.copy(records = it.records + 1) } }
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (error: Exception) {
        update { it.copy(failures = it.failures + DownloadFailure("$current · $what", error.serverErrorMessage())) }
        null
    }

    @OptIn(ExperimentalStoreApi::class)
    suspend fun markDownloaded(what: String, block: suspend () -> Unit) {
        fetch("$what (mark downloaded)") { block() }
    }
}
