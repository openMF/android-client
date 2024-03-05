package com.mifos.mifosxdroid.online.clientidentifiers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.objects.noncore.Identifier
import com.mifos.mifosxdroid.R
import dagger.hilt.android.lifecycle.HiltViewModel
import org.apache.fineract.client.models.DeleteClientsClientIdIdentifiersIdentifierIdResponse
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
@HiltViewModel
class ClientIdentifiersViewModel @Inject constructor(private val repository: ClientIdentifiersRepository) :
    ViewModel() {

    private val _clientIdentifiersUiState = MutableLiveData<ClientIdentifiersUiState>()

    val clientIdentifiersUiState: LiveData<ClientIdentifiersUiState>
        get() = _clientIdentifiersUiState

    fun loadIdentifiers(clientId: Int) {
        _clientIdentifiersUiState.value = ClientIdentifiersUiState.ShowProgressbar
        repository.getClientIdentifiers(clientId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<Identifier>>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _clientIdentifiersUiState.value =
                        ClientIdentifiersUiState.ShowFetchingError(R.string.failed_to_fetch_identifier)
                }

                override fun onNext(identifiers: List<Identifier>) {
                    _clientIdentifiersUiState.value =
                        ClientIdentifiersUiState.ShowClientIdentifiers(identifiers)
                }
            })
    }

    /**
     * Method to call Identifier Delete endpoint to remove an identifier.
     * @param clientId ClientID whose identifier has to be removed
     * @param identifierId Id of the identifier to be removed
     * @param position Position of the identifier to be removed. This will be sent on successful
     * request to notify that identifier at this position has been removed.
     */
    fun deleteIdentifier(clientId: Int, identifierId: Int, position: Int) {
        _clientIdentifiersUiState.value = ClientIdentifiersUiState.ShowProgressbar
        repository.deleteClientIdentifier(clientId, identifierId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<DeleteClientsClientIdIdentifiersIdentifierIdResponse>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _clientIdentifiersUiState.value =
                        ClientIdentifiersUiState.ShowFetchingError(R.string.failed_to_delete_identifier)
                }

                override fun onNext(genericResponse: DeleteClientsClientIdIdentifiersIdentifierIdResponse) {
                    _clientIdentifiersUiState.value =
                        ClientIdentifiersUiState.IdentifierDeletedSuccessfully(position)
                }
            })
    }

}