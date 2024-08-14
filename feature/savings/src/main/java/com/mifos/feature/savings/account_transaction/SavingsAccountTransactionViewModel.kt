package com.mifos.feature.savings.account_transaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.SavingsAccountTransactionRepository
import com.mifos.core.datastore.PrefManager
import com.mifos.core.domain.use_cases.GetSavingsAccountTransactionTemplateUseCase
import com.mifos.core.domain.use_cases.GetSavingsAccountTransactionUseCase
import com.mifos.core.domain.use_cases.ProcessTransactionUseCase
import com.mifos.core.objects.accounts.savings.DepositType
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionResponse
import com.mifos.core.objects.templates.savings.SavingsAccountTransactionTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 13/08/23.
 */
@HiltViewModel
class SavingsAccountTransactionViewModel @Inject constructor(
    private val getSavingsAccountTransactionTemplateUseCase: GetSavingsAccountTransactionTemplateUseCase,
    private val processTransactionUseCase: ProcessTransactionUseCase,
    private val getSavingsAccountTransactionUseCase: GetSavingsAccountTransactionUseCase,
    private val prefManager: PrefManager,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val accountId: StateFlow<Int?> =
        savedStateHandle.getStateFlow(key = Constants.SAVINGS_ACCOUNT_ID, initialValue = null)
    val clientName: StateFlow<String?> =
        savedStateHandle.getStateFlow(key = Constants.CLIENT_NAME, initialValue = null)
    val savingsAccountNumber: StateFlow<Int?> =
        savedStateHandle.getStateFlow(key = Constants.SAVINGS_ACCOUNT_NUMBER, initialValue = null)
    val transactionType: StateFlow<String?> =
        savedStateHandle.getStateFlow(key = Constants.SAVINGS_ACCOUNT_TRANSACTION_TYPE , initialValue = null)
    val savingsAccountType: StateFlow<DepositType?> =
        savedStateHandle.getStateFlow(key = Constants.SAVINGS_ACCOUNT_TYPE, initialValue = null)

    private val _savingsAccountTransactionUiState =
        MutableStateFlow<SavingsAccountTransactionUiState>(SavingsAccountTransactionUiState.ShowProgressbar)
    val savingsAccountTransactionUiState: StateFlow<SavingsAccountTransactionUiState> get() = _savingsAccountTransactionUiState

    fun setUserOffline() {
        prefManager.userStatus = Constants.USER_OFFLINE
    }

    fun loadSavingAccountTemplate() =
        accountId.value?.let {
            viewModelScope.launch(Dispatchers.IO) {
                getSavingsAccountTransactionTemplateUseCase(
                    savingsAccountType.value?.endpoint,
                    it,
                    transactionType.value
                ).collect { result ->
                    when (result) {
                        is Resource.Error -> _savingsAccountTransactionUiState.value =
                            SavingsAccountTransactionUiState.ShowError(result.message.toString())

                        is Resource.Loading -> _savingsAccountTransactionUiState.value =
                            SavingsAccountTransactionUiState.ShowProgressbar

                        is Resource.Success -> _savingsAccountTransactionUiState.value =
                            SavingsAccountTransactionUiState.ShowSavingAccountTemplate(
                                result.data ?: SavingsAccountTransactionTemplate()
                            )
                    }
                }
            }
        }

    fun processTransaction(request: SavingsAccountTransactionRequest) =
        accountId.value?.let {
            viewModelScope.launch(Dispatchers.IO) {
                processTransactionUseCase(
                    savingsAccountType.value?.endpoint,
                    it,
                    transactionType.value,
                    request
                ).collect { result ->
                    when (result) {
                        is Resource.Error -> _savingsAccountTransactionUiState.value =
                            SavingsAccountTransactionUiState.ShowError(result.message.toString())

                        is Resource.Loading -> _savingsAccountTransactionUiState.value =
                            SavingsAccountTransactionUiState.ShowProgressbar

                        is Resource.Success -> _savingsAccountTransactionUiState.value =
                            SavingsAccountTransactionUiState.ShowTransactionSuccessfullyDone(
                                result.data ?: SavingsAccountTransactionResponse()
                            )
                    }
                }
            }
        }

    fun checkInDatabaseSavingAccountTransaction() =
        viewModelScope.launch(Dispatchers.IO) {
            accountId.value?.let {
                getSavingsAccountTransactionUseCase(it).collect { result ->
                    when (result) {
                        is Resource.Error -> _savingsAccountTransactionUiState.value =
                            SavingsAccountTransactionUiState.ShowError(result.message.toString())

                        is Resource.Loading -> _savingsAccountTransactionUiState.value =
                            SavingsAccountTransactionUiState.ShowProgressbar

                        is Resource.Success -> {
                            if (result.data != null) {
                                _savingsAccountTransactionUiState.value =
                                    SavingsAccountTransactionUiState.ShowSavingAccountTransactionExistInDatabase
                            } else {
                                _savingsAccountTransactionUiState.value =
                                    SavingsAccountTransactionUiState.ShowSavingAccountTransactionDoesNotExistInDatabase
                            }
                        }
                    }
                }
            }
        }

}