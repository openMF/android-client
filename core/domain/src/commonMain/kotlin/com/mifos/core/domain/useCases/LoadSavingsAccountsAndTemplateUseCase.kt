/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.domain.useCases

import com.mifos.core.common.utils.DataState
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

    operator fun invoke(): Flow<DataState<SavingProductsAndTemplate>> =
        combine(
            repository.getSavingsAccounts(),
            repository.getSavingsAccountTemplate(),
        ) { savingsAccount, template ->

            if (savingsAccount is DataState.Success && template is DataState.Success) {
                DataState.Success(
                    SavingProductsAndTemplate(
                        mProductSavings = savingsAccount.data,
                        mSavingProductsTemplate = template.data,
                    ),
                )
            } else if (savingsAccount is DataState.Error || template is DataState.Error) {
                val exception = (savingsAccount as? DataState.Error)?.exception
                    ?: (template as? DataState.Error)?.exception
                    ?: Exception("Unknown error")
                DataState.Error(exception)
            } else {
                DataState.Loading
            }
        }
}
