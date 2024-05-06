package com.mifos.mifosxdroid.online.clientdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.objects.zipmodels.ClientAndClientAccounts
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */
@HiltViewModel
class ClientDetailsViewModel @Inject constructor(private val repository: ClientDetailsRepository) :
    ViewModel() {

    private val _clientDetailsUiState = MutableLiveData<ClientDetailsUiState>()

    val clientDetailsUiState: LiveData<ClientDetailsUiState>
        get() = _clientDetailsUiState

    fun uploadImage(id: Int, pngFile: File) {
        val imagePath = pngFile.absolutePath
        val requestFile = pngFile.asRequestBody("image/png".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", pngFile.name, requestFile)
        _clientDetailsUiState.value = ClientDetailsUiState.ShowUploadImageProgressbar(true)
        repository.uploadClientImage(id, body)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<ResponseBody?>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _clientDetailsUiState.value =
                        ClientDetailsUiState.ShowUploadImageFailed("Unable to update image")
                }

                override fun onNext(response: ResponseBody?) {
                    _clientDetailsUiState.value =
                        ClientDetailsUiState.ShowUploadImageSuccessfully(response, imagePath)
                }
            })
    }

    fun deleteClientImage(clientId: Int) {
        repository.deleteClientImage(clientId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<ResponseBody?>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _clientDetailsUiState.value =
                        ClientDetailsUiState.ShowFetchingError("Failed to delete image")
                }

                override fun onNext(response: ResponseBody?) {
                    _clientDetailsUiState.value =
                        ClientDetailsUiState.ShowClientImageDeletedSuccessfully
                }
            })
    }

    fun loadClientDetailsAndClientAccounts(clientId: Int) {
        _clientDetailsUiState.value = ClientDetailsUiState.ShowProgressbar(true)
        Observable.zip(
            repository.getClientAccounts(clientId),
            repository.getClient(clientId)
        ) { clientAccounts, client ->
            val clientAndClientAccounts = ClientAndClientAccounts()
            clientAndClientAccounts.client = client
            clientAndClientAccounts.clientAccounts = clientAccounts
            clientAndClientAccounts
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<ClientAndClientAccounts>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _clientDetailsUiState.value =
                        ClientDetailsUiState.ShowFetchingError("Client not found")
                }

                override fun onNext(clientAndClientAccounts: ClientAndClientAccounts) {
                    _clientDetailsUiState.value = clientAndClientAccounts.clientAccounts?.let {
                        ClientDetailsUiState.ShowClientAccount(
                            it
                        )
                    }
                    _clientDetailsUiState.value = clientAndClientAccounts.client?.let {
                        ClientDetailsUiState.ShowClientInformation(
                            it
                        )
                    }
                }
            })
    }
}