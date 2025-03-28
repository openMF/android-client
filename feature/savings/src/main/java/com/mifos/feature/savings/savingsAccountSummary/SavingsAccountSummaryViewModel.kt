/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.savings.savingsAccountSummary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.data.repository.SavingsAccountSummaryRepository
import com.mifos.feature.savings.R
import com.mifos.room.entities.accounts.savings.SavingsAccountWithAssociationsEntity
import com.mifos.room.entities.accounts.savings.SavingsSummaryData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class SavingsAccountSummaryViewModel(
//    private val getSavingsAccountUseCase: GetSavingsAccountUseCase,
    savedStateHandle: SavedStateHandle,
    private val repository: SavingsAccountSummaryRepository,
) : ViewModel() {

    private val arg = savedStateHandle.getStateFlow(key = "arg", initialValue = "")
    val savingsNavigationData: SavingsSummaryData =
        Json.decodeFromString<SavingsSummaryData>(arg.value)

    private val _savingsAccountSummaryUiState =
        MutableStateFlow<SavingsAccountSummaryUiState>(SavingsAccountSummaryUiState.ShowProgressbar)
    val savingsAccountSummaryUiState: StateFlow<SavingsAccountSummaryUiState> get() = _savingsAccountSummaryUiState

    fun loadSavingAccount(type: String?, accountId: Int) {
        viewModelScope.launch {
            _savingsAccountSummaryUiState.value =
                SavingsAccountSummaryUiState.ShowProgressbar

            repository.getSavingsAccount(
                type,
                accountId,
                Constants.TRANSACTIONS,
            ).catch {
                _savingsAccountSummaryUiState.value =
                    SavingsAccountSummaryUiState.ShowFetchingError(R.string.feature_savings_failed_to_fetch_savingsaccount)
            }.collect { savings ->
                _savingsAccountSummaryUiState.value =
                    SavingsAccountSummaryUiState.ShowSavingAccount(
                        savings ?: SavingsAccountWithAssociationsEntity(),
                    )
            }
        }
    }
}
