package com.mifos.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.api.GenericResponse
import com.mifos.objects.accounts.loan.SavingsApproval
import com.mifos.repositories.SavingsAccountApprovalRepository
import com.mifos.states.SavingsAccountApprovalUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class SavingsAccountApprovalViewModel @Inject constructor(private val repository: SavingsAccountApprovalRepository) :
    ViewModel() {

    private val _savingsAccountApprovalUiState = MutableLiveData<SavingsAccountApprovalUiState>()

    val savingsAccountApprovalUiState: LiveData<SavingsAccountApprovalUiState>
        get() = _savingsAccountApprovalUiState

    fun approveSavingsApplication(savingsAccountId: Int, savingsApproval: SavingsApproval?) {
//        checkViewAttached()
//        mvpView!!.showProgressbar(true)
        _savingsAccountApprovalUiState.value = SavingsAccountApprovalUiState.ShowProgressbar
        repository
            .approveSavingsApplication(savingsAccountId, savingsApproval)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GenericResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    mvpView!!.showProgressbar(false)
//                    mvpView!!.showError(MFErrorParser.errorMessage(e))

                }

                override fun onNext(genericResponse: GenericResponse) {
//                    mvpView!!.showProgressbar(false)
//                    mvpView!!.showSavingAccountApprovedSuccessfully(genericResponse)
                    _savingsAccountApprovalUiState.value =
                        SavingsAccountApprovalUiState.ShowSavingAccountApprovedSuccessfully(
                            genericResponse
                        )
                }
            })
    }
}