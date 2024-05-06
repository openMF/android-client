package com.mifos.mifosxdroid.dialogfragments.identifierdialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.objects.noncore.DocumentType
import com.mifos.core.objects.noncore.IdentifierCreationResponse
import com.mifos.core.objects.noncore.IdentifierPayload
import com.mifos.core.objects.noncore.IdentifierTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.plugins.RxJavaPlugins
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 16/08/23.
 */
@HiltViewModel
class IdentifierDialogViewModel @Inject constructor(private val repository: IdentifierDialogRepository) :
    ViewModel() {

    private val _identifierDialogUiState = MutableLiveData<IdentifierDialogUiState>()

    val identifierDialogUiState: LiveData<IdentifierDialogUiState>
        get() = _identifierDialogUiState

    fun loadClientIdentifierTemplate(clientId: Int) {
        repository.getClientIdentifierTemplate(clientId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<IdentifierTemplate>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _identifierDialogUiState.value =
                        IdentifierDialogUiState.ShowError(e.message.toString())
                }

                override fun onNext(identifierTemplate: IdentifierTemplate) {
                    _identifierDialogUiState.value =
                        IdentifierDialogUiState.ShowClientIdentifierTemplate(identifierTemplate)
                }
            })

    }

    fun createClientIdentifier(clientId: Int, identifierPayload: IdentifierPayload?) {
        _identifierDialogUiState.value = IdentifierDialogUiState.ShowProgressbar
        repository.createClientIdentifier(clientId, identifierPayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<IdentifierCreationResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    try {
                        if (e is HttpException) {
                            val errorMessage = e.response()?.errorBody()
                                ?.string()
                            _identifierDialogUiState.value = errorMessage?.let {
                                IdentifierDialogUiState.ShowError(
                                    it
                                )
                            }
                        }
                    } catch (throwable: Throwable) {
                        RxJavaPlugins.getInstance().errorHandler.handleError(e)
                    }
                }

                override fun onNext(identifierCreationResponse: IdentifierCreationResponse) {
                    _identifierDialogUiState.value =
                        IdentifierDialogUiState.ShowIdentifierCreatedSuccessfully(
                            identifierCreationResponse
                        )
                }
            })

    }

    fun getIdentifierDocumentTypeNames(documentTypes: List<DocumentType>): List<String> {
        val documentTypeList = ArrayList<String>()
        Observable.from(documentTypes)
            .subscribe { documentType -> documentType.name?.let { documentTypeList.add(it) } }
        return documentTypeList
    }

    /**
     * Method to map Document Type with the corresponding name.
     * @param documentTypeList List of DocumentType
     * @return HashMap of <Name></Name>,DocumentType>
     */
    fun mapDocumentTypesWithName(documentTypeList: List<DocumentType>): HashMap<String, DocumentType> {
        val hashMap = HashMap<String, DocumentType>()
        Observable.from(documentTypeList)
            .subscribe { documentType -> hashMap[documentType.name!!] = documentType }
        return hashMap
    }
}