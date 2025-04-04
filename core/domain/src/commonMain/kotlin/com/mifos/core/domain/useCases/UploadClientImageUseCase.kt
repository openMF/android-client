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
import com.mifos.core.data.repository.ClientDetailsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UploadClientImageUseCase(
    private val repository: ClientDetailsRepository,
) {

    operator fun invoke(id: Int, pngFile: PlatformFile): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        val body = pngFile.toMultipartData()
        repository.uploadClientImage(id, body)
        emit(Resource.Success("Image uploaded successfully"))
    }.catch { exception ->
        emit(Resource.Error("Unable to upload image: ${exception.message}"))
    }
}

expect class PlatformFile {
    fun toMultipartData(): MultipartData
}

expect class MultipartData
