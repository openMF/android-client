/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.data.savings.impl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.first
import kpt.core.base.data.annotation.FromStore
import kpt.core.base.data.annotation.RepositoryBinding
import kpt.core.base.store.screen.FetchPolicy
import kpt.core.base.store.screen.ScreenDataStream
import kpt.core.base.store.screen.asScreenStream
import kpt.core.data.savings.SavingsAccountRepository
import kpt.core.database.savings.entity.SavingsAccountTransactionTemplateEntity
import kpt.core.database.savings.entity.SavingsAccountWithAssociationsEntity
import kpt.core.store.config.AppCacheKeys
import kpt.core.store.config.AppStoreIds
import kpt.core.store.config.AppStoreRegistry
import kpt.core.store.savings.GetSavingsAccountTransactionTemplateKey
import kpt.core.store.savings.SavingsAccountKey
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse

@RepositoryBinding(binds = SavingsAccountRepository::class)
internal class SavingsAccountRepositoryImpl(
    @FromStore(AppStoreIds.SavingsAccounts)
    private val store: Store<SavingsAccountKey, SavingsAccountWithAssociationsEntity>,
    @FromStore(AppStoreIds.SavingsGetSavingsAccountTransactionTemplate)
    private val transactionTemplateStore: Store<
        GetSavingsAccountTransactionTemplateKey,
        SavingsAccountTransactionTemplateEntity,
        >,
) : SavingsAccountRepository {

    override fun savingsAccountStream(
        savingsAccountType: String,
        savingsAccountId: Int,
        scope: CoroutineScope,
    ): ScreenDataStream<SavingsAccountWithAssociationsEntity> =
        store.asScreenStream(
            key = SavingsAccountKey(savingsAccountType, savingsAccountId),
            cacheKey = AppCacheKeys.SavingsAccounts.forAccount(savingsAccountType, savingsAccountId),
            scope = scope,
            fetchPolicy = FetchPolicy.NETWORK_WITH_CACHE,
            ttl = AppStoreRegistry.Ttl.SAVINGS_ACCOUNTS,
        )

    override suspend fun transactionTemplate(
        savingsAccountType: String,
        savingsAccountId: Int,
        transactionType: String,
    ): SavingsAccountTransactionTemplateEntity? = runCatching {
        transactionTemplateStore
            .stream(
                StoreReadRequest.cached(
                    GetSavingsAccountTransactionTemplateKey(
                        savingsAccountType = savingsAccountType,
                        savingsAccountId = savingsAccountId,
                        transactionType = transactionType,
                    ),
                    refresh = false,
                ),
            )
            .filterNot {
                it is StoreReadResponse.Loading ||
                    it is StoreReadResponse.NoNewData ||
                    it is StoreReadResponse.Initial
            }
            .first()
            .requireData()
    }.getOrNull()
}
