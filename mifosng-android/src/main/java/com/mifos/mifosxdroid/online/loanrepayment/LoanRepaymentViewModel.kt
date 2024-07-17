package com.mifos.mifosxdroid.online.loanrepayment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.objects.PaymentTypeOption
import com.mifos.core.objects.accounts.loan.LoanRepaymentRequest
import com.mifos.core.objects.accounts.loan.LoanRepaymentResponse
import com.mifos.core.objects.templates.loans.LoanRepaymentTemplate
import com.mifos.mifosxdroid.R
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
    var loanId: Int = 0
    var loanAccountNumber: String = ""
    var loanProductName = ""
    var amountInArrears: Double? = null

    fun loanLoanRepaymentTemplate() {
        _loanRepaymentUiState.value =
            LoanRepaymentUiState.ShowLoanRepayTemplate(
                sampleLoanRepaymentTemplate
            )



//
//        _loanRepaymentUiState.value = LoanRepaymentUiState.ShowProgressbar
//        repository.getLoanRepayTemplate(loanId)
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeOn(Schedulers.io())
//            .subscribe(object : Subscriber<LoanRepaymentTemplate?>() {
//                override fun onCompleted() {
//                }
//
//                override fun onError(e: Throwable) {
//                    Log.d("pronayLog ", ", $loanId $e")
//                    _loanRepaymentUiState.value =
//                        LoanRepaymentUiState.ShowError(R.string.failed_to_load_loanrepayment)
//                }
//
//                override fun onNext(loanRepaymentTemplate: LoanRepaymentTemplate?) {
//                    _loanRepaymentUiState.value =
//                        LoanRepaymentUiState.ShowLoanRepayTemplate(
//                            loanRepaymentTemplate ?: LoanRepaymentTemplate()
//                        )
//                }
//            })
    }

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
                        LoanRepaymentUiState.ShowError(R.string.payment_failed)
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
                        LoanRepaymentUiState.ShowError(R.string.failed_to_load_loanrepayment)
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


val samplePaymentTypeOptions = mutableListOf(
    PaymentTypeOption(
        id = 1,
        name = "Cash",
        description = "Cash payment",
        isCashPayment = true,
        position = 1
    ),
    PaymentTypeOption(
        id = 2,
        name = "Bank Transfer",
        description = "Payment through bank transfer",
        isCashPayment = false,
        position = 2
    ),
    PaymentTypeOption(
        id = 3,
        name = "Mobile Payment",
        description = "Payment via mobile money",
        isCashPayment = false,
        position = 3
    )
)

val sampleLoanRepaymentTemplate = LoanRepaymentTemplate(
    loanId = 101,
//    type = Type , // Assuming Type is an enum or predefined class in your project
    date = mutableListOf(2024, 7, 15),
//    currency =  , // Assuming Currency is an enum or predefined class in your project
    amount = 1000.0,
    principalPortion = 800.0,
    interestPortion = 150.0,
    feeChargesPortion = 30.0,
    penaltyChargesPortion = 20.0,
    paymentTypeOptions = samplePaymentTypeOptions
)
