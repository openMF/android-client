package com.mifos.mifosxdroid.online.createnewgroup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.objects.group.GroupPayload
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.response.SaveResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
@HiltViewModel
class CreateNewGroupViewModel @Inject constructor(private val repository: CreateNewGroupRepository) :
    ViewModel() {

    private val _createNewGroupUiState = MutableLiveData<CreateNewGroupUiState>()

    val createNewGroupUiState: LiveData<CreateNewGroupUiState>
        get() = _createNewGroupUiState

    fun loadOffices() {
        _createNewGroupUiState.value = CreateNewGroupUiState.ShowProgressbar
        repository.offices()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<Office>>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _createNewGroupUiState.value =
                        CreateNewGroupUiState.ShowFetchingError(e.message.toString())
                }

                override fun onNext(offices: List<Office>) {
                    _createNewGroupUiState.value = CreateNewGroupUiState.ShowOffices(offices)
                }
            })
    }

    fun createGroup(groupPayload: GroupPayload) {
        _createNewGroupUiState.value = CreateNewGroupUiState.ShowProgressbar
        repository.createGroup(groupPayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<SaveResponse>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _createNewGroupUiState.value =
                        CreateNewGroupUiState.ShowFetchingError(e.message.toString())
                }

                override fun onNext(saveResponse: SaveResponse) {
                    _createNewGroupUiState.value =
                        CreateNewGroupUiState.ShowGroupCreatedSuccessfully(saveResponse)
                }
            })

    }
}