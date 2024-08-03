package com.mifos.feature.savings.account_summary

import androidx.lifecycle.ViewModel
import com.mifos.core.common.utils.Constants
import com.mifos.core.data.repository.SavingsAccountSummaryRepository
import com.mifos.core.objects.accounts.savings.DepositType
import com.mifos.core.objects.accounts.savings.SavingsAccountWithAssociations
import com.mifos.feature.savings.R
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
class SavingsAccountSummaryViewModel @Inject constructor(private val repository: SavingsAccountSummaryRepository) :
    ViewModel() {

    private val _savingsAccountSummaryUiState = MutableStateFlow<SavingsAccountSummaryUiState>(SavingsAccountSummaryUiState.ShowProgressbar)
    val savingsAccountSummaryUiState: StateFlow<SavingsAccountSummaryUiState> get() = _savingsAccountSummaryUiState

    var accountId = 0
    var savingsAccountType: DepositType? = null

    fun loadSavingAccount(type: String?) {
        _savingsAccountSummaryUiState.value = SavingsAccountSummaryUiState.ShowProgressbar
        repository.getSavingsAccount(type, accountId, Constants.TRANSACTIONS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<SavingsAccountWithAssociations>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    val error = R.string.feature_savings_failed_to_fetch_savingsaccount
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