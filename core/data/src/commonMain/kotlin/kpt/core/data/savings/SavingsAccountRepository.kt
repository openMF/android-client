/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.data.savings

import kotlinx.coroutines.CoroutineScope
import kpt.core.base.store.screen.ScreenDataStream
import kpt.core.database.savings.entity.SavingProductsTemplate
import kpt.core.database.savings.entity.SavingsAccountTransactionTemplateEntity
import kpt.core.database.savings.entity.SavingsAccountWithAssociationsEntity
import kpt.core.model.objects.organisations.ProductSavings

interface SavingsAccountRepository {
    /**
     * One savings account with its transactions.
     *
     * [savingsAccountType] is a Fineract path segment ("savingsaccounts",
     * "recurringdepositaccounts", …), so the same numeric id means different accounts under
     * different types — both are required to address one.
     */
    fun savingsAccountStream(
        savingsAccountType: String,
        savingsAccountId: Int,
        scope: CoroutineScope,
    ): ScreenDataStream<SavingsAccountWithAssociationsEntity>

    /**
     * The transaction template for one (account, command) pair — its payment-type options.
     *
     * A one-shot, for the same reason the loan and group-loan templates are: the deposit and
     * withdraw screens each ask once on open, and a [ScreenDataStream] is keyed at CONSTRUCTION.
     * `null` when the read fails, so the form can still be submitted — Fineract accepts a
     * transaction with no payment type, and refusing to let an officer record cash they have
     * already taken because a reference list did not load would be the worse failure.
     */
    suspend fun transactionTemplate(
        savingsAccountType: String,
        savingsAccountId: Int,
        transactionType: String,
    ): SavingsAccountTransactionTemplateEntity?

    /** The tenant's savings products — what the create wizard opens on. */
    fun savingsProductsStream(scope: CoroutineScope): ScreenDataStream<List<ProductSavings>>

    /**
     * The savings-application template for one (owner, product) pair.
     *
     * [isGroup] selects Fineract's GROUP endpoint over the client one. Both exist and both are
     * cached; the original's wizard read neither per-owner — its route carried `groupId` and
     * `isGroupAccount` and the ViewModel dropped both, so the group path could not work at all.
     *
     * A one-shot for the same reason the loan templates are: the wizard re-asks whenever the
     * officer changes product, and a [ScreenDataStream] is keyed at construction.
     */
    suspend fun savingsApplicationTemplate(
        ownerId: Int,
        productId: Int,
        isGroup: Boolean,
    ): SavingProductsTemplate?
}
