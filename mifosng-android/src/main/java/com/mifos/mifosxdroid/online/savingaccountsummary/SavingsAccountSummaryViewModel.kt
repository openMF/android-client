package com.mifos.mifosxdroid.online.savingaccountsummary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.common.utils.Constants
import com.mifos.core.objects.accounts.savings.SavingsAccountWithAssociations
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
class SavingsAccountSummaryViewModel @Inject constructor(private val repository: SavingsAccountSummaryRepository) :
    ViewModel() {

    private val _savingsAccountSummaryUiState = MutableLiveData<SavingsAccountSummaryUiState>()

    val savingsAccountSummaryUiState: LiveData<SavingsAccountSummaryUiState>
        get() = _savingsAccountSummaryUiState

    fun loadSavingAccount(type: String?, accountId: Int) {
        _savingsAccountSummaryUiState.value = SavingsAccountSummaryUiState.ShowProgressbar
        repository.getSavingsAccount(type, accountId, Constants.TRANSACTIONS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<SavingsAccountWithAssociations>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    val error = R.string.failed_to_fetch_savingsaccount
                    _savingsAccountSummaryUiState.value =
                        SavingsAccountSummaryUiState.ShowFetchingError(error)
                }

                override fun onNext(savingsAccountWithAssociations: SavingsAccountWithAssociations) {
                    _savingsAccountSummaryUiState.value =
                        SavingsAccountSummaryUiState.ShowSavingAccount(
                            savingsAccountWithAssociations
                        )
                }
            })
    }

}