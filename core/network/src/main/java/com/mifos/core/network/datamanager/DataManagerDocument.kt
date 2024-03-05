package com.mifos.core.network.datamanager

import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.noncore.Document
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This DataManager Class for Handling the every request regarding the Document.
 * Created by Rajan Maurya on 02/09/16.
 */
@Singleton
class DataManagerDocument @Inject constructor(val mBaseApiManager: BaseApiManager) {
    /**
     * This Method for Fetching the Document List with EntityType and EntityId from the REST API.
     * REST END POINT:
     * https://demo.openmf.org/fineract-provider/api/v1/{entityType}/{entityId}/documents
     *
     * @param entityType Entity Type
     * @param entityId   Entity Id
     * @return List<Document>
    </Document> */
    fun getDocumentsList(entityType: String?, entityId: Int): Observable<List<Document>> {
        return mBaseApiManager.documentApi.getDocuments(entityType, entityId)
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
    fun createDocument(
        entityType: String?, entityId: Int, name: String?,
        desc: String?, file: MultipartBody.Part?
    ): Observable<GenericResponse> {
        return mBaseApiManager
            .documentApi
            .createDocument(entityType, entityId, name, desc, file)
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
        entityType: String?, entityId: Int,
        documentId: Int
    ): Observable<ResponseBody> {
        return mBaseApiManager.documentApi.downloadDocument(entityType, entityId, documentId)
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
    fun removeDocument(
        entityType: String?, entityId: Int,
        documentId: Int
    ): Observable<GenericResponse> {
        return mBaseApiManager.documentApi.removeDocument(entityType, entityId, documentId)
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
    fun updateDocument(
        entityType: String?,
        entityId: Int,
        documentId: Int,
        name: String?,
        desc: String?,
        file: MultipartBody.Part?
    ): Observable<GenericResponse> {
        return mBaseApiManager.documentApi
            .updateDocument(entityType, entityId, documentId, name, desc, file)
    }
}