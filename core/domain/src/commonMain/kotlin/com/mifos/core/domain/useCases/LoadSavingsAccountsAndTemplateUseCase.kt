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

import com.mifos.core.common.utils.MFErrorParser
import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.SavingsAccountRepository
import com.mifos.room.entities.zipmodels.SavingProductsAndTemplate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by Pronay Sarker on 04/08/2024 (4:41 PM)
 */
class LoadSavingsAccountsAndTemplateUseCase(
    private val repository: SavingsAccountRepository,
) {

    operator fun invoke(): Flow<Resource<SavingProductsAndTemplate?>> =
        flow {
            try {
                emit(Resource.Loading())

                val savingProductsAndTemplate = coroutineScope {
                    val savingsAccount = async { repository.savingsAccounts() }
                    val template = async { repository.savingsAccountTemplate() }

                    SavingsProductsAndTemplate(
                        savingsAccount = savingsAccount.await(),
                        template = template.await(),
                    )
                }
                emit(Resource.Success(savingProductsAndTemplate))
            } catch (exception: Exception) {
                emit(Resource.Error(MFErrorParser.errorMessage(exception)))
            }
        }
}
