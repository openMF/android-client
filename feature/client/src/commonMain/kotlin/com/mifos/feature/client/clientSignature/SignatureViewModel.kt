/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientSignature

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_failed_to_add_signature
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DataState
import com.mifos.core.domain.useCases.CreateDocumentUseCase
import com.mifos.feature.client.utils.createImageRequestBody
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.compose.util.encodeToByteArray
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.write
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignatureViewModel(
    private val createDocumentUseCase: CreateDocumentUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val clientId = savedStateHandle.getStateFlow(key = Constants.CLIENT_ID, initialValue = 0)

    private val _signatureUiState = MutableStateFlow<SignatureUiState>(SignatureUiState.Initial)
    val signatureUiState = _signatureUiState.asStateFlow()

    fun createDocument(
        entityType: String,
        entityId: Int,
        documentName: String,
        description: String,
        documentFile: PlatformFile,
    ) = viewModelScope.launch {
        val result = createDocumentUseCase(
            entityType = entityType,
            entityId = entityId,
            file = createImageRequestBody(documentFile, documentName, description),
        )
        when (result) {
            is DataState.Error ->
                _signatureUiState.value =
                    SignatureUiState.Error(Res.string.feature_client_failed_to_add_signature)
            DataState.Loading -> _signatureUiState.value = SignatureUiState.Loading
            is DataState.Success ->
                _signatureUiState.value =
                    SignatureUiState.SignatureUploadedSuccessfully
        }
    }
}

suspend fun ImageBitmap.toPlatformFile(fileName: String): PlatformFile {
    val bytearray = this.encodeToByteArray(ImageFormat.PNG)
    val outFile = FileKit.filesDir / "$fileName.png"
    outFile.write(bytearray)
    return outFile
}
