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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import java.io.File

/**
 * Created by Aditya Gupta on 18/03/24.
 */

class UploadClientImageUseCase(
    private val repository: ClientDetailsRepository,
) {

    operator fun invoke(id: Int, pngFile: File): Flow<Resource<ResponseBody>> = flow {
        try {
            emit(Resource.Loading())
            val requestFile = pngFile.asRequestBody("image/png".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", pngFile.name, requestFile)
            repository.uploadClientImage(id, body)
            emit(Resource.Success(ResponseBody.create(null, "success")))
        } catch (e: Exception) {
            emit(Resource.Error("Unable to update image: ${e.message}"))
        }
    }
}
