package com.mifos.mifosxdroid.online.loanrepaymentschedule

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
 * Created by Aditya Gupta on 12/08/23.
 */
@HiltViewModel
class LoanRepaymentScheduleViewModel @Inject constructor(private val repository: LoanRepaymentScheduleRepository) :
    ViewModel() {

    private val _loanRepaymentScheduleUiState = MutableLiveData<LoanRepaymentScheduleUiState>()

    val loanRepaymentScheduleUiState: LiveData<LoanRepaymentScheduleUiState>
        get() = _loanRepaymentScheduleUiState

    fun loadLoanRepaySchedule(loanId: Int) {
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