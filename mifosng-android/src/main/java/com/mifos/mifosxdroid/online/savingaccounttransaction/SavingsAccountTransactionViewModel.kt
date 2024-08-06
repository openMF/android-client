package com.mifos.mifosxdroid.online.savingaccounttransaction

import androidx.lifecycle.ViewModel
import com.mifos.core.objects.accounts.savings.DepositType
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionResponse
import com.mifos.core.objects.templates.savings.SavingsAccountTransactionTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 13/08/23.
 */
@HiltViewModel
class SavingsAccountTransactionViewModel @Inject constructor(private val repository: SavingsAccountTransactionRepository) :
    ViewModel() {

    private val _savingsAccountTransactionUiState = MutableStateFlow<SavingsAccountTransactionUiState>(SavingsAccountTransactionUiState.ShowProgressbar)
    val savingsAccountTransactionUiState: StateFlow<SavingsAccountTransactionUiState> get() = _savingsAccountTransactionUiState

    var transactionType : String? = null
    var accountId : Int? = null
    var clientName : String? = null
    var savingsAccountNumber : Int? = null
    var savingsAccountType: DepositType? = null

    fun loadSavingAccountTemplate() {
        _savingsAccountTransactionUiState.value = SavingsAccountTransactionUiState.ShowProgressbar
        accountId?.let {
            repository.getSavingsAccountTransactionTemplate(savingsAccountType?.endpoint,
                it, transactionType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<SavingsAccountTransactionTemplate>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        _savingsAccountTransactionUiState.value =
                            SavingsAccountTransactionUiState.ShowError(e.message.toString())
                    }

                    override fun onNext(savingsAccountTransactionTemplate: SavingsAccountTransactionTemplate) {
                        _savingsAccountTransactionUiState.value =
                            SavingsAccountTransactionUiState.ShowSavingAccountTemplate(
                                savingsAccountTransactionTemplate
                            )
                    }
                })
        }
    }

    fun processTransaction(request: SavingsAccountTransactionRequest) {
        _savingsAccountTransactionUiState.value = SavingsAccountTransactionUiState.ShowProgressbar
        accountId?.let {
            repository
                .processTransaction(savingsAccountType?.endpoint, it, transactionType, request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<SavingsAccountTransactionResponse>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        _savingsAccountTransactionUiState.value =
                            SavingsAccountTransactionUiState.ShowError(e.message.toString())
                    }

                    override fun onNext(savingsAccountTransactionResponse: SavingsAccountTransactionResponse) {
                        _savingsAccountTransactionUiState.value =
                            SavingsAccountTransactionUiState.ShowTransactionSuccessfullyDone(
                                savingsAccountTransactionResponse
                            )
                    }
                })
        }
    }

    fun checkInDatabaseSavingAccountTransaction() {
        _savingsAccountTransactionUiState.value = SavingsAccountTransactionUiState.ShowProgressbar
        accountId?.let {
            repository.getSavingsAccountTransaction(it)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<SavingsAccountTransactionRequest>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        _savingsAccountTransactionUiState.value =
                            SavingsAccountTransactionUiState.ShowError(e.message.toString())
                    }

                    override fun onNext(savingsAccountTransactionRequest: SavingsAccountTransactionRequest?) {
                        if (savingsAccountTransactionRequest != null) {
                            _savingsAccountTransactionUiState.value =
                                SavingsAccountTransactionUiState.ShowSavingAccountTransactionExistInDatabase
                        } else {
                            _savingsAccountTransactionUiState.value =
                                SavingsAccountTransactionUiState.ShowSavingAccountTransactionDoesNotExistInDatabase
                        }
                    }
                })
        }

    }
}