package com.mifos.feature.loan.loan_repayment_schedule

import androidx.lifecycle.ViewModel
import com.mifos.core.data.repository.LoanRepaymentScheduleRepository
import com.mifos.core.objects.accounts.loan.LoanWithAssociations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 12/08/23.
 */
@HiltViewModel
class LoanRepaymentScheduleViewModel @Inject constructor(private val repository: LoanRepaymentScheduleRepository) :
    ViewModel() {

    private val _loanRepaymentScheduleUiState =
        MutableStateFlow<LoanRepaymentScheduleUiState>(LoanRepaymentScheduleUiState.ShowProgressbar)
    val loanRepaymentScheduleUiState: StateFlow<LoanRepaymentScheduleUiState> get() = _loanRepaymentScheduleUiState

    var loanId = 0

    fun loadLoanRepaySchedule() {
        _loanRepaymentScheduleUiState.value = LoanRepaymentScheduleUiState.ShowProgressbar
        repository.getLoanRepaySchedule(loanId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<LoanWithAssociations>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _loanRepaymentScheduleUiState.value =
                        LoanRepaymentScheduleUiState.ShowFetchingError(e.message.toString())
                }

                override fun onNext(loanWithAssociations: LoanWithAssociations) {
                    _loanRepaymentScheduleUiState.value =
                        LoanRepaymentScheduleUiState.ShowLoanRepaySchedule(loanWithAssociations)
                }
            })
    }

}