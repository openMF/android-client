package com.mifos.mifosxdroid.dialogfragments.documentdialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.network.GenericResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.plugins.RxJavaPlugins
import rx.schedulers.Schedulers
import java.io.File
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 16/08/23.
 */
@HiltViewModel
class DocumentDialogViewModel @Inject constructor(private val repository: DocumentDialogRepository) :
    ViewModel() {

    private val _documentDialogUiState = MutableLiveData<DocumentDialogUiState>()

    val documentDialogUiState: LiveData<DocumentDialogUiState>
        get() = _documentDialogUiState

    fun createDocument(type: String?, id: Int, name: String?, desc: String?, file: File) {
        _documentDialogUiState.value = DocumentDialogUiState.ShowProgressbar
        repository
            .createDocument(type, id, name, desc, getRequestFileBody(file))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GenericResponse>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    val errorMessage: String?
                    try {
                        if (e is HttpException) {
                            errorMessage = e.response()?.errorBody()?.string()
                            _documentDialogUiState.value = errorMessage?.let {
                                DocumentDialogUiState.ShowUploadError(
                                    it
                                )
                            }
                        } else {
                            _documentDialogUiState.value =
                                DocumentDialogUiState.ShowError(e.message.toString())
                        }
                    } catch (throwable: Throwable) {
                        RxJavaPlugins.getInstance().errorHandler
                            .handleError(throwable)
                    }
                }

                override fun onNext(genericResponse: GenericResponse) {
                    _documentDialogUiState.value =
                        DocumentDialogUiState.ShowDocumentedCreatedSuccessfully(genericResponse)
                }
            })

    }

    fun updateDocument(
        entityType: String?, entityId: Int, documentId: Int,
        name: String?, desc: String?, file: File
    ) {
        _documentDialogUiState.value = DocumentDialogUiState.ShowProgressbar
        repository.updateDocument(
            entityType, entityId, documentId,
            name, desc, getRequestFileBody(file)
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GenericResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _documentDialogUiState.value =
                        DocumentDialogUiState.ShowError(e.message.toString())
                }

                override fun onNext(genericResponse: GenericResponse) {
                    _documentDialogUiState.value =
                        DocumentDialogUiState.ShowDocumentUpdatedSuccessfully(genericResponse)
                }
            })

    }

    private fun getRequestFileBody(file: File): MultipartBody.Part {
        // create RequestBody instance from file
        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData("file", file.name, requestFile)
    }
}