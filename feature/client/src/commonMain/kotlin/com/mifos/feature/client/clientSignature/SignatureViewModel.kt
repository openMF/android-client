package com.mifos.feature.client.clientSignature

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_failed_to_add_signature
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DataState
import com.mifos.core.domain.useCases.CreateDocumentUseCase
import io.ktor.http.content.PartData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File

private val SignatureViewModel.viewModelScope: Any

class SignatureViewModel(
    private val createDocumentUseCase: CreateDocumentUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val clientId = savedStateHandle.getStateFlow(key = Constants.CLIENT_ID, initialValue = 0)

    private val _signatureUiState = MutableStateFlow<SignatureUiState>(SignatureUiState.Initial)
    val signatureUiState = _signatureUiState.asStateFlow()

    fun createDocument(type: String?, id: Int, name: String?, desc: String?, file: File?) =
        viewModelScope.launch {
            createDocumentUseCase(
                type,
                id,
                name,
                desc,
                getRequestFileBody(file),
            ).collect { result ->
                when (result) {
                    is DataState.Error ->
                        _signatureUiState.value =
                            SignatureUiState.Error(Res.string.feature_client_failed_to_add_signature)

                    is DataState.Loading -> _signatureUiState.value = SignatureUiState.Loading

                    is DataState.Success ->
                        _signatureUiState.value =
                            SignatureUiState.SignatureUploadedSuccessfully
                }
            }
        }

    private fun getRequestFileBody(file: File?): PartData? {
        // create RequestBody instance from file
        val requestFile =
            file?.asRequestBody("multipart/form-data".toMediaTypeOrNull())

        // PartData is used to send also the actual file name
        return requestFile?.let { PartData.createFormData("file", file.name, it) }
    }
}