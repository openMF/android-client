package com.mifos.feature.client

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.FileKitUtil
import com.mifos.core.data.repository.DocumentDialogRepository
import com.mifos.core.data.repository.DocumentListRepository
import com.mifos.feature.client.utils.createDocumentRequestBody
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.nameWithoutExtension
import io.github.vinceglb.filekit.readBytes
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update


class DocumentSelectAndUploadRepositoryImpl (
    private val documentsRepository: DocumentListRepository,
    private val documentDialogRepository: DocumentDialogRepository,
): DocumentSelectAndUploadRepository {
    override val entityDocumentStateMutableStateFlow = MutableStateFlow(EntityDocumentState())

    override val entityDocumentState = entityDocumentStateMutableStateFlow.value

    override suspend fun selectImageFromGallery(
        dialogTitle: String
    ): Result<Unit> = runCatching {
        val pickResult = FileKitUtil.pickImage(dialogTitle)
            .first { it !is DataState.Loading }

        val platformFile = when (pickResult) {
            is DataState.Success -> pickResult.data ?: throw IllegalStateException("Picker succeeded but returned no data.")
            is DataState.Error -> throw pickResult.exception
            DataState.Loading -> error("Unreachable")
        }

        val writeResult = FileKitUtil.writeFileToCache(
            platformFile.nameWithoutExtension,
            platformFile.extension,
            platformFile.readBytes()
        ).first { it !is DataState.Loading }

        when (writeResult) {
            is DataState.Error -> throw writeResult.exception
            is DataState.Success -> {
                entityDocumentStateMutableStateFlow.update {
                    it.copy(entityDocument = platformFile,)
                }
            }
            DataState.Loading -> error("Unreachable")
        }
    }
    override suspend fun selectImageFromFile(dialogTitle: String): Result<Unit> = runCatching {
        val pickResult = FileKitUtil.pickPdfFile(dialogTitle)
            .first { it !is DataState.Loading }

        val platformFile = when (pickResult) {
            is DataState.Success -> pickResult.data ?: throw IllegalStateException("Picker succeeded but returned no data.")
            is DataState.Error -> throw pickResult.exception
            DataState.Loading -> error("Unreachable")
        }

        val writeResult = FileKitUtil.writeFileToCache(
            platformFile.nameWithoutExtension,
            platformFile.extension,
            platformFile.readBytes()
        ).first { it !is DataState.Loading }

        when (writeResult) {
            is DataState.Error -> throw writeResult.exception
            is DataState.Success -> {
                entityDocumentStateMutableStateFlow.update {
                    it.copy(entityDocument = platformFile,)
                }
            }
            DataState.Loading -> error("Unreachable")
        }
    }


    override suspend fun downloadDocumentAndSaveToAppCache() = runCatching {
        val state = entityDocumentStateMutableStateFlow.first()
        val response = documentsRepository.downloadDocument(
            entityType = when (state.entityType) {
                EntityDocumentState.EntityType.Clients -> "clients"
                EntityDocumentState.EntityType.Loans -> "loans"
            },
            entityId = state.entityId,
            documentId = state.documentId,
        )
        val byte = response.readRawBytes()
        val extension = response.headers["Content-Type"]?.split('/')?.last()
            ?: throw Exception("Failed to get document type")

        val writeResult = FileKitUtil.writeFileToCache(
            "attachment",
            extension,
            byte,
        ).first{it !is DataState.Loading }

        when (writeResult) {
            is DataState.Error -> throw writeResult.exception
            is DataState.Success -> {
                entityDocumentStateMutableStateFlow.update {
                    it.copy(entityDocument = FileKitUtil.appCache/"attachment.${extension}")
                }
            }
            DataState.Loading -> error("Unreachable")
        }
    }

    override suspend fun deleteDocument() = runCatching {
        setLoading(true)
        val state = entityDocumentStateMutableStateFlow.first()

        documentsRepository.removeDocument(
            entityType = when (state.entityType) {
                EntityDocumentState.EntityType.Clients -> "clients"
                EntityDocumentState.EntityType.Loans -> "loans"
            },
            entityId = state.entityId,
            documentId = state.documentId,
        )
        setLoading(false)
    }

    override fun uploadDocument(
        documentName: String,
        description: String,
    ) = flow {

        emit(DataState.Loading)
        try {
            val state = entityDocumentStateMutableStateFlow.first()

            val multiPartFormDataContent = getMultiPartFormDataContent(
                documentName, description
            )
            val result = documentDialogRepository.createDocument(
                entityType = when(state.entityType){
                    EntityDocumentState.EntityType.Clients -> "clients"
                    EntityDocumentState.EntityType.Loans -> "loans"
                },
                entityId = state.entityId,
                file = multiPartFormDataContent
            )
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
                documentName, description
            )
            val result = documentDialogRepository.updateDocument(
                entityType = when(state.entityType){
                    EntityDocumentState.EntityType.Clients -> "clients"
                    EntityDocumentState.EntityType.Loans -> "loans"
                },
                entityId = state.entityId,
                documentId = state.documentId,
                file = multiPartFormDataContent
            )
            emit(result)
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }

    suspend fun getMultiPartFormDataContent(
        documentName: String,
        description: String,
    ): MultiPartFormDataContent {
        return try {
            val state = entityDocumentStateMutableStateFlow.first()
            createDocumentRequestBody(
                documentFile = state.entityDocument ?: throw IllegalStateException("Document not found"),
                name = documentName,
                description = description,
            )
        } catch (e: Exception) {
            throw e
        }
    }

    private fun setLoading(loading: Boolean) {
        entityDocumentStateMutableStateFlow.update {
            it.copy(
                isLoading = loading,
            )
        }
    }

}
