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

import com.google.gson.JsonArray
import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.DataTableDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetDataTableInfoUseCase @Inject constructor(private val repository: DataTableDataRepository) {

    suspend operator fun invoke(table: String, entityId: Int): Flow<Resource<JsonArray>> = flow {
        try {
            emit(Resource.Loading())
            val data = repository.getDataTableInfo(table, entityId)
            emit(Resource.Success(data))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }
}
