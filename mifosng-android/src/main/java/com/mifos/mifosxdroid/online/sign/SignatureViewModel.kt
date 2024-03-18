package com.mifos.mifosxdroid.online.sign

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.network.GenericResponse
import com.mifos.mifosxdroid.R
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */

@HiltViewModel
class SignatureViewModel @Inject constructor(private val repository: SignatureRepository) :
    ViewModel() {

    private val _signatureUiState = MutableLiveData<SignatureUiState>()

    val signatureUiState: LiveData<SignatureUiState>
        get() = _signatureUiState


    fun createDocument(type: String?, id: Int, name: String?, desc: String?, file: File?) {
        _signatureUiState.value = SignatureUiState.ShowProgressbar
        repository.createDocument(type, id, name, desc, getRequestFileBody(file))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GenericResponse?>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _signatureUiState.value =
                        SignatureUiState.ShowError(R.string.failed_to_upload_document)
                }

                override fun onNext(genericResponse: GenericResponse?) {
                    _signatureUiState.value =
                        SignatureUiState.ShowSignatureUploadedSuccessfully(genericResponse)
                }
            })
    }

    private fun getRequestFileBody(file: File?): MultipartBody.Part? {
        // create RequestBody instance from file
        val requestFile =
            file?.let { it.asRequestBody("multipart/form-data".toMediaTypeOrNull()) }

        // MultipartBody.Part is used to send also the actual file name
        return requestFile?.let { MultipartBody.Part.createFormData("file", file.name, it) }
    }


}