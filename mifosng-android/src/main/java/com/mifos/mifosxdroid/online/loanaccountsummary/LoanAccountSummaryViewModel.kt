package com.mifos.mifosxdroid.online.loanaccountsummary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.objects.accounts.loan.LoanWithAssociations
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
@HiltViewModel
class LoanAccountSummaryViewModel @Inject constructor(private val repository: LoanAccountSummaryRepository) :
    ViewModel() {

    private val _loanAccountSummaryUiState = MutableLiveData<LoanAccountSummaryUiState>()

    val loanAccountSummaryUiState: LiveData<LoanAccountSummaryUiState>
        get() = _loanAccountSummaryUiState


    fun loadLoanById(loanAccountNumber: Int) {
        _loanAccountSummaryUiState.value = LoanAccountSummaryUiState.ShowProgressbar
        repository.getLoanById(loanAccountNumber)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<LoanWithAssociations?>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _loanAccountSummaryUiState.value =
                        LoanAccountSummaryUiState.ShowFetchingError("Loan Account not found.")
                }

                override fun onNext(loanWithAssociations: LoanWithAssociations?) {
                    _loanAccountSummaryUiState.value = loanWithAssociations?.let {
                        LoanAccountSummaryUiState.ShowLoanById(
                            it
                        )
                    }
                }
            })
    }
}