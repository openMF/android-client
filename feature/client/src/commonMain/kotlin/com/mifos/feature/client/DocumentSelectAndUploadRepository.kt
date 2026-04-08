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

import com.mifos.core.common.utils.DataState
import com.mifos.core.network.GenericResponse
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface DocumentSelectAndUploadRepository {
    val entityDocumentStateMutableStateFlow: MutableStateFlow<EntityDocumentState>

    fun selectImageFromGallery(
        dialogTitle: String = "",
    ): Flow<DataState<PlatformFile?>>

    fun selectDocumentFromFile(dialogTitle: String = ""): Flow<DataState<PlatformFile?>>

    fun downloadDocumentAndCache(): Flow<DataState<PlatformFile>>

    suspend fun deleteDocument(): Result<GenericResponse>

    fun uploadDocument(
        documentName: String,
        description: String,
    ): Flow<DataState<GenericResponse>>

    fun updateDocument(
        documentName: String,
        description: String,
    ): Flow<DataState<GenericResponse>>

    fun resetStateAndRefresh()

    fun updateStep(step: EntityDocumentState.Step)
    fun changeSubmitMode(submitMode: EntityDocumentState.SubmitMode)
    fun updateEntityDocument(platformFile: PlatformFile)

    fun resetRefreshState()
}

data class EntityDocumentState(
    val entityId: Int = -1,
    val documentId: Int = -1,
    val entityType: EntityType = EntityType.Clients,
    val doARefresh: Boolean = false,
    val entityDocument: PlatformFile? = null,
    val submitMode: SubmitMode = SubmitMode.UPLOAD,
    val changePreviewDocument: Boolean = false,
    val documentPreviewedAndAccepted: Boolean = false,
    val step: Step = Step.ADD,
) {
    sealed interface EntityType {
        object Clients : EntityType
        object Loans : EntityType
    }

    /**
     *  ADD : When picking a new document. Default. Set by DocumentList screen.
     *  PREVIEW : Set by AddDocument screen. Use for showing preview of the screen
     *  VIEW: If submit is clicked on the preview screen, then that document is ready for upload,
     *          but it can also be previewed again and updates,
     *  UPDATE_PREVIEW: This brings the Update New button. Used for selecting a new document in place
     *  of the old one.
     */
    enum class Step {
        ADD,
        PREVIEW,
        VIEW,
        UPDATE_PREVIEW,
    }

    /**
     * UPLOAD: Default value. Set by ClientListScreen only.
     * UPDATE: Set by ClientListScreen when the user choose to view an uploaded document.
     *
     * These values help SUBMIT button to decide whether to upload new document or update an
     * existing one.
     *
     * Currently, the UPDATE is not used. Because the PUT HTTP Request is not updating value on the
     * server. So, I am not using it.
     */
    enum class SubmitMode {
        UPLOAD,
        UPDATE,
    }
}
