package com.mifos.mifosxdroid.dialogfragments.chargedialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.data.ChargesPayload
import com.mifos.core.objects.client.ChargeCreationResponse
import com.mifos.core.objects.templates.clients.ChargeOptions
import com.mifos.core.objects.templates.clients.ChargeTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.plugins.RxJavaPlugins
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 13/08/23.
 */
@HiltViewModel
class ChargeDialogViewModel @Inject constructor(private val repository: ChargeDialogRepository) :
    ViewModel() {

    private val _chargeDialogUiState = MutableLiveData<ChargeDialogUiState>()

    val chargeDialogUiState: LiveData<ChargeDialogUiState>
        get() = _chargeDialogUiState

    fun loadAllChargesV2(clientId: Int) {
        _chargeDialogUiState.value = ChargeDialogUiState.ShowProgressbar
        repository.getAllChargesV2(clientId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<ChargeTemplate>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    try {
                        if (e is HttpException) {
                            val errorMessage = e.response()?.errorBody()
                                ?.string()
                            _chargeDialogUiState.value = errorMessage?.let {
                                ChargeDialogUiState.ShowFetchingError(
                                    it
                                )
                            }
                        }
                    } catch (throwable: Throwable) {
                        RxJavaPlugins.getInstance().errorHandler.handleError(e)
                    }
                }

                override fun onNext(chargeTemplate: ChargeTemplate) {
                    _chargeDialogUiState.value =
                        ChargeDialogUiState.ShowAllChargesV2(chargeTemplate)
                }
            })
    }

    fun createCharges(clientId: Int, payload: ChargesPayload?) {
        _chargeDialogUiState.value = ChargeDialogUiState.ShowProgressbar
        repository.createCharges(clientId, payload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<ChargeCreationResponse>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    try {
                        if (e is HttpException) {
                            val errorMessage = e.response()?.errorBody()
                                ?.string()
                            _chargeDialogUiState.value = errorMessage?.let {
                                ChargeDialogUiState.ShowFetchingError(
                                    it
                                )
                            }
                        }
                    } catch (throwable: Throwable) {
                        RxJavaPlugins.getInstance().errorHandler.handleError(e)
                    }
                }

                override fun onNext(chargeCreationResponse: ChargeCreationResponse) {
                    _chargeDialogUiState.value =
                        ChargeDialogUiState.ShowChargesCreatedSuccessfully(chargeCreationResponse)
                }
            })
    }

    fun filterChargeName(chargeOptions: List<ChargeOptions>?): List<String> {
        val chargeNameList = ArrayList<String>()
        Observable.from<ChargeOptions>(chargeOptions)
            .subscribe { chargeOptions -> chargeNameList.add(chargeOptions.name) }
        return chargeNameList
    }
}