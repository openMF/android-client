package com.mifos.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.api.GenericResponse
import com.mifos.mifosxdroid.R
import com.mifos.objects.client.ActivatePayload
import com.mifos.repositories.ActivateRepository
import com.mifos.states.ActivateUiState
import com.mifos.utils.MFErrorParser
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */
@HiltViewModel
class ActivateViewModel @Inject constructor(private val repository: ActivateRepository) :
    ViewModel() {

    private val _activateUiState = MutableLiveData<ActivateUiState>()

    val activateUiState: LiveData<ActivateUiState>
        get() = _activateUiState

    fun activateClient(clientId: Int, clientActivate: ActivatePayload?) {
        _activateUiState.value = ActivateUiState.ShowProgressbar(true)
        repository.activateClient(clientId, clientActivate)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GenericResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _activateUiState.value = ActivateUiState.ShowError(e.message.toString())
                }

                override fun onNext(genericResponse: GenericResponse) {
                    _activateUiState.value =
                        ActivateUiState.ShowActivatedSuccessfully(R.string.client_activated_successfully)
                }
            })

    }

    fun activateCenter(centerId: Int, activatePayload: ActivatePayload?) {
        _activateUiState.value = ActivateUiState.ShowProgressbar(true)
        repository.activateCenter(centerId, activatePayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GenericResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _activateUiState.value = ActivateUiState.ShowError(e.message.toString())
                }

                override fun onNext(genericResponse: GenericResponse) {
                    _activateUiState.value =
                        ActivateUiState.ShowActivatedSuccessfully(R.string.center_activated_successfully)
                }
            })
    }

    fun activateGroup(groupId: Int, activatePayload: ActivatePayload?) {
        _activateUiState.value = ActivateUiState.ShowProgressbar(true)

        repository.activateGroup(groupId, activatePayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GenericResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _activateUiState.value = ActivateUiState.ShowError(e.message.toString())
                }

                override fun onNext(genericResponse: GenericResponse) {
                    _activateUiState.value =
                        ActivateUiState.ShowActivatedSuccessfully(R.string.group_created_successfully)
                }
            })

    }

}