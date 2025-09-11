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
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.DocumentListRepository
import com.mifos.core.model.objects.noncoreobjects.Document
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerDocument
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class DocumentListRepositoryImp(
    private val dataManagerDocument: DataManagerDocument,
) : DocumentListRepository {

    override fun getDocumentsList(entityType: String, entityId: Int): Flow<DataState<List<Document>>> {
        return dataManagerDocument.getDocumentsList(entityType, entityId)
            .asDataStateFlow()
    }

    override fun downloadDocument(
        entityType: String,
        entityId: Int,
        documentId: Int,
    ): Flow<DataState<HttpResponse>> {
        return dataManagerDocument.downloadDocument(entityType, entityId, documentId).asDataStateFlow()
    }

    override suspend fun removeDocument(
        entityType: String,
        entityId: Int,
        documentId: Int,
    ): GenericResponse {
        return dataManagerDocument.removeDocument(entityType, entityId, documentId)
    }
}
