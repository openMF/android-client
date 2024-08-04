package com.mifos.feature.loan.loan_approval

import androidx.lifecycle.ViewModel
import com.mifos.core.data.repository.LoanAccountApprovalRepository
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.accounts.loan.LoanApproval
import com.mifos.core.objects.accounts.loan.LoanWithAssociations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
class LoanAccountApprovalViewModel @Inject constructor(
    private val repository: LoanAccountApprovalRepository
) :
    ViewModel() {

    private val _loanAccountApprovalUiState =
        MutableStateFlow<LoanAccountApprovalUiState>(LoanAccountApprovalUiState.Initial)
    val loanAccountApprovalUiState: StateFlow<LoanAccountApprovalUiState> get() = _loanAccountApprovalUiState

    var loanId = 0
    var loanWithAssociations: LoanWithAssociations? = null

    fun approveLoan(loanApproval: LoanApproval?) {
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
                            _loanAccountApprovalUiState.value =
                                LoanAccountApprovalUiState.ShowLoanApproveFailed(
                                    errorMessage ?: "Something went wrong"
                                )
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
                    }!!
                }
            })
    }
}