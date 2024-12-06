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

import com.mifos.core.data.repository.DocumentDialogRepository
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerDocument
import okhttp3.MultipartBody
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 16/08/23.
 */
class DocumentDialogRepositoryImp @Inject constructor(private val dataManagerDocument: DataManagerDocument) :
    DocumentDialogRepository {

    override fun createDocument(
        entityType: String?,
        entityId: Int,
        name: String?,
        desc: String?,
        file: MultipartBody.Part?,
    ): Observable<GenericResponse> {
        return dataManagerDocument.createDocument(entityType, entityId, name, desc, file)
    }

    override fun updateDocument(
        entityType: String?,
        entityId: Int,
        documentId: Int,
        name: String?,
        desc: String?,
        file: MultipartBody.Part?,
    ): Observable<GenericResponse> {
        return dataManagerDocument.updateDocument(
            entityType,
            entityId,
            documentId,
            name,
            desc,
            file,
        )
    }
}
