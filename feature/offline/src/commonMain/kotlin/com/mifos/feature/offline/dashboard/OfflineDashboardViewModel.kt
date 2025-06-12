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

import androidclient.feature.offline.generated.resources.Res
import androidclient.feature.offline.generated.resources.feature_offline_sync_centers
import androidclient.feature.offline.generated.resources.feature_offline_sync_clients
import androidclient.feature.offline.generated.resources.feature_offline_sync_groups
import androidclient.feature.offline.generated.resources.feature_offline_sync_loanRepayments
import androidclient.feature.offline.generated.resources.feature_offline_sync_savingsAccountTransactions
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.OfflineDashboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OfflineDashboardViewModel(
    private val repository: OfflineDashboardRepository,
) : ViewModel() {

    private val _offlineDashboardUiState =
        MutableStateFlow(OfflineDashboardUiState.SyncUiState(initGetSyncData()))
    val offlineDashboardUiState: StateFlow<OfflineDashboardUiState> =
        _offlineDashboardUiState.asStateFlow()

    fun loadDatabaseClientPayload() {
        handleDataState(
            flow = repository.allDatabaseClientPayload(),
            type = Type.SYNC_CLIENTS,
        )
    }

    fun loadDatabaseGroupPayload() {
        handleDataState(
            flow = repository.allDatabaseGroupPayload(),
            type = Type.SYNC_GROUPS,
        )
    }

    fun loadDatabaseCenterPayload() {
        handleDataState(
            flow = repository.allDatabaseCenterPayload(),
            type = Type.SYNC_CENTERS,
        )
    }

    fun loadDatabaseLoanRepaymentTransactions() {
        handleDataState(
            flow = repository.databaseLoanRepayments(),
            type = Type.SYNC_LOAN_REPAYMENTS,
        )
    }

    fun loadDatabaseSavingsAccountTransactions() {
        handleDataState(
            flow = repository.allSavingsAccountTransactions(),
            type = Type.SYNC_SAVINGS_ACCOUNT_TRANSACTION,
        )
    }

    private fun setCountOfSyncData(type: Type, count: Int) {
        val updatedList = _offlineDashboardUiState.value.list.map { syncStateData ->
            if (syncStateData.type == type) {
                syncStateData.copy(count = count)
            } else {
                syncStateData
            }
        }
        _offlineDashboardUiState.value = OfflineDashboardUiState.SyncUiState(updatedList)
    }

    private fun setError(type: Type, error: String) {
        val updatedList = _offlineDashboardUiState.value.list.map { syncStateData ->
            if (syncStateData.type == type) {
                syncStateData.copy(errorMsg = error)
            } else {
                syncStateData
            }
        }
        _offlineDashboardUiState.value = OfflineDashboardUiState.SyncUiState(updatedList)
    }

    private fun <T> handleDataState(
        flow: Flow<DataState<List<T>>>,
        type: Type,
    ) {
        viewModelScope.launch {
            flow.collect { state ->
                when (state) {
                    is DataState.Success -> setCountOfSyncData(type, state.data.size)
                    is DataState.Error -> setError(type, state.message)
                    is DataState.Loading -> {
                        /* handle loading if needed */
                    }
                }
            }
        }
    }

    private fun initGetSyncData(): List<SyncStateData> {
        return listOf(
            SyncStateData(
                count = 0,
                name = Res.string.feature_offline_sync_clients,
                type = Type.SYNC_CLIENTS,
            ),
            SyncStateData(
                count = 0,
                name = Res.string.feature_offline_sync_groups,
                type = Type.SYNC_GROUPS,
            ),
            SyncStateData(
                count = 0,
                name = Res.string.feature_offline_sync_centers,
                type = Type.SYNC_CENTERS,
            ),
            SyncStateData(
                count = 0,
                name = Res.string.feature_offline_sync_loanRepayments,
                type = Type.SYNC_LOAN_REPAYMENTS,
            ),
            SyncStateData(
                count = 0,
                name = Res.string.feature_offline_sync_savingsAccountTransactions,
                type = Type.SYNC_SAVINGS_ACCOUNT_TRANSACTION,
            ),
        )
    }
}
