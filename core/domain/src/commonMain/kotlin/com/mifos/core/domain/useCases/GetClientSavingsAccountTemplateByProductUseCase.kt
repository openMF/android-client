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
import com.mifos.room.entities.templates.savings.SavingProductsTemplate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by Pronay Sarker on 04/08/2024 (11:59 AM)
 */
class GetClientSavingsAccountTemplateByProductUseCase(
    private val repository: SavingsAccountRepository,
) {

    operator fun invoke(clientId: Int, productId: Int): Flow<Resource<SavingProductsTemplate?>> = flow {
        try {
            emit(Resource.Loading())
            val response = repository.getClientSavingsAccountTemplateByProduct(clientId, productId)
            emit(Resource.Success(response))
        } catch (exception: Exception) {
            emit(Resource.Error(MFErrorParser.errorMessage(exception)))
        }
    }
}
