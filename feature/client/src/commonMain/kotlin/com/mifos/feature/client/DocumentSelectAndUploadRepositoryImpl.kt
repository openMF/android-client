/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.default_preview_pdf_name
import androidclient.feature.client.generated.resources.error_document_not_found
import androidclient.feature.client.generated.resources.error_failed_to_get_document_type
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.FileKitUtil
import com.mifos.core.data.repository.DocumentCreateUpdateRepository
import com.mifos.core.data.repository.DocumentListRepository
import com.mifos.feature.client.utils.createDocumentRequestBody
import io.github.vinceglb.filekit.PlatformFile
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.statement.readRawBytes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import org.jetbrains.compose.resources.getString

class DocumentSelectAndUploadRepositoryImpl(
    private val documentsRepository: DocumentListRepository,
    private val documentDialogRepository: DocumentCreateUpdateRepository,
) : DocumentSelectAndUploadRepository {
    override val entityDocumentStateMutableStateFlow = MutableStateFlow(EntityDocumentState())

    override fun selectImageFromGallery(
        dialogTitle: String,
    ) = FileKitUtil.pickImage(dialogTitle)

    override fun selectDocumentFromFile(dialogTitle: String) = FileKitUtil.pickFile(dialogTitle)

    override fun downloadDocumentAndCache() = flow {
        emit(DataState.Loading)
        val state = entityDocumentStateMutableStateFlow.first()
        val response = documentsRepository.downloadDocument(
            entityType = when (state.entityType) {
                EntityDocumentState.EntityType.Clients -> "clients"
                EntityDocumentState.EntityType.Loans -> "loans"
            },
            entityId = state.entityId,
            documentId = state.documentId,
        ).first { it !is DataState.Loading }

        if (response is DataState.Error) {
            emit(DataState.Error(Exception(response.message)))
            return@flow
        } else {
            response.data?.let { httpResponse ->
                val byte = httpResponse.readRawBytes()
                val extension = httpResponse.headers["Content-Type"]?.split('/')?.last()
                    ?: throw Exception(getString(Res.string.error_failed_to_get_document_type))
                FileKitUtil.writeFileToCache(
                    getString(Res.string.default_preview_pdf_name),
                    extension,
                    byte,
                ).collect { writeState ->
                    if (writeState !is DataState.Loading) {
                        emit(writeState)
                    }
                }
            } ?: emit(DataState.Error(Exception("Received null data.")))
        }
    }

    override suspend fun deleteDocument() = runCatching {
        val state = entityDocumentStateMutableStateFlow.first()

        documentsRepository.removeDocument(
            entityType = when (state.entityType) {
                EntityDocumentState.EntityType.Clients -> "clients"
                EntityDocumentState.EntityType.Loans -> "loans"
            },
            entityId = state.entityId,
            documentId = state.documentId,
        )
    }

    override fun uploadDocument(
        documentName: String,
        description: String,
    ) = flow {
        emit(DataState.Loading)
        try {
            val state = entityDocumentStateMutableStateFlow.first()

            val multiPartFormDataContent = getMultiPartFormDataContent(
                documentName,
                description,
            )
            val result = documentDialogRepository.createDocument(
                entityType = when (state.entityType) {
                    EntityDocumentState.EntityType.Clients -> "clients"
                    EntityDocumentState.EntityType.Loans -> "loans"
                },
                entityId = state.entityId,
                file = multiPartFormDataContent,
            ).first { it !is DataState.Loading }
            emit(result)
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }

    override fun updateDocument(
        documentName: String,
        description: String,
    ) = flow {
        emit(DataState.Loading)
        try {
            val state = entityDocumentStateMutableStateFlow.first()

            val multiPartFormDataContent = getMultiPartFormDataContent(
                documentName,
                description,
            )
            val result = documentDialogRepository.updateDocument(
                entityType = when (state.entityType) {
                    EntityDocumentState.EntityType.Clients -> "clients"
                    EntityDocumentState.EntityType.Loans -> "loans"
                },
                entityId = state.entityId,
                documentId = state.documentId,
                file = multiPartFormDataContent,
            ).first { it !is DataState.Loading }
            emit(result)
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }

    private suspend fun getMultiPartFormDataContent(
        documentName: String,
        description: String,
    ): MultiPartFormDataContent {
        return try {
            val state = entityDocumentStateMutableStateFlow.first()
            createDocumentRequestBody(
                documentFile = state.entityDocument ?: throw IllegalStateException(
                    getString(Res.string.error_document_not_found),
                ),
                name = documentName,
                description = description,
            )
        } catch (e: Exception) {
            throw e
        }
    }

    override fun updateEntityDocument(platformFile: PlatformFile) {
        entityDocumentStateMutableStateFlow.update {
            it.copy(entityDocument = platformFile)
        }
    }

    override fun updateStep(step: EntityDocumentState.Step) {
        entityDocumentStateMutableStateFlow.update {
            it.copy(step = step)
        }
    }

    override fun changeSubmitMode(submitMode: EntityDocumentState.SubmitMode) {
        entityDocumentStateMutableStateFlow.update {
            it.copy(submitMode = submitMode)
        }
    }

    override fun resetStateAndRefresh() {
        entityDocumentStateMutableStateFlow.update {
            it.copy(
                entityType = EntityDocumentState.EntityType.Clients,
                doARefresh = true,
                entityDocument = null,
                submitMode = EntityDocumentState.SubmitMode.UPLOAD,
                documentPreviewedAndAccepted = false,
                step = EntityDocumentState.Step.ADD,
            )
        }
    }

    override fun resetRefreshState() {
        entityDocumentStateMutableStateFlow.update {
            it.copy(doARefresh = false)
        }
    }
}
