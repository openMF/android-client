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

import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.LoanAccountRepository
import com.mifos.room.entities.templates.loans.LoanTemplate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class GetLoansAccountTemplateUseCase(
    private val loanAccountRepository: LoanAccountRepository,
) {

    operator fun invoke(clientId: Int, productId: Int): Flow<Resource<LoanTemplate>> =
        callbackFlow {
            try {
                trySend(Resource.Loading())
                val response = loanAccountRepository.getLoansAccountTemplate(clientId, productId)
                trySend(Resource.Success(response))
            } catch (exception: Exception) {
                send(Resource.Error(exception.message.toString()))
            }
        }
}
