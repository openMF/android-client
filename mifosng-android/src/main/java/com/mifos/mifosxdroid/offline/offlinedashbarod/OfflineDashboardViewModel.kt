package com.mifos.mifosxdroid.offline.offlinedashbarod

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.data.CenterPayload
import com.mifos.core.objects.accounts.loan.LoanRepaymentRequest
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.core.objects.client.ClientPayload
import com.mifos.core.objects.group.GroupPayload
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 16/08/23.
 */
@HiltViewModel
class OfflineDashboardViewModel @Inject constructor(private val repository: OfflineDashboardRepository) :
    ViewModel() {

    private val _offlineDashboardUiState = MutableLiveData<OfflineDashboardUiState>()

    val offlineDashboardUiState: LiveData<OfflineDashboardUiState>
        get() = _offlineDashboardUiState


    fun loadDatabaseClientPayload() {
        _offlineDashboardUiState.value = OfflineDashboardUiState.ShowProgressbar
        repository.allDatabaseClientPayload()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<ClientPayload>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _offlineDashboardUiState.value =
                        OfflineDashboardUiState.ShowError(e.message.toString())
                }

                override fun onNext(clientPayloads: List<ClientPayload>) {
                    _offlineDashboardUiState.value =
                        OfflineDashboardUiState.ShowClients(clientPayloads)
                }
            })

    }

    fun loadDatabaseGroupPayload() {
        _offlineDashboardUiState.value = OfflineDashboardUiState.ShowProgressbar
        repository.allDatabaseGroupPayload()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<GroupPayload>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _offlineDashboardUiState.value =
                        OfflineDashboardUiState.ShowError(e.message.toString())
                }

                override fun onNext(groupPayloads: List<GroupPayload>) {
                    _offlineDashboardUiState.value =
                        OfflineDashboardUiState.ShowGroups(groupPayloads)
                }
            })
    }

    fun loadDatabaseCenterPayload() {
        _offlineDashboardUiState.value = OfflineDashboardUiState.ShowProgressbar
        repository.allDatabaseCenterPayload()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<CenterPayload>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _offlineDashboardUiState.value =
                        OfflineDashboardUiState.ShowError(e.message.toString())
                }

                override fun onNext(centerPayloads: List<CenterPayload>) {
                    _offlineDashboardUiState.value =
                        OfflineDashboardUiState.ShowCenters(centerPayloads)
                }
            })

    }

    fun loadDatabaseLoanRepaymentTransactions() {
        _offlineDashboardUiState.value = OfflineDashboardUiState.ShowProgressbar
        repository.databaseLoanRepayments()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<LoanRepaymentRequest>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _offlineDashboardUiState.value =
                        OfflineDashboardUiState.ShowError(e.message.toString())
                }

                override fun onNext(loanRepaymentRequests: List<LoanRepaymentRequest>) {
                    _offlineDashboardUiState.value =
                        OfflineDashboardUiState.ShowLoanRepaymentTransactions(loanRepaymentRequests)
                }
            })

    }

    fun loadDatabaseSavingsAccountTransactions() {
        repository.allSavingsAccountTransactions()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<SavingsAccountTransactionRequest>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _offlineDashboardUiState.value =
                        OfflineDashboardUiState.ShowError(e.message.toString())
                }

                override fun onNext(transactionRequests: List<SavingsAccountTransactionRequest>) {
                    _offlineDashboardUiState.value =
                        OfflineDashboardUiState.ShowSavingsAccountTransaction(transactionRequests)
                }
            })

    }
}