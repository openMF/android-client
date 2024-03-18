package com.mifos.mifosxdroid.online.savingsaccountactivate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.network.GenericResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 13/08/23.
 */
@HiltViewModel
class SavingsAccountActivateViewModel @Inject constructor(private val repository: SavingsAccountActivateRepository) :
    ViewModel() {

    private val _savingsAccountActivateUiState = MutableLiveData<SavingsAccountActivateUiState>()

    val savingsAccountActivateUiState: LiveData<SavingsAccountActivateUiState>
        get() = _savingsAccountActivateUiState

    fun activateSavings(savingsAccountId: Int, request: HashMap<String, String>) {
        _savingsAccountActivateUiState.value = SavingsAccountActivateUiState.ShowProgressbar
        repository.activateSavings(savingsAccountId, request)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GenericResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _savingsAccountActivateUiState.value =
                        SavingsAccountActivateUiState.ShowError(e.message.toString())
                }

                override fun onNext(genericResponse: GenericResponse) {
                    _savingsAccountActivateUiState.value =
                        SavingsAccountActivateUiState.ShowSavingAccountActivatedSuccessfully(
                            genericResponse
                        )
                }
            })
    }
}