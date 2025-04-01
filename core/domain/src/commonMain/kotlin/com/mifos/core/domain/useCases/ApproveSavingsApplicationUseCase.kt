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
import com.mifos.core.data.repository.SavingsAccountApprovalRepository
import com.mifos.core.model.objects.account.loan.SavingsApproval
import com.mifos.core.network.GenericResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by Pronay Sarker on 04/08/2024 (12:46 PM)
 */
class ApproveSavingsApplicationUseCase(
    private val repository: SavingsAccountApprovalRepository,
) {

    operator fun invoke(savingsAccountId: Int, savingsApproval: SavingsApproval?): Flow<Resource<GenericResponse>> =
        flow {
            emit(Resource.Loading())
            val response = repository.approveSavingsApplication(savingsAccountId, savingsApproval)
            emit(Resource.Success(response))
        }.catch { exception ->
            emit(Resource.Error(exception.message.toString()))
        }
}
