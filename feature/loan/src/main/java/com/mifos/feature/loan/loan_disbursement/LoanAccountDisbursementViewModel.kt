package com.mifos.feature.loan.loan_disbursement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.data.repository.LoanAccountDisbursementRepository
import com.mifos.core.model.APIEndPoint
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.accounts.loan.LoanDisbursement
import com.mifos.core.objects.templates.loans.LoanTransactionTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
@HiltViewModel
class LoanAccountDisbursementViewModel @Inject constructor(private val repository: LoanAccountDisbursementRepository) :
    ViewModel() {


    private val _loanAccountDisbursementUiState = MutableStateFlow<LoanAccountDisbursementUiState>(LoanAccountDisbursementUiState.ShowProgressbar)

    val loanAccountDisbursementUiState: StateFlow<LoanAccountDisbursementUiState>
        get() = _loanAccountDisbursementUiState

    var loanId : Int = 0

    fun loadLoanTemplate() {
        _loanAccountDisbursementUiState.value = LoanAccountDisbursementUiState.ShowProgressbar
        repository.getLoanTransactionTemplate(loanId, APIEndPoint.DISBURSE)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<LoanTransactionTemplate>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _loanAccountDisbursementUiState.value =
                        LoanAccountDisbursementUiState.ShowError(e.message.toString())
                }

                override fun onNext(loanTransactionTemplate: LoanTransactionTemplate) {
                    _loanAccountDisbursementUiState.value =
                        LoanAccountDisbursementUiState.ShowLoanTransactionTemplate(
                            loanTransactionTemplate
                        )
                }
            })
    }

    fun disburseLoan(loanDisbursement: LoanDisbursement?) {
        _loanAccountDisbursementUiState.value = LoanAccountDisbursementUiState.ShowProgressbar
        repository.disburseLoan(loanId, loanDisbursement)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GenericResponse?>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _loanAccountDisbursementUiState.value =
                        LoanAccountDisbursementUiState.ShowError(e.message.toString())
                }

                override fun onNext(genericResponse: GenericResponse?) {
                    _loanAccountDisbursementUiState.value =
                        LoanAccountDisbursementUiState.ShowDisburseLoanSuccessfully(genericResponse)
                }
            })
    }

}