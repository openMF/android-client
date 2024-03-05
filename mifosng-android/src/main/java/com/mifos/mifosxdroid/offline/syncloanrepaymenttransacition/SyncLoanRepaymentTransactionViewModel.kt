package com.mifos.mifosxdroid.offline.syncloanrepaymenttransacition

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.objects.accounts.loan.LoanRepaymentRequest
import com.mifos.core.objects.accounts.loan.LoanRepaymentResponse
import com.mifos.mifosxdroid.R
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 16/08/23.
 */
@HiltViewModel
class SyncLoanRepaymentTransactionViewModel @Inject constructor(private val repository: SyncLoanRepaymentTransactionRepository) :
    ViewModel() {

    private val _syncLoanRepaymentTransactionUiState =
        MutableLiveData<SyncLoanRepaymentTransactionUiState>()

    val syncLoanRepaymentTransactionUiState: LiveData<SyncLoanRepaymentTransactionUiState>
        get() = _syncLoanRepaymentTransactionUiState

    fun loadDatabaseLoanRepaymentTransactions() {
        _syncLoanRepaymentTransactionUiState.value =
            SyncLoanRepaymentTransactionUiState.ShowProgressbar
        repository.databaseLoanRepayments()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<LoanRepaymentRequest>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncLoanRepaymentTransactionUiState.value =
                        SyncLoanRepaymentTransactionUiState.ShowError(R.string.failed_to_load_loanrepayment)
                }

                override fun onNext(loanRepaymentRequests: List<LoanRepaymentRequest>) {
                    _syncLoanRepaymentTransactionUiState.value =
                        SyncLoanRepaymentTransactionUiState.ShowLoanRepaymentTransactions(
                            loanRepaymentRequests
                        )
                }
            })

    }

    fun loanPaymentTypeOption() {
        _syncLoanRepaymentTransactionUiState.value =
            SyncLoanRepaymentTransactionUiState.ShowProgressbar
        repository
            .paymentTypeOption()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<com.mifos.core.objects.PaymentTypeOption>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncLoanRepaymentTransactionUiState.value =
                        SyncLoanRepaymentTransactionUiState.ShowError(R.string.failed_to_load_paymentoptions)
                }

                override fun onNext(paymentTypeOptions: List<com.mifos.core.objects.PaymentTypeOption>) {
                    _syncLoanRepaymentTransactionUiState.value =
                        SyncLoanRepaymentTransactionUiState.ShowPaymentTypeOption(paymentTypeOptions)
                }
            })

    }

    fun syncLoanRepayment(loanId: Int, loanRepaymentRequest: LoanRepaymentRequest?) {
        _syncLoanRepaymentTransactionUiState.value =
            SyncLoanRepaymentTransactionUiState.ShowProgressbar
        repository.submitPayment(loanId, loanRepaymentRequest!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<LoanRepaymentResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncLoanRepaymentTransactionUiState.value =
                        SyncLoanRepaymentTransactionUiState.ShowPaymentFailed(e.message.toString())
                }

                override fun onNext(loanRepaymentResponse: LoanRepaymentResponse) {
                    _syncLoanRepaymentTransactionUiState.value =
                        SyncLoanRepaymentTransactionUiState.ShowPaymentSubmittedSuccessfully
                }
            })

    }

    fun deleteAndUpdateLoanRepayments(loanId: Int) {
        _syncLoanRepaymentTransactionUiState.value =
            SyncLoanRepaymentTransactionUiState.ShowProgressbar
        repository.deleteAndUpdateLoanRepayments(loanId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<LoanRepaymentRequest>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncLoanRepaymentTransactionUiState.value =
                        SyncLoanRepaymentTransactionUiState.ShowError(R.string.failed_to_update_list)
                }

                override fun onNext(loanRepaymentRequests: List<LoanRepaymentRequest>) {
                    _syncLoanRepaymentTransactionUiState.value =
                        SyncLoanRepaymentTransactionUiState.ShowLoanRepaymentDeletedAndUpdateLoanRepayment(
                            loanRepaymentRequests
                        )
                }
            })

    }

    fun updateLoanRepayment(loanRepaymentRequest: LoanRepaymentRequest?) {
        _syncLoanRepaymentTransactionUiState.value =
            SyncLoanRepaymentTransactionUiState.ShowProgressbar
        repository.updateLoanRepaymentTransaction(loanRepaymentRequest!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<LoanRepaymentRequest>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncLoanRepaymentTransactionUiState.value =
                        SyncLoanRepaymentTransactionUiState.ShowError(R.string.failed_to_load_loanrepayment)
                }

                override fun onNext(loanRepaymentRequest: LoanRepaymentRequest) {
                    _syncLoanRepaymentTransactionUiState.value =
                        SyncLoanRepaymentTransactionUiState.ShowLoanRepaymentUpdated(
                            loanRepaymentRequest
                        )
                }
            })

    }
}