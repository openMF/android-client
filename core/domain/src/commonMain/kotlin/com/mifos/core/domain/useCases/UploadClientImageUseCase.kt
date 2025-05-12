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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by Aditya Gupta on 18/03/24.
 */

class UploadClientImageUseCase(
    private val repository: ClientDetailsRepository,
) {

    operator fun invoke(id: Int, image: String): Flow<DataState<String>> = flow {
        try {
            emit(DataState.Loading)
            repository.uploadClientImage(id, image)
            DataState.Success("Client image uploaded successfully")
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }
}
