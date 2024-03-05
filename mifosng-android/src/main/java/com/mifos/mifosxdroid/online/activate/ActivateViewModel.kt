package com.mifos.mifosxdroid.online.activate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.client.ActivatePayload
import com.mifos.mifosxdroid.R
import dagger.hilt.android.lifecycle.HiltViewModel
import org.apache.fineract.client.models.PostCentersCenterIdResponse
import org.apache.fineract.client.models.PostClientsClientIdResponse
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
            .subscribe(object : Subscriber<PostClientsClientIdResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _activateUiState.value = ActivateUiState.ShowError(e.message.toString())
                }

                override fun onNext(genericResponse: PostClientsClientIdResponse) {
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
            .subscribe(object : Subscriber<PostCentersCenterIdResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _activateUiState.value = ActivateUiState.ShowError(e.message.toString())
                }

                override fun onNext(genericResponse: PostCentersCenterIdResponse) {
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