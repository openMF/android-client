package com.mifos.feature.client

import com.mifos.core.common.utils.DataState
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow


interface DocumentSelectAndUploadRepository {
    val entityDocumentStateMutableStateFlow: MutableStateFlow<EntityDocumentState>

    suspend fun selectImageFromGallery(
        dialogTitle: String = ""
    ): Result<Unit>

    suspend fun selectImageFromFile(dialogTitle: String =""): Result<Unit>

    suspend fun downloadDocumentAndSaveToAppCache():Result<Unit>

    suspend fun deleteDocument(): Result<Unit>

    fun uploadDocument(
        documentName: String,
        description: String,
    ): Flow<DataState<Unit?>>

    fun updateDocument(
        documentName: String,
        description: String,
    ): Flow<DataState<Unit?>>


    fun resetState()
}


data class EntityDocumentState(
    val entityId: Int = -1,
    val documentId: Int = -1,
    val entityType: EntityType = EntityType.Clients,
    val isLoading: Boolean = false,
    val entityDocument: PlatformFile? = null,
    val uploadType: UploadType = UploadType.Upload,
    val documentPreviewedAndAccepted: Boolean=  false,
){
    sealed interface EntityType {
        object Clients: EntityType
        object Loans: EntityType
    }
    sealed interface UploadType {
        object Upload: UploadType
        object Update: UploadType
    }
}
