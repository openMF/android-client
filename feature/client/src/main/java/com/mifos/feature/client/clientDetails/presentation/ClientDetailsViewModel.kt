package com.mifos.feature.client.clientDetails.presentation

import android.graphics.Bitmap
import android.os.Environment
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.Coil
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.transform.CircleCropTransformation
import com.mifos.core.common.utils.Resource
import com.mifos.core.network.utils.ImageLoaderUtils
import com.mifos.feature.client.clientDetails.domain.usecase.DeleteClientImageUseCase
import com.mifos.feature.client.clientDetails.domain.usecase.GetClientDetailsUseCase
import com.mifos.feature.client.clientDetails.domain.usecase.UploadClientImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */

@HiltViewModel
class ClientDetailsViewModel @Inject constructor(
    private val uploadClientImageUseCase: UploadClientImageUseCase,
    private val getClientDetailsUseCase: GetClientDetailsUseCase,
    private val deleteClientImageUseCase: DeleteClientImageUseCase,
    private val imageLoaderUtils: ImageLoaderUtils
) : ViewModel() {

    private val _clientDetailsUiState =
        MutableStateFlow<ClientDetailsUiState>(ClientDetailsUiState.Loading)
    val clientDetailsUiState = _clientDetailsUiState.asStateFlow()

    private fun uploadImage(id: Int, pngFile: File) = viewModelScope.launch(Dispatchers.IO) {
        uploadClientImageUseCase(id, pngFile).collect { result ->
            when (result) {
                is Resource.Error -> {
                    _clientDetailsUiState.value =
                        ClientDetailsUiState.ShowError(result.message ?: "Unexpected error")
                }

                is Resource.Loading -> {
                    _clientDetailsUiState.value = ClientDetailsUiState.Loading
                }

                is Resource.Success -> {
                    _clientDetailsUiState.value = ClientDetailsUiState.ShowUploadImageSuccessfully(
                        result.data,
                        pngFile.absolutePath
                    )
                }
            }
        }
    }

    fun deleteClientImage(clientId: Int) = viewModelScope.launch(Dispatchers.IO) {
        deleteClientImageUseCase(clientId).collect { result ->
            when (result) {
                is Resource.Error -> _clientDetailsUiState.value =
                    ClientDetailsUiState.ShowError(result.message ?: "Unexpected error")

                is Resource.Loading -> _clientDetailsUiState.value = ClientDetailsUiState.Loading

                is Resource.Success -> _clientDetailsUiState.value =
                    ClientDetailsUiState.ShowClientImageDeletedSuccessfully
            }
        }
    }

    fun loadClientDetailsAndClientAccounts(clientId: Int) = viewModelScope.launch(Dispatchers.IO) {
        getClientDetailsUseCase(clientId).collect { result ->
            when (result) {
                is Resource.Error -> _clientDetailsUiState.value =
                    ClientDetailsUiState.ShowError(result.message.toString())

                is Resource.Loading -> _clientDetailsUiState.value = ClientDetailsUiState.Loading

                is Resource.Success -> {
                    _clientDetailsUiState.value = ClientDetailsUiState.ShowClientDetails(
                        result.data?.client,
                        result.data?.clientAccounts
                    )
                }
            }
        }
    }


    fun saveClientImage(clientId: Int, bitmap: Bitmap) {
        try {
            val clientImageFile = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "/client_image.png"
            )
            clientImageFile.createNewFile()
            val fOut = FileOutputStream(clientImageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
            fOut.flush()
            fOut.close()
            uploadImage(clientId, clientImageFile)
        } catch (e: Exception) {
            _clientDetailsUiState.value = ClientDetailsUiState.ShowError(e.message.toString())
        }
    }

    suspend fun getClientImageUrl(clientId: Int): ImageResult {
        return imageLoaderUtils.loadImage(clientId)
    }
}