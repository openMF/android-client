package com.mifos.mifosxdroid.dialogfragments.documentdialog

import com.mifos.core.network.GenericResponse
import okhttp3.MultipartBody
import rx.Observable

/**
 * Created by Aditya Gupta on 16/08/23.
 */
interface DocumentDialogRepository {

    fun createDocument(
        entityType: String?, entityId: Int, name: String?,
        desc: String?, file: MultipartBody.Part?
    ): Observable<GenericResponse>

    fun updateDocument(
        entityType: String?,
        entityId: Int,
        documentId: Int,
        name: String?,
        desc: String?,
        file: MultipartBody.Part?
    ): Observable<GenericResponse>
}