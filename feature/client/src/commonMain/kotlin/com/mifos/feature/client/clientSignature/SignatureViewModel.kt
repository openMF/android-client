package com.mifos.feature.client.clientSignature

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_failed_to_add_signature
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DataState
import com.mifos.core.domain.useCases.CreateDocumentUseCase
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.InternalAPI
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
            file = createDocumentRequestBody(documentFile, documentName, description),
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


    @OptIn(InternalAPI::class)
    private suspend fun createDocumentRequestBody(
        file: PlatformFile,
        name: String,
        description: String,
    ): MultiPartFormDataContent {
        val mimeType = getMimeTypeFromPlatformFile(file)
        val byteArray = file.readBytes()
        return MultiPartFormDataContent(
            formData {
                append(
                    "file",
                    byteArray,
                    Headers.build {
                        append(HttpHeaders.ContentType, mimeType)
                        append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
                    },
                )
                append("name", name)
                append("description", description)
            },
        )
    }

    fun getMimeTypeFromPlatformFile(file: PlatformFile): String {
        return when (file.extension.lowercase()) {
            "jpeg", "jpg" -> "image/jpeg"
            "png" -> "image/png"
            "pdf" -> "application/pdf"
            "doc" -> "application/msword"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "xls" -> "application/vnd.ms-excel"
            "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            else -> "application/octet-stream"
        }
    }
}