/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.data.offline

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kpt.core.base.store.infra.StoreFactory
import kpt.core.data.offline.impl.OfflineDownloadRepositoryImpl
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
import kpt.core.model.loan.LoanStatus
import kpt.core.model.objects.account.loan.loanWithAssociations.LoanWithAssociations
import kpt.core.model.offline.DownloadTarget
import kpt.core.model.savings.SavingAccountDepositType
import kpt.core.model.savings.SavingsAccount
import kpt.core.model.savings.SavingsAccountStatus
import kpt.core.model.savings.SavingsAccountTransactionTemplate
import kpt.core.model.savings.SavingsAccountWithAssociations
import kpt.core.store.savings.GetSavingsAccountTransactionTemplateKey
import kpt.core.store.savings.SavingsAccountKey
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.StoreBuilder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OfflineDownloadRepositoryTest {

    /** Every network read, in order — "what did the download fetch". */
    private val fetched = mutableListOf<String>()

    /** A read-only store answering from [data]; a missing key is a server error. */
    private fun <K : Any, V : Any> store(name: String, data: Map<K, V>) = StoreBuilder.from(
        fetcher = Fetcher.of { key: K ->
            fetched += "$name:$key"
            data[key] ?: throw RuntimeException("$name $key not found")
        },
    ).build()

    private val centerRows = MutableStateFlow<Map<Int, Center>>(emptyMap())
    private val groupRows = MutableStateFlow<Map<Int, Group>>(emptyMap())

    private val activeLoan = LoanAccount(id = 501, status = LoanStatus(active = true))
    private val closedLoan = LoanAccount(id = 502, status = LoanStatus(active = false))
    private val fixedDeposit = SavingsAccount(id = 601, status = SavingsAccountStatus(active = true), depositType = SavingAccountDepositType(id = 200))

    private val repository = OfflineDownloadRepositoryImpl(
        centerStore = store("center", mapOf(1 to CenterWithAssociations(id = 1, name = "North", groupMembers = listOf(Group(id = 10))))),
        centerAccountsStore = store("centerAccounts", mapOf(1 to CenterAccounts())),
        centerItemStore = StoreFactory.createOfflineMutableStore(
            sourceOfTruth = SourceOfTruth.of(
                reader = { id: Int -> centerRows.map { it[id] } },
                writer = { id: Int, row: Center -> centerRows.value += id to row },
            ),
        ),
        // Group 10 has two clients; client 101's detail is missing on the server.
        groupStore = store("group", mapOf(10 to GroupWithAssociations(id = 10, clientMembers = listOf(Client(id = 100, displayName = "Asha"), Client(id = 101, displayName = "Bimal"))))),
        groupRowStore = store("groupRow", mapOf(10 to Group(id = 10, name = "Weavers"))),
        groupAccountsStore = store("groupAccounts", mapOf(10 to GroupAccounts())),
        groupItemStore = StoreFactory.createOfflineMutableStore(
            sourceOfTruth = SourceOfTruth.of(
                reader = { id: Int -> groupRows.map { it[id] } },
                writer = { id: Int, row: Group -> groupRows.value += id to row },
            ),
        ),
        clientStore = store("client", mapOf(100 to Client(id = 100))),
        clientAccountsStore = store("clientAccounts", mapOf(100 to ClientAccounts(loanAccounts = listOf(activeLoan, closedLoan), savingsAccounts = listOf(fixedDeposit)))),
        loanStore = store("loan", mapOf(501 to LoanWithAssociations())),
        repaymentTemplateStore = store("repaymentTemplate", mapOf(501 to LoanRepaymentTemplate())),
        savingsStore = store("savings", mapOf(SavingsAccountKey("fixeddepositaccounts", 601) to SavingsAccountWithAssociations())),
        savingsTemplateStore = store(
            "savingsTemplate",
            mapOf(
                GetSavingsAccountTransactionTemplateKey("fixeddepositaccounts", 601, "deposit") to SavingsAccountTransactionTemplate(),
                GetSavingsAccountTransactionTemplateKey("fixeddepositaccounts", 601, "withdrawal") to SavingsAccountTransactionTemplate(),
            ),
        ),
    )

    @Test
    fun aCenterIsWalkedDownToEveryActiveAccountWithTheScreensKeys() = runTest {
        val progress = repository.downloadCenters(listOf(DownloadTarget(1, "North"))).toList()

        // The center → its group → its clients → each ACTIVE account, with the keys the screens use.
        assertTrue("loan:501" in fetched && "repaymentTemplate:501" in fetched)
        assertTrue(fetched.none { it.startsWith("loan:502") }, "a closed loan is not downloaded")
        // A fixed deposit is read under ITS endpoint — the key the savings summary streams.
        assertTrue(fetched.any { it.startsWith("savings:") && "fixeddepositaccounts" in it })
        assertEquals(2, fetched.count { it.startsWith("savingsTemplate:") }) // deposit + withdrawal

        val last = progress.last()
        assertTrue(last.finished)
        assertEquals(1, last.completed)
        // Bimal's detail failed: recorded, and the walk still finished.
        assertEquals(1, last.failures.size)
        assertTrue("Bimal" in last.failures.single().item)
        // The selected center is marked downloaded — what its list shows.
        assertEquals(true, centerRows.value[1]?.sync)
        assertEquals("North", centerRows.value[1]?.name)
    }

    @Test
    fun aSelectedGroupIsMarkedButACentersGroupIsNot() = runTest {
        repository.downloadCenters(listOf(DownloadTarget(1, "North"))).toList()
        assertTrue(groupRows.value.isEmpty(), "a center's groups ride on the center's flag")

        repository.downloadGroups(listOf(DownloadTarget(10, "Weavers"))).toList()
        assertEquals(true, groupRows.value[10]?.sync)
        assertEquals("Weavers", groupRows.value[10]?.name)
    }

    @Test
    fun progressCountsUpAndEndsFinished() = runTest {
        val progress = repository.downloadGroups(listOf(DownloadTarget(10, "Weavers"), DownloadTarget(99, "Missing"))).toList()
        assertEquals(2, progress.first().total)
        assertEquals(0, progress.first().completed)
        val last = progress.last()
        assertEquals(2, last.completed)
        assertTrue(last.finished)
        // Group 99 does not exist — one failure, not a crash, and the first group's records still count.
        assertTrue(last.failures.any { "Missing" in it.item })
        assertTrue(last.records > 0)
    }
}
