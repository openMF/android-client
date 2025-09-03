/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.datamanager

import com.mifos.core.model.objects.noncoreobjects.Document
import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.GenericResponse
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

/**
 * This DataManager Class for Handling the every request regarding the Document.
 * Created by Rajan Maurya on 02/09/16.
 */
class DataManagerDocument(val mBaseApiManager: BaseApiManager) {
    /**
     * This Method for Fetching the Document List with EntityType and EntityId from the REST API.
     * REST END POINT:
     * https://demo.openmf.org/fineract-provider/api/v1/{entityType}/{entityId}/documents
     *
     * @param entityType Entity Type
     * @param entityId   Entity Id
     * @return List<Document>
     </Document> */
    fun getDocumentsList(entityType: String, entityId: Int): Flow<List<Document>> {
        return mBaseApiManager.documentService.getDocuments(entityType, entityId)
    }

    /**
     * This Method for Creating the Document with EntityType and EntityId to server.
     * REST END POINT:
     * https://demo.openmf.org/fineract-provider/api/v1/{entityType}/{entityId}/documents
     *
     * @param entityType Entity Type
     * @param entityId   Entity Id
     * @param name       Name of Document
     * @param desc       Description of Document
     * @param file       Document File
     * @return GenericResponse
     */
    suspend fun createDocument(
        entityType: String,
        entityId: Int,
        file: MultiPartFormDataContent,
    ): GenericResponse {
        return mBaseApiManager.documentService.createDocument(entityType, entityId, file)
    }

    /**
     * This Method to Retrieve Binary File associated with Document.
     * REST END POINT:
     * https://demo.openmf.org/fineract-provider/api/v1/{entityType}/{entityId}/documents/
     * {documentId}/attachment
     *
     * @param entityType Entity Type
     * @param entityId   Entity Id
     * @param documentId Document Id
     * @return ResponseBody Binary File of Document
     */
    fun downloadDocument(
        entityType: String,
        entityId: Int,
        documentId: Int,
    ): Flow<HttpResponse> {
        return mBaseApiManager.documentService.downloadDocument(entityType, entityId, documentId)
    }

    /**
     * This Method to Remove the Document from the server with EntityType and EntityId and
     * Document Id.
     * REST END POINT:
     * https://demo.openmf.org/fineract-provider/api/v1/{entityType}/{entityId}/documents/
     * {documentId}
     *
     * @param entityType Entity Type
     * @param entityId   Entity Id
     * @param documentId Document Id
     * @return GenericResponse
     */
    suspend fun removeDocument(
        entityType: String,
        entityId: Int,
        documentId: Int,
    ): GenericResponse {
        return mBaseApiManager.documentService.removeDocument(entityType, entityId, documentId)
    }

    /**
     * This Method for Updating the document with EntityType and EntityId and Document Id
     * to the new one.
     * REST END POINT:
     * https://demo.openmf.org/fineract-provider/api/v1/{entityType}/{entityId}/documents/
     * {documentId}
     *
     * @param entityType Entity Type
     * @param entityId   Entity Id
     * @param documentId Document Id
     * @param name       Name of Document
     * @param desc       Description of Document
     * @param file       Document File
     * @return GenericResponse
     */
    suspend fun updateDocument(
        entityType: String,
        entityId: Int,
        documentId: Int,
        file: MultiPartFormDataContent,
    ): GenericResponse {
        return mBaseApiManager.documentService
            .updateDocument(entityType, entityId, documentId, file)
    }
}
