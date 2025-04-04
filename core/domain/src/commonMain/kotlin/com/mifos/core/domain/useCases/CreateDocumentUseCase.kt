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
import com.mifos.core.data.repository.SignatureRepository
import com.mifos.core.network.GenericResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody

class CreateDocumentUseCase(
    private val repository: SignatureRepository,
) {

    operator fun invoke(
        entityType: String?,
        entityId: Int,
        name: String?,
        desc: String?,
        file: MultipartBody.Part?,
    ): Flow<Resource<GenericResponse>> = flow {
        try {
            emit(Resource.Loading())
            val response = repository.createDocument(entityType, entityId, name, desc, file)
            emit(Resource.Success(response))
        } catch (exception: Exception) {
            emit(Resource.Error(MFErrorParser.errorMessage(exception)))
        }
    }
}
