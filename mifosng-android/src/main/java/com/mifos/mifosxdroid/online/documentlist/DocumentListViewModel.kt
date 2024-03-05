package com.mifos.mifosxdroid.online.documentlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.noncore.Document
import com.mifos.mifosxdroid.R
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.ResponseBody
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
@HiltViewModel
class DocumentListViewModel @Inject constructor(private val repository: DocumentListRepository) :
    ViewModel() {

    private val _documentListUiState = MutableLiveData<DocumentListUiState>()

    val documentListUiState: LiveData<DocumentListUiState>
        get() = _documentListUiState

    fun loadDocumentList(type: String?, id: Int) {
        _documentListUiState.value = DocumentListUiState.ShowProgressbar
        repository.getDocumentsList(type, id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<Document>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _documentListUiState.value =
                        DocumentListUiState.ShowFetchingError(R.string.failed_to_fetch_documents)
                }

                override fun onNext(documents: List<Document>) {
                    _documentListUiState.value = DocumentListUiState.ShowDocumentList(documents)
                }
            })
    }

    fun downloadDocument(entityType: String?, entityId: Int, documentId: Int) {
        _documentListUiState.value = DocumentListUiState.ShowProgressbar
        repository.downloadDocument(entityType, entityId, documentId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<ResponseBody?>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _documentListUiState.value =
                        DocumentListUiState.ShowFetchingError(R.string.failed_to_fetch_documents)
                }

                override fun onNext(responseBody: ResponseBody?) {
                    _documentListUiState.value =
                        DocumentListUiState.ShowDocumentFetchSuccessfully(responseBody)
                }
            })

    }

    fun removeDocument(entityType: String?, entityId: Int, documentId: Int) {
        _documentListUiState.value = DocumentListUiState.ShowProgressbar
        repository.removeDocument(entityType, entityId, documentId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GenericResponse?>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _documentListUiState.value =
                        DocumentListUiState.ShowFetchingError(R.string.failed_to_remove_document)
                }

                override fun onNext(genericResponse: GenericResponse?) {
                    _documentListUiState.value = DocumentListUiState.ShowDocumentRemovedSuccessfully
                }
            })

    }
}