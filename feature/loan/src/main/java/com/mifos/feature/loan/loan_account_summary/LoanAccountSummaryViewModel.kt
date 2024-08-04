package com.mifos.feature.loan.loan_account_summary

import androidx.lifecycle.ViewModel
import com.mifos.core.data.repository.LoanAccountSummaryRepository
import com.mifos.core.objects.accounts.loan.LoanWithAssociations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _loanAccountSummaryUiState =
        MutableStateFlow<LoanAccountSummaryUiState>(LoanAccountSummaryUiState.ShowProgressbar)

    val loanAccountSummaryUiState: StateFlow<LoanAccountSummaryUiState>
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
                    }!!
                }
            })
    }
}