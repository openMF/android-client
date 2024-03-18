package com.mifos.mifosxdroid.online.loanaccountapproval

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.accounts.loan.LoanApproval
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.plugins.RxJavaPlugins
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
@HiltViewModel
class LoanAccountApprovalViewModel @Inject constructor(private val repository: LoanAccountApprovalRepository) :
    ViewModel() {

    private val _loanAccountApprovalUiState = MutableLiveData<LoanAccountApprovalUiState>()

    val loanAccountApprovalUiState: LiveData<LoanAccountApprovalUiState>
        get() = _loanAccountApprovalUiState

    fun approveLoan(loanId: Int, loanApproval: LoanApproval?) {
        _loanAccountApprovalUiState.value = LoanAccountApprovalUiState.ShowProgressbar
        repository.approveLoan(loanId, loanApproval)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GenericResponse?>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    try {
                        if (e is HttpException) {
                            val errorMessage = e.response()?.errorBody()
                                ?.string()
                            _loanAccountApprovalUiState.value = errorMessage?.let {
                                LoanAccountApprovalUiState.ShowLoanApproveFailed(
                                    it
                                )
                            }
                        }
                    } catch (throwable: Throwable) {
                        RxJavaPlugins.getInstance().errorHandler.handleError(e)
                    }
                }

                override fun onNext(genericResponse: GenericResponse?) {
                    _loanAccountApprovalUiState.value = genericResponse?.let {
                        LoanAccountApprovalUiState.ShowLoanApproveSuccessfully(
                            it
                        )
                    }
                }
            })
    }
}