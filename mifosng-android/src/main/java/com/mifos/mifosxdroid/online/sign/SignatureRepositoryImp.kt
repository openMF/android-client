package com.mifos.mifosxdroid.online.sign

import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerDocument
import okhttp3.MultipartBody
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class SignatureRepositoryImp @Inject constructor(private val dataManagerDocument: DataManagerDocument) :
    SignatureRepository {

    override fun createDocument(
        entityType: String?,
        entityId: Int,
        name: String?,
        desc: String?,
        file: MultipartBody.Part?
    ): Observable<GenericResponse> {
        return dataManagerDocument.createDocument(entityType, entityId, name, desc, file)
    }


}