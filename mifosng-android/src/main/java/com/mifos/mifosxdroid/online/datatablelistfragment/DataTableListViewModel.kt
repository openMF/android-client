package com.mifos.mifosxdroid.online.datatablelistfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.data.GroupLoanPayload
import com.mifos.core.data.LoansPayload
import com.mifos.core.objects.accounts.loan.Loans
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.ClientPayload
import com.mifos.mifosxdroid.R
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
@HiltViewModel
class DataTableListViewModel @Inject constructor(private val repository: DataTableListRepository) :
    ViewModel() {

    private val _dataTableListUiState = MutableLiveData<DataTableListUiState>()

    val dataTableListUiState: LiveData<DataTableListUiState>
        get() = _dataTableListUiState

    fun createLoansAccount(loansPayload: LoansPayload?) {
        _dataTableListUiState.value = DataTableListUiState.ShowProgressBar
        repository.createLoansAccount(loansPayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Loans>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _dataTableListUiState.value =
                        DataTableListUiState.ShowMessage(R.string.generic_failure_message)
                }

                override fun onNext(loans: Loans) {
                    _dataTableListUiState.value =
                        DataTableListUiState.ShowMessage(R.string.loan_creation_success)
                }
            })
    }

    fun createGroupLoanAccount(loansPayload: GroupLoanPayload?) {
        _dataTableListUiState.value = DataTableListUiState.ShowProgressBar
        repository.createGroupLoansAccount(loansPayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Loans?>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _dataTableListUiState.value =
                        DataTableListUiState.ShowMessage(R.string.generic_failure_message)
                }

                override fun onNext(loans: Loans?) {
                    _dataTableListUiState.value =
                        DataTableListUiState.ShowMessage(R.string.loan_creation_success)
                }
            })

    }

    fun createClient(clientPayload: ClientPayload) {
        _dataTableListUiState.value = DataTableListUiState.ShowProgressBar
        repository.createClient(clientPayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Client>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _dataTableListUiState.value =
                        DataTableListUiState.ShowMessageString(e.message.toString())
                }

                override fun onNext(client: Client) {
                    if (client.clientId != null) {
                        _dataTableListUiState.value =
                            DataTableListUiState.ShowClientCreatedSuccessfully(client)
                    } else {
                        _dataTableListUiState.value =
                            DataTableListUiState.ShowWaitingForCheckerApproval(R.string.waiting_for_checker_approval)
                    }
                }
            })
    }
}