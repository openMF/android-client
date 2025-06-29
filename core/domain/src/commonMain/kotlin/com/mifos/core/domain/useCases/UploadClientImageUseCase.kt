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

import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.ClientDetailsRepository
import core.domain.generated.resources.Res
import core.domain.generated.resources.core_domain_client_image_uploaded_successfully
import io.ktor.client.request.forms.MultiPartFormDataContent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.resources.getString

/**
 * Created by Aditya Gupta on 18/03/24.
 */

class UploadClientImageUseCase(
    private val repository: ClientDetailsRepository,
) {

    operator fun invoke(id: Int, image: MultiPartFormDataContent): Flow<DataState<String>> = flow {
        emit(DataState.Loading)

        try {
            repository.uploadClientImage(id, image)
            emit(DataState.Success(getString(Res.string.core_domain_client_image_uploaded_successfully)))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }
}
