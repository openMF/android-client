package com.mifos.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.objects.accounts.savings.SavingsAccountTransactionResponse
import com.mifos.objects.templates.savings.SavingsAccountTransactionTemplate
import com.mifos.repositories.SavingsAccountTransactionRepository
import com.mifos.states.SavingsAccountTransactionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class SavingsAccountTransactionViewModel @Inject constructor(private val repository: SavingsAccountTransactionRepository) :
    ViewModel() {

    private val _savingsAccountTransactionUiState =
        MutableLiveData<SavingsAccountTransactionUiState>()

    val savingsAccountTransactionUiState: LiveData<SavingsAccountTransactionUiState>
        get() = _savingsAccountTransactionUiState

    fun loadSavingAccountTemplate(type: String?, accountId: Int, transactionType: String?) {
//        checkViewAttached()
//        mvpView?.showProgressbar(true)
        _savingsAccountTransactionUiState.value = SavingsAccountTransactionUiState.ShowProgressbar
        repository.getSavingsAccountTransactionTemplate(type, accountId, transactionType)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<SavingsAccountTransactionTemplate>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    mvpView?.showProgressbar(false)
//                    mvpView?.showError(R.string.failed_to_fetch_savings_template)
                    _savingsAccountTransactionUiState.value =
                        SavingsAccountTransactionUiState.ShowError(e.message.toString())
                }

                override fun onNext(savingsAccountTransactionTemplate: SavingsAccountTransactionTemplate) {
//                    mvpView?.showProgressbar(false)
//                    mvpView?.showSavingAccountTemplate(savingsAccountTransactionTemplate)
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
//        checkViewAttached()
//        mvpView?.showProgressbar(true)
        _savingsAccountTransactionUiState.value = SavingsAccountTransactionUiState.ShowProgressbar
        repository
            .processTransaction(type, accountId, transactionType, request)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<SavingsAccountTransactionResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    mvpView?.showProgressbar(false)
//                    mvpView?.showError(R.string.transaction_failed)
                    _savingsAccountTransactionUiState.value =
                        SavingsAccountTransactionUiState.ShowError(e.message.toString())
                }

                override fun onNext(savingsAccountTransactionResponse: SavingsAccountTransactionResponse) {
//                    mvpView?.showProgressbar(false)
//                    mvpView?.showTransactionSuccessfullyDone(savingsAccountTransactionResponse)
                    _savingsAccountTransactionUiState.value =
                        SavingsAccountTransactionUiState.ShowTransactionSuccessfullyDone(
                            savingsAccountTransactionResponse
                        )
                }
            })
    }

    fun checkInDatabaseSavingAccountTransaction(savingAccountId: Int) {
//        checkViewAttached()
//        mvpView?.showProgressbar(true)
        _savingsAccountTransactionUiState.value = SavingsAccountTransactionUiState.ShowProgressbar
        repository.getSavingsAccountTransaction(savingAccountId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<SavingsAccountTransactionRequest>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                        mvpView?.showProgressbar(false)
//                        mvpView?.showError(R.string.failed_to_load_savingaccounttransaction)
                    _savingsAccountTransactionUiState.value =
                        SavingsAccountTransactionUiState.ShowError(e.message.toString())
                }

                override fun onNext(savingsAccountTransactionRequest: SavingsAccountTransactionRequest) {
//                        mvpView?.showProgressbar(false)
//                        mvpView?.showSavingAccountTransactionExistInDatabase()
                    _savingsAccountTransactionUiState.value =
                        SavingsAccountTransactionUiState.ShowSavingAccountTransactionExistInDatabase
                }
            })

    }
}