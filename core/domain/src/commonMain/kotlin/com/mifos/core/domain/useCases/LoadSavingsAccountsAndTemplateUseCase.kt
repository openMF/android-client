/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.domain.useCases

import com.mifos.core.data.repository.SavingsAccountRepository
import com.mifos.room.entities.zipmodels.SavingProductsAndTemplate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Created by Pronay Sarker on 04/08/2024 (4:41 PM)
 */
class LoadSavingsAccountsAndTemplateUseCase(
    private val repository: SavingsAccountRepository,
) {

    // offline-first-template-migration 03-core-datastate-removal (D18): the two upstream Flows
    // are now plain — `combine` propagates either's exception through this Flow's own exception
    // channel automatically, so no manual Success/Error/Loading branching is needed.
    operator fun invoke(): Flow<SavingProductsAndTemplate> =
        combine(
            repository.getSavingsAccounts(),
            repository.getSavingsAccountTemplate(),
        ) { savingsAccount, template ->
            SavingProductsAndTemplate(
                mProductSavings = savingsAccount,
                mSavingProductsTemplate = template,
            )
        }
}
