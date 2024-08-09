package com.mifos.feature.savings.account_summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.SavingsAccountSummaryRepository
import com.mifos.core.domain.use_cases.GetSavingsAccountUseCase
import com.mifos.core.objects.accounts.savings.DepositType
import com.mifos.core.objects.accounts.savings.SavingsAccountWithAssociations
import com.mifos.feature.savings.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
@HiltViewModel
class SavingsAccountSummaryViewModel @Inject constructor(
    private val getSavingsAccountUseCase: GetSavingsAccountUseCase
//    private val repository: SavingsAccountSummaryRepository
) : ViewModel() {

    private val _savingsAccountSummaryUiState =
        MutableStateFlow<SavingsAccountSummaryUiState>(SavingsAccountSummaryUiState.ShowProgressbar)
    val savingsAccountSummaryUiState: StateFlow<SavingsAccountSummaryUiState> get() = _savingsAccountSummaryUiState

    var accountId = 0
    var savingsAccountType: DepositType? = null

    fun loadSavingAccount(type: String?) = viewModelScope.launch(Dispatchers.IO) {
        getSavingsAccountUseCase(type, accountId, Constants.TRANSACTIONS).collect { result ->
            when (result) {
                is Resource.Error -> _savingsAccountSummaryUiState.value =
                    SavingsAccountSummaryUiState.ShowFetchingError(R.string.feature_savings_failed_to_fetch_savingsaccount)

                is Resource.Loading -> _savingsAccountSummaryUiState.value =
                    SavingsAccountSummaryUiState.ShowProgressbar

                is Resource.Success -> _savingsAccountSummaryUiState.value =
                    SavingsAccountSummaryUiState.ShowSavingAccount(
                        result.data ?: SavingsAccountWithAssociations()
                    )
            }
        }
    }
}