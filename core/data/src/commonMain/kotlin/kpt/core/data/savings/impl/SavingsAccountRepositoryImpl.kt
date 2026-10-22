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
import kpt.core.database.savings.entity.SavingProductsTemplate
import kpt.core.database.savings.entity.SavingsAccountTransactionTemplateEntity
import kpt.core.database.savings.entity.SavingsAccountWithAssociationsEntity
import kpt.core.model.objects.organisations.ProductSavings
import kpt.core.network.mifos.savings.dto.FixedDepositTemplate
import kpt.core.network.mifos.savings.dto.ShareTemplate
import kpt.core.store.config.AppCacheKeys
import kpt.core.store.config.AppStoreIds
import kpt.core.store.config.AppStoreRegistry
import kpt.core.store.savings.FixedDepositProductTemplateKey
import kpt.core.store.savings.GetClientSavingsAccountTemplateByProductKey
import kpt.core.store.savings.GetGroupSavingsAccountTemplateByProductKey
import kpt.core.store.savings.GetSavingsAccountTransactionTemplateKey
import kpt.core.store.savings.SavingsAccountKey
import kpt.core.store.savings.ShareProductTemplateKey
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
    @FromStore(AppStoreIds.SavingsProducts)
    private val productsStore: Store<Unit, List<ProductSavings>>,
    @FromStore(AppStoreIds.SavingsGetClientSavingsAccountTemplateByProduct)
    private val clientApplicationTemplateStore: Store<
        GetClientSavingsAccountTemplateByProductKey,
        SavingProductsTemplate,
        >,
    @FromStore(AppStoreIds.SavingsGetGroupSavingsAccountTemplateByProduct)
    private val groupApplicationTemplateStore: Store<
        GetGroupSavingsAccountTemplateByProductKey,
        SavingProductsTemplate,
        >,
    @FromStore(AppStoreIds.SavingsShareProductTemplate)
    private val shareTemplateStore: Store<ShareProductTemplateKey, ShareTemplate>,
    @FromStore(AppStoreIds.SavingsFixedDepositProductTemplate)
    private val fixedDepositTemplateStore: Store<FixedDepositProductTemplateKey, FixedDepositTemplate>,
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

    override fun savingsProductsStream(scope: CoroutineScope): ScreenDataStream<List<ProductSavings>> =
        productsStore.asScreenStream(
            key = Unit,
            cacheKey = AppCacheKeys.SavingsProducts.LIST,
            scope = scope,
            fetchPolicy = FetchPolicy.NETWORK_WITH_CACHE,
            ttl = AppStoreRegistry.Ttl.SAVINGS_PRODUCTS,
        )

    override suspend fun savingsApplicationTemplate(
        ownerId: Int,
        productId: Int,
        isGroup: Boolean,
    ): SavingProductsTemplate? = runCatching {
        // The owner decides the ENDPOINT, not just a payload field — Fineract has a separate
        // group template, and the original's wizard read neither.
        val responses = if (isGroup) {
            groupApplicationTemplateStore.stream(
                StoreReadRequest.cached(
                    GetGroupSavingsAccountTemplateByProductKey(groupId = ownerId, productId = productId),
                    refresh = false,
                ),
            )
        } else {
            clientApplicationTemplateStore.stream(
                StoreReadRequest.cached(
                    GetClientSavingsAccountTemplateByProductKey(clientId = ownerId, productId = productId),
                    refresh = false,
                ),
            )
        }

        responses
            .filterNot {
                it is StoreReadResponse.Loading ||
                    it is StoreReadResponse.NoNewData ||
                    it is StoreReadResponse.Initial
            }
            .first()
            .requireData()
    }.getOrNull()

    override fun shareTemplateStream(
        clientId: Int,
        productId: Int?,
        scope: CoroutineScope,
    ): ScreenDataStream<ShareTemplate> = shareTemplateStore.asScreenStream(
        key = ShareProductTemplateKey(clientId = clientId, productId = productId),
        cacheKey = AppCacheKeys.SavingsShareProductTemplate.forKey("$clientId:$productId"),
        scope = scope,
        fetchPolicy = FetchPolicy.NETWORK_WITH_CACHE,
        ttl = AppStoreRegistry.Ttl.SAVINGS_SHARE_PRODUCT_TEMPLATE,
    )

    override fun fixedDepositTemplateStream(
        clientId: Int,
        productId: Int?,
        scope: CoroutineScope,
    ): ScreenDataStream<FixedDepositTemplate> = fixedDepositTemplateStore.asScreenStream(
        key = FixedDepositProductTemplateKey(clientId = clientId, productId = productId),
        cacheKey = AppCacheKeys.SavingsFixedDepositProductTemplate.forKey("$clientId:$productId"),
        scope = scope,
        fetchPolicy = FetchPolicy.NETWORK_WITH_CACHE,
        ttl = AppStoreRegistry.Ttl.SAVINGS_FIXED_DEPOSIT_PRODUCT_TEMPLATE,
    )
}
