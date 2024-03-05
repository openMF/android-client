package com.mifos.mifosxdroid.online.savingaccounttransaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionResponse
import com.mifos.core.objects.templates.savings.SavingsAccountTransactionTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val _savingsAccountTransactionUiState =
        MutableLiveData<SavingsAccountTransactionUiState>()

    val savingsAccountTransactionUiState: LiveData<SavingsAccountTransactionUiState>
        get() = _savingsAccountTransactionUiState

    fun loadSavingAccountTemplate(type: String?, accountId: Int, transactionType: String?) {
        _savingsAccountTransactionUiState.value = SavingsAccountTransactionUiState.ShowProgressbar
        repository.getSavingsAccountTransactionTemplate(type, accountId, transactionType)
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

    fun processTransaction(
        type: String?, accountId: Int, transactionType: String?,
        request: SavingsAccountTransactionRequest
    ) {
        _savingsAccountTransactionUiState.value = SavingsAccountTransactionUiState.ShowProgressbar
        repository
            .processTransaction(type, accountId, transactionType, request)
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

    fun checkInDatabaseSavingAccountTransaction(savingAccountId: Int) {
        _savingsAccountTransactionUiState.value = SavingsAccountTransactionUiState.ShowProgressbar
        repository.getSavingsAccountTransaction(savingAccountId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<SavingsAccountTransactionRequest>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _savingsAccountTransactionUiState.value =
                        SavingsAccountTransactionUiState.ShowError(e.message.toString())
                }

                override fun onNext(savingsAccountTransactionRequest: SavingsAccountTransactionRequest) {
                    _savingsAccountTransactionUiState.value =
                        SavingsAccountTransactionUiState.ShowSavingAccountTransactionExistInDatabase
                }
            })

    }
}