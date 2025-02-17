/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.offline.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.data.repository.OfflineDashboardRepository
import com.mifos.core.entity.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.core.entity.client.ClientPayload
import com.mifos.feature.offline.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.observeOn
import kotlinx.coroutines.flow.subscribe
import kotlinx.coroutines.flow.subscribeOn
import kotlinx.coroutines.launch
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class OfflineDashboardViewModel @Inject constructor(
    private val repository: OfflineDashboardRepository,
) : ViewModel() {

    private val _offlineDashboardUiState =
        MutableStateFlow(OfflineDashboardUiState.SyncUiState(initGetSyncData()))
    val offlineDashboardUiState: StateFlow<OfflineDashboardUiState> = _offlineDashboardUiState

    fun loadDatabaseClientPayload() {
        repository.allDatabaseClientPayload()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                object : Subscriber<List<ClientPayload>>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        setError(Type.SYNC_CLIENTS, e.message.toString())
                    }

                    override fun onNext(clientPayloads: List<ClientPayload>) {
                        setCountOfSyncData(Type.SYNC_CLIENTS, clientPayloads.size)
                    }
                },
            )
    }

    fun loadDatabaseGroupPayload() {
        viewModelScope.launch {
            repository.allDatabaseGroupPayload()
                .catch {
                    setError(Type.SYNC_GROUPS, it.message.toString())
                }.collect { groupPayloads ->
                    setCountOfSyncData(Type.SYNC_GROUPS, groupPayloads.size)
                }
        }
    }

    fun loadDatabaseCenterPayload() {
        viewModelScope.launch {
            repository.allDatabaseCenterPayload()
                .catch {
                    setError(Type.SYNC_CENTERS, it.message.toString())
                }.collect { centerPayloads ->
                    setCountOfSyncData(Type.SYNC_CENTERS, centerPayloads.size)
                }
        }
    }

    fun loadDatabaseLoanRepaymentTransactions() {
        viewModelScope.launch {
            repository.databaseLoanRepayments()
                .catch { e ->
                    setError(Type.SYNC_LOAN_REPAYMENTS, e.message.toString())
                }.collect { loanRepaymentRequests ->
                    setCountOfSyncData(Type.SYNC_LOAN_REPAYMENTS, loanRepaymentRequests.size)
                }
        }
    }

    fun loadDatabaseSavingsAccountTransactions() {
        repository.allSavingsAccountTransactions()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                object : Subscriber<List<SavingsAccountTransactionRequest>>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        setError(Type.SYNC_SAVINGS_ACCOUNT_TRANSACTION, e.message.toString())
                    }

                    override fun onNext(transactionRequests: List<SavingsAccountTransactionRequest>) {
                        setCountOfSyncData(
                            Type.SYNC_SAVINGS_ACCOUNT_TRANSACTION,
                            transactionRequests.size,
                        )
                    }
                },
            )
    }

    private fun setCountOfSyncData(type: Type, count: Int) {
        viewModelScope.launch {
            val updatedList = _offlineDashboardUiState.value.list.map { syncStateData ->
                if (syncStateData.type == type) {
                    syncStateData.copy(count = count)
                } else {
                    syncStateData
                }
            }
            _offlineDashboardUiState.value = OfflineDashboardUiState.SyncUiState(updatedList)
        }
    }

    private fun setError(type: Type, error: String) {
        viewModelScope.launch {
            val updatedList = _offlineDashboardUiState.value.list.map { syncStateData ->
                if (syncStateData.type == type) {
                    syncStateData.copy(errorMsg = error)
                } else {
                    syncStateData
                }
            }
            _offlineDashboardUiState.value = OfflineDashboardUiState.SyncUiState(updatedList)
        }
    }

    private fun initGetSyncData(): List<SyncStateData> {
        return listOf(
            SyncStateData(
                count = 0,
                name = R.string.feature_offline_sync_clients,
                type = Type.SYNC_CLIENTS,
            ),
            SyncStateData(
                count = 0,
                name = R.string.feature_offline_sync_groups,
                type = Type.SYNC_GROUPS,
            ),
            SyncStateData(
                count = 0,
                name = R.string.feature_offline_sync_centers,
                type = Type.SYNC_CENTERS,
            ),
            SyncStateData(
                count = 0,
                name = R.string.feature_offline_sync_loanRepayments,
                type = Type.SYNC_LOAN_REPAYMENTS,
            ),
            SyncStateData(
                count = 0,
                name = R.string.feature_offline_sync_savingsAccountTransactions,
                type = Type.SYNC_SAVINGS_ACCOUNT_TRANSACTION,
            ),
        )
    }
}
