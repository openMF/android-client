/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.data.loan.impl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.first
import kpt.core.base.data.annotation.FromStore
import kpt.core.base.data.annotation.RepositoryBinding
import kpt.core.base.store.screen.FetchPolicy
import kpt.core.base.store.screen.ScreenDataStream
import kpt.core.base.store.screen.asScreenStream
import kpt.core.data.loan.LoanRepository
import kpt.core.database.charge.entity.ChargesEntity
import kpt.core.database.loan.entity.LoanRepaymentTemplateEntity
import kpt.core.database.loan.entity.LoanTemplate
import kpt.core.model.objects.account.loan.loanDisburse.LoanDisburseTemplate
import kpt.core.model.objects.account.loan.loanWithAssociations.LoanWithAssociations
import kpt.core.model.objects.account.loan.reschedules.LoanRescheduleResponse
import kpt.core.model.objects.account.loan.reschedules.LoanRescheduleTemplate
import kpt.core.model.objects.account.loan.transfer.AccountTransferTemplate
import kpt.core.model.objects.organisations.LoanProducts
import kpt.core.model.objects.template.client.ChargeTemplate
import kpt.core.model.objects.template.loan.GroupLoanTemplate
import kpt.core.store.charge.GetChargeTemplateKey
import kpt.core.store.config.AppCacheKeys
import kpt.core.store.config.AppStoreIds
import kpt.core.store.config.AppStoreRegistry
import kpt.core.store.loan.GetAccountTransferTemplateKey
import kpt.core.store.loan.GetGroupLoansAccountTemplateKey
import kpt.core.store.loan.LoanTemplateKey
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse

@RepositoryBinding(binds = LoanRepository::class)
internal class LoanRepositoryImpl(
    @FromStore(AppStoreIds.Loans) private val store: Store<Int, LoanWithAssociations>,
    @FromStore(AppStoreIds.LoanTemplates) private val templateStore: Store<LoanTemplateKey, LoanTemplate>,
    @FromStore(AppStoreIds.LoanRepaymentTemplates) private val repaymentTemplateStore: Store<Int, LoanRepaymentTemplateEntity>,
    @FromStore(AppStoreIds.LoanDisburseTemplates) private val disburseTemplateStore: Store<Int, LoanDisburseTemplate>,
    @FromStore(AppStoreIds.LoanCharges) private val chargeStore: Store<Int, List<ChargesEntity>>,
    @FromStore(AppStoreIds.LoanGetAccountTransferTemplate)
    private val transferTemplateStore: Store<GetAccountTransferTemplateKey, AccountTransferTemplate>,
    @FromStore(AppStoreIds.ChargeGetChargeTemplate)
    private val chargeTemplateStore: Store<GetChargeTemplateKey, ChargeTemplate>,
    @FromStore(AppStoreIds.LoanGetLoanReschedules)
    private val reschedulesStore: Store<Int, List<LoanRescheduleResponse>>,
    @FromStore(AppStoreIds.LoanRescheduleTemplate)
    private val rescheduleTemplateStore: Store<Unit, LoanRescheduleTemplate>,
    @FromStore(AppStoreIds.LoanProducts) private val loanProductsStore: Store<Unit, List<LoanProducts>>,
    @FromStore(AppStoreIds.LoanGetGroupLoansAccountTemplate)
    private val groupLoanTemplateStore: Store<GetGroupLoansAccountTemplateKey, GroupLoanTemplate>,
) : LoanRepository {

    // NETWORK_WITH_CACHE, not the CACHE_FIRST_SWR default: SWR's read path is
    // `cached(key, refresh = false)` and leaves fetching to a freshness-band gate that cannot fire
    // until something HAS been fetched, so on a clean install nothing ever loads. Room is still
    // served first, so the offline-first behaviour is unchanged. The ttl stays the declared
    // freshness bound — it drives the "updated N ago" indicator, never whether cache is served.
    override fun loanStream(loanId: Int, scope: CoroutineScope): ScreenDataStream<LoanWithAssociations> =
        store.asScreenStream(
            key = loanId,
            cacheKey = AppCacheKeys.Loans.byId(loanId),
            scope = scope,
            fetchPolicy = FetchPolicy.NETWORK_WITH_CACHE,
            ttl = AppStoreRegistry.Ttl.LOANS,
        )

    override fun loanTemplateStream(
        clientId: Int,
        productId: Int?,
        scope: CoroutineScope,
    ): ScreenDataStream<LoanTemplate> =
        templateStore.asScreenStream(
            key = LoanTemplateKey(clientId, productId),
            cacheKey = AppCacheKeys.LoanTemplates.forClient(clientId, productId ?: NO_PRODUCT),
            scope = scope,
            fetchPolicy = FetchPolicy.NETWORK_WITH_CACHE,
            ttl = AppStoreRegistry.Ttl.LOAN_TEMPLATES,
        )

    override fun repaymentTemplateStream(
        loanId: Int,
        scope: CoroutineScope,
    ): ScreenDataStream<LoanRepaymentTemplateEntity> =
        repaymentTemplateStore.asScreenStream(
            key = loanId,
            cacheKey = AppCacheKeys.LoanRepaymentTemplates.forLoan(loanId),
            scope = scope,
            fetchPolicy = FetchPolicy.NETWORK_WITH_CACHE,
            ttl = AppStoreRegistry.Ttl.LOAN_REPAYMENT_TEMPLATES,
        )

    override fun disburseTemplateStream(
        loanId: Int,
        scope: CoroutineScope,
    ): ScreenDataStream<LoanDisburseTemplate> =
        disburseTemplateStore.asScreenStream(
            key = loanId,
            cacheKey = AppCacheKeys.LoanDisburseTemplates.forLoan(loanId),
            scope = scope,
            fetchPolicy = FetchPolicy.NETWORK_WITH_CACHE,
            ttl = AppStoreRegistry.Ttl.LOAN_DISBURSE_TEMPLATES,
        )

    override suspend fun accountTransferTemplate(
        fromClientId: Int,
        fromAccountId: Int,
        fromAccountType: Int,
        fromOfficeId: Int?,
        toOfficeId: Int?,
        toClientId: Int?,
        toAccountType: Int?,
    ): AccountTransferTemplate? = runCatching {
        val key = GetAccountTransferTemplateKey(
            fromClientId = fromClientId,
            fromAccountType = fromAccountType,
            fromAccountId = fromAccountId,
            fromOfficeId = fromOfficeId,
            toOfficeId = toOfficeId,
            toClientId = toClientId,
            toAccountType = toAccountType,
        )
        // `cached(refresh = false)`: each distinct cascade step is its OWN store key, so stepping
        // back up the cascade — reopening a client dropdown after changing the account type — is
        // served from cache rather than re-asking the server the same question. The intermediate
        // responses are filtered out so `first()` cannot short-circuit on Loading.
        transferTemplateStore
            .stream(StoreReadRequest.cached(key, refresh = false))
            .filterNot {
                it is StoreReadResponse.Loading ||
                    it is StoreReadResponse.NoNewData ||
                    it is StoreReadResponse.Initial
            }
            .first()
            .requireData()
    }.getOrNull()

    override fun loanProductsStream(scope: CoroutineScope): ScreenDataStream<List<LoanProducts>> =
        loanProductsStore.asScreenStream(
            key = Unit,
            cacheKey = AppCacheKeys.LoanProducts.LIST,
            scope = scope,
            fetchPolicy = FetchPolicy.NETWORK_WITH_CACHE,
            ttl = AppStoreRegistry.Ttl.LOAN_PRODUCTS,
        )

    override suspend fun groupLoanTemplate(groupId: Int, productId: Int): GroupLoanTemplate? =
        runCatching {
            groupLoanTemplateStore
                .stream(
                    StoreReadRequest.cached(
                        GetGroupLoansAccountTemplateKey(groupId = groupId, productId = productId),
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

    override suspend fun loanTemplate(clientId: Int, productId: Int): LoanTemplate? =
        runCatching {
            templateStore
                .stream(
                    StoreReadRequest.cached(
                        LoanTemplateKey(clientId = clientId, productId = productId),
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

    override fun loanReschedulesStream(
        loanId: Int,
        scope: CoroutineScope,
    ): ScreenDataStream<List<LoanRescheduleResponse>> = reschedulesStore.asScreenStream(
        key = loanId,
        cacheKey = AppCacheKeys.LoanGetLoanReschedules.forKey(loanId.toString()),
        scope = scope,
        // No `isEmpty` predicate: the reschedules ViewModel declares the empty decision with
        // `emptyIfContent`, so stating it here too would be the same rule in two places, free to
        // drift. The ViewModel's is the one a test can prove.
        fetchPolicy = FetchPolicy.NETWORK_WITH_CACHE,
        ttl = AppStoreRegistry.Ttl.LOAN_GET_LOAN_RESCHEDULES,
    )

    override suspend fun loanRescheduleReasons(): LoanRescheduleTemplate? = runCatching {
        rescheduleTemplateStore
            .stream(StoreReadRequest.cached(Unit, refresh = false))
            .filterNot {
                it is StoreReadResponse.Loading ||
                    it is StoreReadResponse.NoNewData ||
                    it is StoreReadResponse.Initial
            }
            .first()
            .requireData()
    }.getOrNull()

    override suspend fun loanChargeTemplate(loanId: Int): ChargeTemplate? = runCatching {
        chargeTemplateStore
            .stream(
                StoreReadRequest.cached(
                    GetChargeTemplateKey(resourceType = LOAN_RESOURCE, resourceId = loanId),
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

    override fun loanChargesStream(
        loanId: Int,
        scope: CoroutineScope,
    ): ScreenDataStream<List<ChargesEntity>> =
        chargeStore.asScreenStream(
            key = loanId,
            cacheKey = AppCacheKeys.LoanCharges.forLoan(loanId),
            scope = scope,
            fetchPolicy = FetchPolicy.NETWORK_WITH_CACHE,
            ttl = AppStoreRegistry.Ttl.LOAN_CHARGES,
        )

    private companion object {
        /** Mirrors the store's sentinel for "no product chosen yet". */
        const val NO_PRODUCT = -1

        /**
         * The charge endpoints are shared across resource types; this repository is the LOAN one.
         * A client charge read lives on `ClientChargeRepository` with its own literal.
         */
        const val LOAN_RESOURCE = "loans"
    }
}
