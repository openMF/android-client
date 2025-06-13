/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.SignatureRepository
import com.mifos.core.network.datamanager.DataManagerDocument
import io.ktor.client.request.forms.MultiPartFormDataContent

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class SignatureRepositoryImp(
    private val dataManagerDocument: DataManagerDocument,
) : SignatureRepository {

    override suspend fun createDocument(
        entityType: String,
        entityId: Int,
        file: MultiPartFormDataContent,
    ): DataState<Unit> {
        return dataManagerDocument.createDocument(entityType, entityId, file)
    }
}
