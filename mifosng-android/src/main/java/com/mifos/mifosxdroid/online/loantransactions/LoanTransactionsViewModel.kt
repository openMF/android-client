package com.mifos.mifosxdroid.online.loantransactions

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
class LoanTransactionsViewModel @Inject constructor(private val repository: LoanTransactionsRepository) :
    ViewModel() {

    private val _loanTransactionsUiState = MutableLiveData<LoanTransactionsUiState>()

    val loanTransactionsUiState: LiveData<LoanTransactionsUiState>
        get() = _loanTransactionsUiState

    fun loadLoanTransaction(loan: Int) {
        _loanTransactionsUiState.value = LoanTransactionsUiState.ShowProgressBar
        repository.getLoanTransactions(loan)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<LoanWithAssociations>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _loanTransactionsUiState.value =
                        LoanTransactionsUiState.ShowFetchingError(e.message.toString())
                }

                override fun onNext(loanWithAssociations: LoanWithAssociations) {
                    _loanTransactionsUiState.value =
                        LoanTransactionsUiState.ShowLoanTransaction(loanWithAssociations)
                }
            })
    }
}