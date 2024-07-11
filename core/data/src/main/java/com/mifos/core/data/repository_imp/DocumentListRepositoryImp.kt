package com.mifos.core.data.repository_imp

import com.mifos.core.data.repository.DocumentListRepository
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerDocument
import com.mifos.core.objects.noncore.Document
import okhttp3.ResponseBody
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class DocumentListRepositoryImp @Inject constructor(private val dataManagerDocument: DataManagerDocument) :
    DocumentListRepository {

    override suspend fun getDocumentsList(entityType: String, entityId: Int): List<Document> {
        return dataManagerDocument.getDocumentsList(entityType, entityId)
    }

    override suspend fun downloadDocument(
        entityType: String,
        entityId: Int,
        documentId: Int
    ): ResponseBody {
        return dataManagerDocument.downloadDocument(entityType, entityId, documentId)
    }

    override suspend fun removeDocument(
        entityType: String,
        entityId: Int,
        documentId: Int
    ): GenericResponse {
        return dataManagerDocument.removeDocument(entityType, entityId, documentId)
    }
}