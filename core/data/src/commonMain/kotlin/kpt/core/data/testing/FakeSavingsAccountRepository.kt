/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.data.testing

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kpt.core.base.store.screen.ScreenDataStream
import kpt.core.base.store.screen.ScreenState
import kpt.core.base.store.screen.screenDataStreamForTesting
import kpt.core.data.savings.SavingsAccountRepository
import kpt.core.database.savings.entity.SavingsAccountTransactionTemplateEntity
import kpt.core.database.savings.entity.SavingsAccountWithAssociationsEntity

/**
 * Test double for [SavingsAccountRepository] — the savings-side twin of [FakeLoanRepository].
 *
 * [requestedKeys] records the `(type, id)` pair the stream was asked for, which is the point of the
 * fake here: a savings account is addressed by BOTH, and passing the wrong type silently requests a
 * different account rather than failing.
 */
@ExperimentalDataTestingApi
class FakeSavingsAccountRepository(
    private val account: MutableStateFlow<ScreenState<SavingsAccountWithAssociationsEntity>>? = null,
    /** Answers the transaction-template question; null models the read failing. */
    private val transactionTemplate: () -> SavingsAccountTransactionTemplateEntity? = { null },
) : SavingsAccountRepository {

    val refreshes: MutableSharedFlow<Unit> = MutableSharedFlow(extraBufferCapacity = 8)

    /** Every `(savingsAccountType, savingsAccountId)` the stream was constructed for. */
    val requestedKeys: MutableList<Pair<String, Int>> = mutableListOf()

    override fun savingsAccountStream(
        savingsAccountType: String,
        savingsAccountId: Int,
        scope: CoroutineScope,
    ): ScreenDataStream<SavingsAccountWithAssociationsEntity> {
        requestedKeys += savingsAccountType to savingsAccountId
        val source = requireNotNull(account) {
            "FakeSavingsAccountRepository: savingsAccountStream was read but no state flow " +
                "was supplied for it"
        }
        return screenDataStreamForTesting(source, refreshTrigger = refreshes)
    }

    /** Every `(type, id, command)` the template was asked for. */
    val templateQueries: MutableList<Triple<String, Int, String>> = mutableListOf()

    override suspend fun transactionTemplate(
        savingsAccountType: String,
        savingsAccountId: Int,
        transactionType: String,
    ): SavingsAccountTransactionTemplateEntity? {
        templateQueries += Triple(savingsAccountType, savingsAccountId, transactionType)
        return transactionTemplate()
    }
}
