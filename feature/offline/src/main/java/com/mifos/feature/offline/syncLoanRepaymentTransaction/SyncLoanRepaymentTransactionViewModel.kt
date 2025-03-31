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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.FileUtils
import com.mifos.core.data.repository.SyncLoanRepaymentTransactionRepository
import com.mifos.core.datastore.PrefManager
import com.mifos.feature.offline.R
import com.mifos.room.entities.PaymentTypeOptionEntity
import com.mifos.room.entities.accounts.loans.LoanRepaymentRequestEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

/**
 * Created by Aditya Gupta on 16/08/23.
 */
class SyncLoanRepaymentTransactionViewModel(
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

    private var mLoanRepaymentRequests: MutableList<LoanRepaymentRequestEntity> = mutableListOf()
    private var mPaymentTypeOptions: List<PaymentTypeOptionEntity> = emptyList()
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
        viewModelScope.launch {
            _syncLoanRepaymentTransactionUiState.value =
                SyncLoanRepaymentTransactionUiState.ShowProgressbar

            repository.databaseLoanRepayments()
                .catch {
                    _syncLoanRepaymentTransactionUiState.value =
                        SyncLoanRepaymentTransactionUiState.ShowError(R.string.feature_offline_failed_to_load_loanrepayment)
                }
                .collect { loanRepaymentRequests ->
                    mLoanRepaymentRequests = loanRepaymentRequests.toMutableList()
                    updateUiState()
                }
        }
    }

    fun loanPaymentTypeOption() {
        viewModelScope.launch {
            _syncLoanRepaymentTransactionUiState.value =
                SyncLoanRepaymentTransactionUiState.ShowProgressbar

            repository.paymentTypeOption()
                .catch {
                    _syncLoanRepaymentTransactionUiState.value =
                        SyncLoanRepaymentTransactionUiState.ShowError(R.string.feature_offline_failed_to_load_paymentoptions)
                }
                .collect { paymentTypeOptions ->
                    mPaymentTypeOptions = paymentTypeOptions
                    updateUiState()
                }
        }
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

    private fun syncLoanRepayment(loanId: Int, loanRepaymentRequest: LoanRepaymentRequestEntity?) {
        viewModelScope.launch {
            _syncLoanRepaymentTransactionUiState.value =
                SyncLoanRepaymentTransactionUiState.ShowProgressbar

            try {
                repository.submitPayment(loanId, loanRepaymentRequest!!)
                mLoanRepaymentRequests[mClientSyncIndex].loanId?.let {
                    deleteAndUpdateLoanRepayments(
                        it,
                    )
                }
            } catch (e: Exception) {
                val eLoanRepaymentRequest = mLoanRepaymentRequests[mClientSyncIndex].copy(
                    errorMessage = e.message.toString(),
                )
                updateLoanRepayment(eLoanRepaymentRequest)
            }
        }
    }

    private fun deleteAndUpdateLoanRepayments(loanId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _syncLoanRepaymentTransactionUiState.value =
                SyncLoanRepaymentTransactionUiState.ShowProgressbar

            repository.deleteAndUpdateLoanRepayments(loanId).catch {
                _syncLoanRepaymentTransactionUiState.value =
                    SyncLoanRepaymentTransactionUiState.ShowError(R.string.feature_offline_failed_to_update_list)
            }.collect { loanRepaymentRequests ->
                mClientSyncIndex = 0
                mLoanRepaymentRequests =
                    loanRepaymentRequests as MutableList<LoanRepaymentRequestEntity>
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
        }
    }

    private fun updateLoanRepayment(loanRepaymentRequest: LoanRepaymentRequestEntity?) {
        viewModelScope.launch {
            SyncLoanRepaymentTransactionUiState.ShowProgressbar

            repository.updateLoanRepaymentTransaction(loanRepaymentRequest!!)
                .flowOn(Dispatchers.IO)
                .catch {
                    _syncLoanRepaymentTransactionUiState.value =
                        SyncLoanRepaymentTransactionUiState.ShowError(R.string.feature_offline_failed_to_load_loanrepayment)
                }
                .collect {
                    mLoanRepaymentRequests[mClientSyncIndex] = it ?: LoanRepaymentRequestEntity()
                    mClientSyncIndex += 1
                    if (mLoanRepaymentRequests.size != mClientSyncIndex) {
                        syncGroupPayload()
                    }
                    updateUiState()
                }
        }
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
                    FileUtils.logger.d { it }
//                    Log.d(
//                        LOG_TAG,
//                        it,
//                    )
                }
            }
        }
    }
}
