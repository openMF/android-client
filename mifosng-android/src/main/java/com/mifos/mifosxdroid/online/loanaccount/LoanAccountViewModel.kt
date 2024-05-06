package com.mifos.mifosxdroid.online.loanaccount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.data.LoansPayload
import com.mifos.core.objects.accounts.loan.Loans
import com.mifos.core.objects.organisation.LoanProducts
import com.mifos.core.objects.templates.loans.LoanTemplate
import com.mifos.mifosxdroid.R
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
@HiltViewModel
class LoanAccountViewModel @Inject constructor(private val repository: LoanAccountRepository) :
    ViewModel() {

    private val _loanAccountUiState = MutableLiveData<LoanAccountUiState>()

    val loanAccountUiState: LiveData<LoanAccountUiState>
        get() = _loanAccountUiState

    fun loadAllLoans() {
        _loanAccountUiState.value = LoanAccountUiState.ShowProgressbar
        repository.allLoans()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<LoanProducts>>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _loanAccountUiState.value =
                        LoanAccountUiState.ShowMessage(R.string.failed_to_fetch_loan_products)
                }

                override fun onNext(productLoanses: List<LoanProducts>) {
                    _loanAccountUiState.value = LoanAccountUiState.ShowAllLoan(productLoanses)
                }
            })
    }

    fun loadLoanAccountTemplate(clientId: Int, productId: Int) {
        _loanAccountUiState.value = LoanAccountUiState.ShowProgressbar
        repository.getLoansAccountTemplate(clientId, productId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<LoanTemplate?>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _loanAccountUiState.value =
                        LoanAccountUiState.ShowMessage(R.string.failed_to_fetch_loan_template)
                }

                override fun onNext(loanTemplate: LoanTemplate?) {
                    if (loanTemplate != null) {
                        _loanAccountUiState.value =
                            LoanAccountUiState.ShowLoanAccountTemplate(loanTemplate)
                    }
                }
            })
    }

    fun createLoansAccount(loansPayload: LoansPayload?) {
        _loanAccountUiState.value = LoanAccountUiState.ShowProgressbar
        repository.createLoansAccount(loansPayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Loans?>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _loanAccountUiState.value =
                        LoanAccountUiState.ShowFetchingError(e.message.toString())
                }

                override fun onNext(loans: Loans?) {
                    _loanAccountUiState.value =
                        LoanAccountUiState.ShowLoanAccountCreatedSuccessfully(loans)
                }
            })
    }

}