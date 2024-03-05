package com.mifos.mifosxdroid.online.documentlist

import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.noncore.Document
import okhttp3.ResponseBody
import rx.Observable

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface DocumentListRepository {

    fun getDocumentsList(entityType: String?, entityId: Int): Observable<List<Document>>

    fun downloadDocument(
        entityType: String?,
        entityId: Int,
        documentId: Int
    ): Observable<ResponseBody>

    fun removeDocument(
        entityType: String?,
        entityId: Int,
        documentId: Int
    ): Observable<GenericResponse>
}