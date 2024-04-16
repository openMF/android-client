package com.mifos.feature.client.clientDetails.ui

import android.graphics.Bitmap
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.request.ImageResult
import com.mifos.core.common.utils.Resource
import com.mifos.core.network.utils.ImageLoaderUtils
import com.mifos.core.objects.accounts.loan.LoanAccount
import com.mifos.core.objects.accounts.savings.SavingsAccount
import com.mifos.core.objects.client.Client
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
        MutableStateFlow<ClientDetailsUiState>(ClientDetailsUiState.Empty)
    val clientDetailsUiState = _clientDetailsUiState.asStateFlow()

    private val _loanAccounts = MutableStateFlow<List<LoanAccount>?>(null)
    val loanAccount = _loanAccounts.asStateFlow()


    private val _savingsAccounts = MutableStateFlow<List<SavingsAccount>?>(null)
    val savingsAccounts = _savingsAccounts.asStateFlow()

    private val _client = MutableStateFlow<Client?>(null)
    val client = _client.asStateFlow()

    private val _showLoading = MutableStateFlow(true)
    val showLoading = _showLoading.asStateFlow()


    private fun uploadImage(id: Int, pngFile: File) = viewModelScope.launch(Dispatchers.IO) {
        uploadClientImageUseCase(id, pngFile).collect { result ->
            when (result) {
                is Resource.Error -> {
                    _clientDetailsUiState.value =
                        ClientDetailsUiState.ShowError(result.message ?: "Unexpected error")
                    _showLoading.value = false
                }

                is Resource.Loading -> {
                    _showLoading.value = true
                }

                is Resource.Success -> {
                    _clientDetailsUiState.value = ClientDetailsUiState.ShowUploadImageSuccessfully(
                        result.data,
                        pngFile.absolutePath
                    )
                    _showLoading.value = false
                }
            }
        }
    }

    fun deleteClientImage(clientId: Int) = viewModelScope.launch(Dispatchers.IO) {
        deleteClientImageUseCase(clientId).collect { result ->
            when (result) {
                is Resource.Error -> {
                    _clientDetailsUiState.value =
                        ClientDetailsUiState.ShowError(result.message ?: "Unexpected error")
                    _showLoading.value = false
                }

                is Resource.Loading -> _showLoading.value = true

                is Resource.Success -> {
                    _clientDetailsUiState.value =
                        ClientDetailsUiState.ShowClientImageDeletedSuccessfully
                    _showLoading.value = false
                }
            }
        }
    }

    fun loadClientDetailsAndClientAccounts(clientId: Int) = viewModelScope.launch(Dispatchers.IO) {
        getClientDetailsUseCase(clientId).collect { result ->
            when (result) {
                is Resource.Error -> {
                    _clientDetailsUiState.value =
                        ClientDetailsUiState.ShowError(result.message.toString())
                    _showLoading.value = false
                }

                is Resource.Loading -> _showLoading.value = true

                is Resource.Success -> {
                    _client.value = result.data?.client
                    _loanAccounts.value = result.data?.clientAccounts?.loanAccounts
                    _savingsAccounts.value = result.data?.clientAccounts?.savingsAccounts
                    _showLoading.value = false
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