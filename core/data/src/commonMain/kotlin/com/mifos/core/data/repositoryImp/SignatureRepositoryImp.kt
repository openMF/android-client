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

import com.mifos.core.data.repository.SignatureRepository
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerDocument
import io.ktor.client.request.forms.MultiPartFormDataContent

/**
 * Created by Arin Yadav on 04/09/25.
 */
class SignatureRepositoryImp(
    private val dataManagerDocument: DataManagerDocument,
) : SignatureRepository {

    override suspend fun createSignature(
        entityType: String,
        entityId: Int,
        file: MultiPartFormDataContent,
    ): GenericResponse {
        return dataManagerDocument.createDocument(entityType, entityId, file)
    }

    override suspend fun updateSignature(
        entityType: String,
        entityId: Int,
        documentId: Int,
        file: MultiPartFormDataContent,
    ): GenericResponse {
        return dataManagerDocument.updateDocument(entityType, entityId, documentId, file)
    }
}
