package com.mifos.feature.client

import com.mifos.core.common.utils.DataState
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow


interface DocumentSelectAndUploadRepository {
    val entityDocumentStateMutableStateFlow: MutableStateFlow<EntityDocumentState>

    suspend fun selectImageFromGallery(
        dialogTitle: String = ""
    ): Flow<DataState<PlatformFile?>>

    suspend fun selectImageFromFile(dialogTitle: String =""): Flow<DataState<PlatformFile?>>

    suspend fun downloadDocumentAndCache(): Flow<DataState<PlatformFile>>

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

    fun updateStep(step: EntityDocumentState.Step)
    fun changeSubmitMode(sumbitMode: EntityDocumentState.SubmitMode)
    fun updateEntityDocument(platformFile: PlatformFile)
}


data class EntityDocumentState(
    val entityId: Int = -1,
    val documentId: Int = -1,
    val entityType: EntityType = EntityType.Clients,
    val isLoading: Boolean = false,
    val entityDocument: PlatformFile? = null,
    val submitMode: SubmitMode = SubmitMode.UPLOAD,
    val changePreviewDocument: Boolean = false,
    val documentPreviewedAndAccepted: Boolean=  false,
    val step: Step = Step.VIEW
){
    sealed interface EntityType {
        object Clients: EntityType
        object Loans: EntityType
    }
    enum class Step {
        ADD,
        PREVIEW,
        VIEW,
        UPDATE_PREVIEW,
    }
    enum class SubmitMode {
        UPLOAD,
        UPDATE
    }
}
