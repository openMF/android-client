package com.mifos.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.api.GenericResponse
import com.mifos.repositories.SavingsAccountActivateRepository
import com.mifos.states.SavingsAccountActivateUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class SavingsAccountActivateViewModel @Inject constructor(private val repository: SavingsAccountActivateRepository) :
    ViewModel() {

    private val _savingsAccountActivateUiState = MutableLiveData<SavingsAccountActivateUiState>()

    val savingsAccountActivateUiState: LiveData<SavingsAccountActivateUiState>
        get() = _savingsAccountActivateUiState

    fun activateSavings(savingsAccountId: Int, request: HashMap<String, String>) {
//        checkViewAttached()
//        mvpView!!.showProgressbar(true)
        _savingsAccountActivateUiState.value = SavingsAccountActivateUiState.ShowProgressbar
        repository.activateSavings(savingsAccountId, request)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GenericResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    mvpView!!.showProgressbar(false)
//                    mvpView!!.showError(MFErrorParser.errorMessage(e))
                    _savingsAccountActivateUiState.value =
                        SavingsAccountActivateUiState.ShowError(e.message.toString())
                }

                override fun onNext(genericResponse: GenericResponse) {
//                    mvpView!!.showProgressbar(false)
//                    mvpView!!.showSavingAccountActivatedSuccessfully(genericResponse)
                    _savingsAccountActivateUiState.value =
                        SavingsAccountActivateUiState.ShowSavingAccountActivatedSuccessfully(
                            genericResponse
                        )
                }
            })
    }
}