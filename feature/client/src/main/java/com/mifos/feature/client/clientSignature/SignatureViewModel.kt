/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientSignature

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.Resource
import com.mifos.core.domain.useCases.CreateDocumentUseCase
import com.mifos.feature.client.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SignatureViewModel @Inject constructor(
    private val createDocumentUseCase: CreateDocumentUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val clientId = savedStateHandle.getStateFlow(key = Constants.CLIENT_ID, initialValue = 0)

    private val _signatureUiState = MutableStateFlow<SignatureUiState>(SignatureUiState.Initial)
    val signatureUiState = _signatureUiState.asStateFlow()

    fun createDocument(type: String?, id: Int, name: String?, desc: String?, file: File?) =
        viewModelScope.launch(Dispatchers.IO) {
            createDocumentUseCase(
                type,
                id,
                name,
                desc,
                getRequestFileBody(file),
            ).collect { result ->
                when (result) {
                    is Resource.Error ->
                        _signatureUiState.value =
                            SignatureUiState.Error(R.string.feature_client_failed_to_add_signature)

                    is Resource.Loading -> _signatureUiState.value = SignatureUiState.Loading

                    is Resource.Success ->
                        _signatureUiState.value =
                            SignatureUiState.SignatureUploadedSuccessfully
                }
            }
        }

    private fun getRequestFileBody(file: File?): MultipartBody.Part? {
        // create RequestBody instance from file
        val requestFile =
            file?.asRequestBody("multipart/form-data".toMediaTypeOrNull())

        // MultipartBody.Part is used to send also the actual file name
        return requestFile?.let { MultipartBody.Part.createFormData("file", file.name, it) }
    }
}
