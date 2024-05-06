package com.mifos.mifosxdroid.activity.pinpointclient

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.client.ClientAddressRequest
import com.mifos.core.objects.client.ClientAddressResponse
import com.mifos.mifosxdroid.R
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
@HiltViewModel
class PinPointClientViewModel @Inject constructor(private val repository: PinPointClientRepository) :
    ViewModel() {

    private val _pinPointClientUiState = MutableLiveData<PinPointClientUiState>()

    val pinPointClientUiState: LiveData<PinPointClientUiState>
        get() = _pinPointClientUiState

    fun getClientPinpointLocations(clientId: Int) {
        _pinPointClientUiState.value = PinPointClientUiState.ShowProgressbar
        repository.getClientPinpointLocations(clientId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<ClientAddressResponse>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _pinPointClientUiState.value =
                        PinPointClientUiState.ShowMessage(R.string.failed_to_fetch_pinpoint_location)
                    _pinPointClientUiState.value = PinPointClientUiState.ShowFailedToFetchAddress
                }

                override fun onNext(clientAddressResponses: List<ClientAddressResponse>) {
                    if (clientAddressResponses.isEmpty()) {
                        _pinPointClientUiState.value = PinPointClientUiState.ShowEmptyAddress
                    } else {
                        _pinPointClientUiState.value =
                            PinPointClientUiState.ShowClientPinpointLocations(clientAddressResponses)
                    }
                }
            })

    }

    fun addClientPinpointLocation(clientId: Int, addressRequest: ClientAddressRequest?) {
        _pinPointClientUiState.value =
            PinPointClientUiState.ShowProgressDialog(true, R.string.adding_client_address)
        repository.addClientPinpointLocation(clientId, addressRequest)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GenericResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _pinPointClientUiState.value =
                        PinPointClientUiState.ShowMessage(R.string.failed_to_add_pinpoint_location)
                }

                override fun onNext(genericResponse: GenericResponse) {
                    _pinPointClientUiState.value =
                        PinPointClientUiState.UpdateClientAddress(R.string.address_added_successfully)
                }
            })

    }

    fun deleteClientPinpointLocation(apptableId: Int, datatableId: Int) {
        _pinPointClientUiState.value =
            PinPointClientUiState.ShowProgressDialog(true, R.string.deleting_client_address)
        repository.deleteClientAddressPinpointLocation(apptableId, datatableId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GenericResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _pinPointClientUiState.value =
                        PinPointClientUiState.ShowMessage(R.string.failed_to_delete_pinpoint_location)
                }

                override fun onNext(genericResponse: GenericResponse) {
                    _pinPointClientUiState.value =
                        PinPointClientUiState.UpdateClientAddress(R.string.address_deleted_successfully)
                }
            })
    }

    fun updateClientPinpointLocation(
        apptableId: Int, datatableId: Int,
        addressRequest: ClientAddressRequest?
    ) {
        _pinPointClientUiState.value =
            PinPointClientUiState.ShowProgressDialog(true, R.string.updating_client_address)
        repository.updateClientPinpointLocation(
            apptableId, datatableId, addressRequest
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GenericResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _pinPointClientUiState.value =
                        PinPointClientUiState.ShowMessage(R.string.failed_to_update_pinpoint_location)
                }

                override fun onNext(genericResponse: GenericResponse) {
                    _pinPointClientUiState.value =
                        PinPointClientUiState.UpdateClientAddress(R.string.address_updated_successfully)
                }
            })

    }

}