package com.mifos.mifosxdroid.online.sign

import com.mifos.core.network.GenericResponse
import okhttp3.MultipartBody
import rx.Observable

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface SignatureRepository {

    fun createDocument(
        entityType: String?, entityId: Int, name: String?,
        desc: String?, file: MultipartBody.Part?
    ): Observable<GenericResponse>

}