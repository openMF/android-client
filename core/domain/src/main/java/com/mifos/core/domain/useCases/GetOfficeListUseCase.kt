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
import com.mifos.core.data.repository.NewIndividualCollectionSheetRepository
import com.mifos.room.entities.organisation.OfficeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetOfficeListUseCase @Inject constructor(private val repository: NewIndividualCollectionSheetRepository) {

    suspend operator fun invoke(): Flow<Resource<List<OfficeEntity>>> = flow {
        try {
            emit(Resource.Loading())
            emit(Resource.Success(repository.offices()))
        } catch (exception: Exception) {
            emit(Resource.Error(exception.message.toString()))
        }
    }
}
