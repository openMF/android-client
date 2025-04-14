/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.document.documentDialog

import androidx.lifecycle.ViewModel
import com.mifos.core.data.repository.DocumentDialogRepository
import com.mifos.core.network.GenericResponse
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.util.rootCause
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.plugins.RxJavaPlugins
import rx.schedulers.Schedulers
import java.io.File

/**
 * Created by Aditya Gupta on 16/08/23.
 */

class DocumentDialogViewModel(
    private val repository: DocumentDialogRepository,
) : ViewModel() {

    private val _documentDialogUiState =
        MutableStateFlow<DocumentDialogUiState>(DocumentDialogUiState.Initial)

    val documentDialogUiState: StateFlow<DocumentDialogUiState>
        get() = _documentDialogUiState

    @OptIn(InternalAPI::class)
    fun createDocument(type: String?, id: Int, name: String?, desc: String?, file: File) {
        _documentDialogUiState.value = DocumentDialogUiState.ShowProgressbar
        repository
            .createDocument(type, id, name, desc, getRequestFileBody(file))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                object : Subscriber<GenericResponse>() {
                    override fun onCompleted() {
                    }

                    override fun onError(e: Throwable) {
                        try {
                            when (e) {
                                is ClientRequestException, is ServerResponseException -> {
                                    _documentDialogUiState.value = DocumentDialogUiState.ShowUploadError(e.message ?: "Server error occurred")
                                }
                                is IOException -> {
                                    _documentDialogUiState.value = DocumentDialogUiState.ShowError(e.rootCause?.message ?: "Network error occurred")
                                }
                                is SerializationException -> {
                                    _documentDialogUiState.value = DocumentDialogUiState.ShowError("Data parsing error")
                                }
                                else -> {
                                    _documentDialogUiState.value = DocumentDialogUiState.ShowError(e.rootCause?.message ?: "Unknown error")
                                }
                            }
                        } catch (throwable: Throwable) {
                            RxJavaPlugins.getInstance().errorHandler
                                .handleError(throwable)
                        }
                    }

                    override fun onNext(genericResponse: GenericResponse) {
                        _documentDialogUiState.value =
                            DocumentDialogUiState.ShowDocumentedCreatedSuccessfully(genericResponse)
                    }
                },
            )
    }

    fun updateDocument(
        entityType: String?,
        entityId: Int,
        documentId: Int,
        name: String?,
        desc: String?,
        file: File,
    ) {
        _documentDialogUiState.value = DocumentDialogUiState.ShowProgressbar
        repository.updateDocument(
            entityType,
            entityId,
            documentId,
            name,
            desc,
            getRequestFileBody(file),
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                object : Subscriber<GenericResponse>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        _documentDialogUiState.value =
                            DocumentDialogUiState.ShowError(e.message.toString())
                    }

                    override fun onNext(genericResponse: GenericResponse) {
                        _documentDialogUiState.value =
                            DocumentDialogUiState.ShowDocumentUpdatedSuccessfully(genericResponse)
                    }
                },
            )
    }

    private fun getRequestFileBody(file: File): PartData {
        // create RequestBody instance from file
        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())

        // PartData is used to send also the actual file name
        return PartData.createFormData("file", file.name, requestFile)
    }
}
