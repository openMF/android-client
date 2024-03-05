package com.mifos.mifosxdroid.online.documentlist

import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerDocument
import com.mifos.core.objects.noncore.Document
import okhttp3.ResponseBody
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class DocumentListRepositoryImp @Inject constructor(private val dataManagerDocument: DataManagerDocument) :
    DocumentListRepository {
    override fun getDocumentsList(entityType: String?, entityId: Int): Observable<List<Document>> {
        return dataManagerDocument.getDocumentsList(entityType, entityId)
    }

    override fun downloadDocument(
        entityType: String?,
        entityId: Int,
        documentId: Int
    ): Observable<ResponseBody> {
        return dataManagerDocument.downloadDocument(entityType, entityId, documentId)
    }

    override fun removeDocument(
        entityType: String?,
        entityId: Int,
        documentId: Int
    ): Observable<GenericResponse> {
        return dataManagerDocument.removeDocument(entityType, entityId, documentId)
    }
}