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

import androidclient.feature.offline.generated.resources.Res
import androidclient.feature.offline.generated.resources.feature_offline_failed_to_load_loanrepayment
import androidclient.feature.offline.generated.resources.feature_offline_failed_to_load_paymentoptions
import androidclient.feature.offline.generated.resources.feature_offline_no_loanrepayment_to_sync
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.FileUtils
import com.mifos.core.data.repository.SyncLoanRepaymentTransactionRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.room.entities.PaymentTypeOptionEntity
import com.mifos.room.entities.accounts.loans.LoanRepaymentRequestEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Created by Aditya Gupta on 16/08/23.
 */
class SyncLoanRepaymentTransactionViewModel(
    private val repository: SyncLoanRepaymentTransactionRepository,
    private val prefManager: UserPreferencesRepository,
    networkMonitor: NetworkMonitor,
) : ViewModel() {

    private val _syncLoanRepaymentTransactionUiState =
        MutableStateFlow<SyncLoanRepaymentTransactionUiState>(
            SyncLoanRepaymentTransactionUiState.ShowProgressbar,
        )
    val syncLoanRepaymentTransactionUiState: StateFlow<SyncLoanRepaymentTransactionUiState> =
        _syncLoanRepaymentTransactionUiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var mLoanRepaymentRequests: MutableList<LoanRepaymentRequestEntity> = mutableListOf()
    private var mPaymentTypeOptions: List<PaymentTypeOptionEntity> = emptyList()
    private var mClientSyncIndex = 0

    val isNetworkAvailable = networkMonitor.isOnline
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    val userStatus: StateFlow<Boolean> = prefManager.userInfo
        .map { it.userStatus }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

    fun refreshTransactions() {
        _isRefreshing.value = true
        loadDatabaseLoanRepaymentTransactions()
        loanPaymentTypeOption()
        _isRefreshing.value = false
    }

    fun loadDatabaseLoanRepaymentTransactions() {
        viewModelScope.launch {
            repository.databaseLoanRepayments()
                .collect { dataState ->
                    when (dataState) {
                        is DataState.Success -> {
                            mLoanRepaymentRequests = dataState.data.toMutableList()
                            updateUiState()
                        }

                        is DataState.Error -> {
                            _syncLoanRepaymentTransactionUiState.value =
                                SyncLoanRepaymentTransactionUiState.ShowError(Res.string.feature_offline_failed_to_load_loanrepayment)
                        }

                        is DataState.Loading -> {
                            _syncLoanRepaymentTransactionUiState.value =
                                SyncLoanRepaymentTransactionUiState.ShowProgressbar
                        }
                    }
                }
        }
    }

    fun loanPaymentTypeOption() {
        viewModelScope.launch {
            repository.paymentTypeOption()
                .collect { dataState ->
                    when (dataState) {
                        is DataState.Success -> {
                            mPaymentTypeOptions = dataState.data
                            updateUiState()
                        }

                        is DataState.Error -> {
                            _syncLoanRepaymentTransactionUiState.value =
                                SyncLoanRepaymentTransactionUiState.ShowError(Res.string.feature_offline_failed_to_load_paymentoptions)
                        }

                        is DataState.Loading -> {
                            _syncLoanRepaymentTransactionUiState.value =
                                SyncLoanRepaymentTransactionUiState.ShowProgressbar
                        }
                    }
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
                    Res.string.feature_offline_no_loanrepayment_to_sync.toString(),
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
        viewModelScope.launch {
            repository.deleteAndUpdateLoanRepayments(loanId)
                .collect { dataState ->
                    when (dataState) {
                        is DataState.Error -> Unit

                        DataState.Loading ->
                            _syncLoanRepaymentTransactionUiState.value =
                                SyncLoanRepaymentTransactionUiState.ShowProgressbar

                        is DataState.Success -> {
                            mClientSyncIndex = 0
                            mLoanRepaymentRequests = dataState.data.toMutableList()
                            if (mLoanRepaymentRequests.isNotEmpty()) {
                                syncGroupPayload()
                            } else {
                                _syncLoanRepaymentTransactionUiState.value =
                                    SyncLoanRepaymentTransactionUiState.ShowEmptyLoanRepayments(
                                        Res.string.feature_offline_no_loanrepayment_to_sync.toString(),
                                    )
                            }
                            updateUiState()
                        }
                    }
                }
        }
    }

    private fun updateLoanRepayment(loanRepaymentRequest: LoanRepaymentRequestEntity?) {
        viewModelScope.launch {
            repository.updateLoanRepaymentTransaction(loanRepaymentRequest!!)
                .collect { result ->
                    when (result) {
                        is DataState.Success -> {
                            val updatedEntity = result.data ?: LoanRepaymentRequestEntity()
                            mLoanRepaymentRequests[mClientSyncIndex] = updatedEntity
                            mClientSyncIndex += 1
                            if (mLoanRepaymentRequests.size != mClientSyncIndex) {
                                syncGroupPayload()
                            }
                            updateUiState()
                        }

                        is DataState.Error -> {
                            _syncLoanRepaymentTransactionUiState.value =
                                SyncLoanRepaymentTransactionUiState.ShowError(Res.string.feature_offline_failed_to_load_loanrepayment)
                        }

                        is DataState.Loading -> {
                            _syncLoanRepaymentTransactionUiState.value =
                                SyncLoanRepaymentTransactionUiState.ShowProgressbar
                        }
                    }
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
                }
            }
        }
    }
}
