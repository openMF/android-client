package com.mifos.feature.loan.loan_repayment

import android.util.Log
import androidx.lifecycle.ViewModel
import com.mifos.core.data.repository.LoanRepaymentRepository
import com.mifos.core.objects.accounts.loan.LoanRepaymentRequest
import com.mifos.core.objects.accounts.loan.LoanRepaymentResponse
import com.mifos.core.objects.templates.loans.LoanRepaymentTemplate
import com.mifos.feature.loan.R
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
class LoanRepaymentViewModel @Inject constructor(private val repository: LoanRepaymentRepository) :
    ViewModel() {

    private val _loanRepaymentUiState =
        MutableStateFlow<LoanRepaymentUiState>(LoanRepaymentUiState.ShowProgressbar)
    val loanRepaymentUiState: StateFlow<LoanRepaymentUiState> get() = _loanRepaymentUiState

    var clientName = ""
    var loanId = 0
    var loanAccountNumber = ""
    var loanProductName = ""
    var amountInArrears: Double? = null

    fun loanLoanRepaymentTemplate() {
        _loanRepaymentUiState.value = LoanRepaymentUiState.ShowProgressbar
        repository.getLoanRepayTemplate(loanId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<LoanRepaymentTemplate?>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    Log.d("loanRepaymentLog ", ", $loanId $e")
                    _loanRepaymentUiState.value =
                        LoanRepaymentUiState.ShowError(R.string.feature_loan_failed_to_load_loan_repayment)
                }

                override fun onNext(loanRepaymentTemplate: LoanRepaymentTemplate?) {
                    _loanRepaymentUiState.value =
                        LoanRepaymentUiState.ShowLoanRepayTemplate(
                            loanRepaymentTemplate ?: LoanRepaymentTemplate()
                        )
                }
            })
    }

    /**
     *   app crashes on submit click
     */
    fun submitPayment(request: LoanRepaymentRequest) {
        _loanRepaymentUiState.value = LoanRepaymentUiState.ShowProgressbar
        repository.submitPayment(loanId, request)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<LoanRepaymentResponse?>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _loanRepaymentUiState.value =
                        LoanRepaymentUiState.ShowError(R.string.feature_loan_payment_failed)
                }

                override fun onNext(loanRepaymentResponse: LoanRepaymentResponse?) {
                    _loanRepaymentUiState.value =
                        LoanRepaymentUiState.ShowPaymentSubmittedSuccessfully(loanRepaymentResponse)
                }
            })
    }

    fun checkDatabaseLoanRepaymentByLoanId() {
        _loanRepaymentUiState.value = LoanRepaymentUiState.ShowProgressbar
        repository.getDatabaseLoanRepaymentByLoanId(loanId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<LoanRepaymentRequest?>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _loanRepaymentUiState.value =
                        LoanRepaymentUiState.ShowError(R.string.feature_loan_failed_to_load_loan_repayment)
                }

                override fun onNext(loanRepaymentRequest: LoanRepaymentRequest?) {
                    if (loanRepaymentRequest != null) {
                        _loanRepaymentUiState.value =
                            LoanRepaymentUiState.ShowLoanRepaymentExistInDatabase
                    } else {
                        _loanRepaymentUiState.value =
                            LoanRepaymentUiState.ShowLoanRepaymentDoesNotExistInDatabase
                    }
                }
            })
    }
}


