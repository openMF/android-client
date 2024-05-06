package com.mifos.mifosxdroid.online.clientcharge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.objects.client.Charges
import com.mifos.core.objects.client.Page
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.plugins.RxJavaPlugins
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
@HiltViewModel
class ClientChargeViewModel @Inject constructor(private val repository: ClientChargeRepository) :
    ViewModel() {

    private val _clientChargeUiState = MutableLiveData<ClientChargeUiState>()

    val clientChargeUiState: LiveData<ClientChargeUiState>
        get() = _clientChargeUiState

    fun loadCharges(clientId: Int, offset: Int, limit: Int) {
        _clientChargeUiState.value = ClientChargeUiState.ShowProgressbar
        repository.getClientCharges(clientId, offset, limit)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Page<Charges>>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    try {
                        if (e is HttpException) {
                            val errorMessage = e.response()?.errorBody()?.string()
                            _clientChargeUiState.value = errorMessage?.let {
                                ClientChargeUiState.ShowFetchingErrorCharges(
                                    it
                                )
                            }
                        }
                    } catch (throwable: Throwable) {
                        RxJavaPlugins.getInstance().errorHandler.handleError(e)
                    }
                }

                override fun onNext(chargesPage: Page<Charges>) {
                    if (chargesPage.pageItems.isNotEmpty()) {
                        _clientChargeUiState.value =
                            ClientChargeUiState.ShowChargesList(chargesPage)
                    } else {
                        _clientChargeUiState.value = ClientChargeUiState.ShowEmptyCharges
                    }
                }
            })
    }
}