/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.offline.syncLoanRepaymentTransaction

import android.util.Log
import androidx.lifecycle.ViewModel
import com.mifos.core.common.utils.FileUtils.LOG_TAG
import com.mifos.core.data.CenterPayload_Table.errorMessage
import com.mifos.core.data.repository.SyncLoanRepaymentTransactionRepository
import com.mifos.core.datastore.PrefManager
import com.mifos.core.objects.PaymentTypeOption
import com.mifos.core.objects.accounts.loan.LoanRepaymentRequest
import com.mifos.core.modelobjects.account.loan.LoanRepaymentResponse
import com.mifos.feature.offline.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 16/08/23.
 */
@HiltViewModel
class SyncLoanRepaymentTransactionViewModel @Inject constructor(
    private val repository: SyncLoanRepaymentTransactionRepository,
    private val prefManager: PrefManager,
) : ViewModel() {

    private val _syncLoanRepaymentTransactionUiState =
        MutableStateFlow<SyncLoanRepaymentTransactionUiState>(
            SyncLoanRepaymentTransactionUiState.ShowProgressbar,
        )
    val syncLoanRepaymentTransactionUiState: StateFlow<SyncLoanRepaymentTransactionUiState> =
        _syncLoanRepaymentTransactionUiState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var mLoanRepaymentRequests: MutableList<LoanRepaymentRequest> = mutableListOf()
    private var mPaymentTypeOptions: List<PaymentTypeOption> = emptyList()
    private var mClientSyncIndex = 0

    fun getUserStatus(): Boolean {
        return prefManager.userStatus
    }

    fun refreshTransactions() {
        _isRefreshing.value = true
        loadDatabaseLoanRepaymentTransactions()
        loanPaymentTypeOption()
        _isRefreshing.value = false
    }

    fun loadDatabaseLoanRepaymentTransactions() {
        _syncLoanRepaymentTransactionUiState.value =
            SyncLoanRepaymentTransactionUiState.ShowProgressbar
        repository.databaseLoanRepayments()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<LoanRepaymentRequest>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncLoanRepaymentTransactionUiState.value =
                        SyncLoanRepaymentTransactionUiState.ShowError(R.string.feature_offline_failed_to_load_loanrepayment)
                }

                override fun onNext(loanRepaymentRequests: List<LoanRepaymentRequest>) {
                    mLoanRepaymentRequests = loanRepaymentRequests.toMutableList()
                    updateUiState()
                }
            })
    }

    fun loanPaymentTypeOption() {
        _syncLoanRepaymentTransactionUiState.value =
            SyncLoanRepaymentTransactionUiState.ShowProgressbar
        repository
            .paymentTypeOption()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<PaymentTypeOption>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncLoanRepaymentTransactionUiState.value =
                        SyncLoanRepaymentTransactionUiState.ShowError(R.string.feature_offline_failed_to_load_paymentoptions)
                }

                override fun onNext(paymentTypeOptions: List<PaymentTypeOption>) {
                    mPaymentTypeOptions = paymentTypeOptions
                    updateUiState()
                }
            })
    }

    private fun updateUiState() {
        if (mLoanRepaymentRequests.isNotEmpty()) {
            _syncLoanRepaymentTransactionUiState.value =
                SyncLoanRepaymentTransactionUiState.ShowLoanRepaymentTransactions(
                    mLoanRepaymentRequests,
                    mPaymentTypeOptions,
                )
        } else {
            _syncLoanRepaymentTransactionUiState.value =
                SyncLoanRepaymentTransactionUiState.ShowEmptyLoanRepayments(
                    R.string.feature_offline_no_loanrepayment_to_sync.toString(),
                )
        }
    }

    private fun syncLoanRepayment(loanId: Int, loanRepaymentRequest: LoanRepaymentRequest?) {
        _syncLoanRepaymentTransactionUiState.value =
            SyncLoanRepaymentTransactionUiState.ShowProgressbar
        repository.submitPayment(loanId, loanRepaymentRequest!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<LoanRepaymentResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    val eLoanRepaymentRequest = mLoanRepaymentRequests[mClientSyncIndex]
                    eLoanRepaymentRequest.errorMessage = errorMessage.toString()
                    updateLoanRepayment(eLoanRepaymentRequest)
                }

                override fun onNext(loanRepaymentResponse: LoanRepaymentResponse) {
                    mLoanRepaymentRequests[mClientSyncIndex].loanId?.let {
                        deleteAndUpdateLoanRepayments(
                            it,
                        )
                    }
                }
            })
    }

    fun deleteAndUpdateLoanRepayments(loanId: Int) {
        _syncLoanRepaymentTransactionUiState.value =
            SyncLoanRepaymentTransactionUiState.ShowProgressbar
        repository.deleteAndUpdateLoanRepayments(loanId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<LoanRepaymentRequest>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncLoanRepaymentTransactionUiState.value =
                        SyncLoanRepaymentTransactionUiState.ShowError(R.string.feature_offline_failed_to_update_list)
                }

                override fun onNext(loanRepaymentRequests: List<LoanRepaymentRequest>) {
                    mClientSyncIndex = 0
                    mLoanRepaymentRequests =
                        loanRepaymentRequests as MutableList<LoanRepaymentRequest>
                    if (mLoanRepaymentRequests.isNotEmpty()) {
                        syncGroupPayload()
                    } else {
                        _syncLoanRepaymentTransactionUiState.value =
                            SyncLoanRepaymentTransactionUiState.ShowEmptyLoanRepayments(
                                R.string.feature_offline_no_loanrepayment_to_sync.toString(),
                            )
                    }
                    updateUiState()
                }
            })
    }

    fun updateLoanRepayment(loanRepaymentRequest: LoanRepaymentRequest?) {
        _syncLoanRepaymentTransactionUiState.value =
            SyncLoanRepaymentTransactionUiState.ShowProgressbar
        repository.updateLoanRepaymentTransaction(loanRepaymentRequest!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<LoanRepaymentRequest>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncLoanRepaymentTransactionUiState.value =
                        SyncLoanRepaymentTransactionUiState.ShowError(R.string.feature_offline_failed_to_load_loanrepayment)
                }

                override fun onNext(loanRepaymentRequest: LoanRepaymentRequest) {
                    mLoanRepaymentRequests[mClientSyncIndex] = loanRepaymentRequest
                    mClientSyncIndex += 1
                    if (mLoanRepaymentRequests.size != mClientSyncIndex) {
                        syncGroupPayload()
                    }
                    updateUiState()
                }
            })
    }

    fun syncGroupPayload() {
        for (i in mLoanRepaymentRequests.indices) {
            if (mLoanRepaymentRequests[i].errorMessage == null) {
                mLoanRepaymentRequests[i].loanId?.let {
                    syncLoanRepayment(
                        it,
                        mLoanRepaymentRequests[i],
                    )
                }
                mClientSyncIndex = i
                break
            } else {
                mLoanRepaymentRequests[i].errorMessage?.let {
                    Log.d(
                        LOG_TAG,
                        it,
                    )
                }
            }
        }
    }
}
