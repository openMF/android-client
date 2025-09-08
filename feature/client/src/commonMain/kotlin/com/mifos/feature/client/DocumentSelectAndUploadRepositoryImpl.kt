package com.mifos.feature.client

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.FileKitUtil
import com.mifos.core.data.repository.DocumentDialogRepository
import com.mifos.core.data.repository.DocumentListRepository
import com.mifos.feature.client.EntityDocumentState.UploadType
import com.mifos.feature.client.utils.createDocumentRequestBody
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.nameWithoutExtension
import io.github.vinceglb.filekit.readBytes
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update


class DocumentSelectAndUploadRepositoryImpl (
    private val documentsRepository: DocumentListRepository,
    private val documentDialogRepository: DocumentDialogRepository,
): DocumentSelectAndUploadRepository {
    override val entityDocumentStateMutableStateFlow = MutableStateFlow(EntityDocumentState())

    override val state = entityDocumentStateMutableStateFlow.value

    override suspend fun selectImageFromGallery(
        dialogTitle: String
    ): Result<Unit> = runCatching {
        FileKitUtil.pickImage(dialogTitle).collect {dataState ->
            when(dataState){
                is DataState.Error<*> -> {
                    throw dataState.exception
                }
                DataState.Loading -> {
                    setLoading(true)
                }
                is DataState.Success<*> -> {
                    dataState.data?.let {platformFile ->
                        FileKitUtil.writeFileToCache(
                            platformFile.nameWithoutExtension,
                            platformFile.extension,
                            platformFile.readBytes()
                        ).collect {
                            when(it){
                                is DataState.Error<*> -> {
                                    setLoading(false)
                                    throw  it.exception
                                }
                                DataState.Loading -> { }
                                is DataState.Success<*> -> {
                                    entityDocumentStateMutableStateFlow.update {
                                        it.copy(entityDocument = platformFile,)
                                    }
                                    setLoading(false)
                                }
                            }
                        }
                    } ?: setLoading(false)
                }
            }
        }
    }
    override suspend fun selectImageFromFile(dialogTitle: String): Result<Unit> = runCatching {
        FileKitUtil.pickPdfFile(dialogTitle).collect {dataState ->
            when(dataState){
                is DataState.Error<*> -> {
                    throw dataState.exception
                }
                DataState.Loading -> {
                    setLoading(true)
                }
                is DataState.Success<*> -> {
                    dataState.data?.let {platformFile ->
                        FileKitUtil.writeFileToCache(
                            platformFile.nameWithoutExtension,
                            platformFile.extension,
                            platformFile.readBytes()
                        ).collect {
                            when(it){
                                is DataState.Error<*> -> {
                                    setLoading(false)
                                    throw  it.exception
                                }
                                DataState.Loading -> { }
                                is DataState.Success<*> -> {
                                    entityDocumentStateMutableStateFlow.update {
                                        it.copy(entityDocument = platformFile,)
                                    }
                                    setLoading(false)
                                }
                            }
                        }
                    } ?: setLoading(false)
                }
            }
        }
    }


    override suspend fun downloadDocumentAndSaveToAppCache() = runCatching {
        setLoading(true)
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

        FileKitUtil.writeFileToCache(
            "attachment",
            extension,
            byte,
        ).collect {
            when(it){
                is DataState.Error<*> -> {
                    setLoading(false)
                    throw  it.exception
                }
                DataState.Loading -> { }
                is DataState.Success<*> -> {
                    setLoading(false)
                    entityDocumentStateMutableStateFlow.update {
                        it.copy(
                            entityDocument = FileKitUtil.appCache/"attachment.${extension}",
                            uploadType = UploadType.Update
                        )
                    }
                }
            }
        }
    }

    override suspend fun deleteDocument() = runCatching {
        setLoading(true)
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
        runCatching {
            emit(DataState.Loading)
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
            when (result) {
                is DataState.Error<*> -> throw result.exception
                DataState.Loading -> {
                    DataState.Loading
                }
                is DataState.Success<*> -> {
                    DataState.Success(result.data)
                }
            }

        }.onFailure {
            emit(DataState.Error(it))
        }.onSuccess {
           emit(it)
        }
    }

    override fun updateDocument(
        documentName: String,
        description: String,
    ) = flow {
        emit(DataState.Loading)
        runCatching {
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
            when (result) {
                is DataState.Error<*> -> throw result.exception
                DataState.Loading -> {
                    DataState.Loading
                }
                is DataState.Success<*> -> {
                    DataState.Success(result.data)
                }
            }

        }.onFailure {
            emit(DataState.Error(it))
        }.onSuccess {
            emit(it)
        }
    }

    suspend fun getMultiPartFormDataContent(
        documentName: String,
        description: String,
    ): MultiPartFormDataContent {
        return try {
            createDocumentRequestBody(
                documentFile = state.entityDocument!!,
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
