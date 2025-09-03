/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import com.mifos.core.common.utils.DataState
import com.mifos.core.network.GenericResponse
import io.ktor.client.request.forms.MultiPartFormDataContent
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 16/08/23.
 */
interface DocumentDialogRepository {

    suspend fun createDocument(
        entityType: String,
        entityId: Int,
        file: MultiPartFormDataContent,
    ): Flow<DataState<GenericResponse>>

    suspend fun updateDocument(
        entityType: String,
        entityId: Int,
        documentId: Int,
        file: MultiPartFormDataContent,
    ): Flow<DataState<GenericResponse>>
}
