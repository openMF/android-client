package com.mifos.mifosxdroid.online.savingsaccountactivate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.network.GenericResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _savingsAccountActivateUiState = MutableStateFlow<SavingsAccountActivateUiState>(SavingsAccountActivateUiState.Initial)

    val savingsAccountActivateUiState: StateFlow<SavingsAccountActivateUiState>
        get() = _savingsAccountActivateUiState

    var savingsAccountId = 0

    fun activateSavings(request: HashMap<String, String>) {
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