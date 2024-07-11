package com.mifos.core.data.repository

import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.noncore.Document
import okhttp3.ResponseBody

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface DocumentListRepository {

    suspend fun getDocumentsList(entityType: String, entityId: Int): List<Document>

    suspend fun downloadDocument(
        entityType: String,
        entityId: Int,
        documentId: Int
    ): ResponseBody

    suspend fun removeDocument(
        entityType: String,
        entityId: Int,
        documentId: Int
    ): GenericResponse
}