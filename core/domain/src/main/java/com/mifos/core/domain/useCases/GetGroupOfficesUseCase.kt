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
import com.mifos.core.data.repository.CreateNewGroupRepository
import com.mifos.room.entities.organisation.OfficeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Created by Pronay Sarker on 01/08/2024 (8:23 AM)
 */

class GetGroupOfficesUseCase @Inject constructor(private val repository: CreateNewGroupRepository) {

    suspend operator fun invoke(): Flow<Resource<List<OfficeEntity>>> = flow {
        try {
            emit(Resource.Loading())
            val response = repository.offices()
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }
}
