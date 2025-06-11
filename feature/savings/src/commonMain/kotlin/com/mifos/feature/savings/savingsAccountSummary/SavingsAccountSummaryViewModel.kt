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

import androidclient.feature.savings.generated.resources.Res
import androidclient.feature.savings.generated.resources.feature_savings_failed_to_fetch_savingsaccount
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.SavingsAccountSummaryRepository
import com.mifos.room.entities.accounts.savings.SavingsAccountWithAssociationsEntity
import com.mifos.room.entities.accounts.savings.SavingsSummaryData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class SavingsAccountSummaryViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: SavingsAccountSummaryRepository,
) : ViewModel() {

    private val arg = savedStateHandle.getStateFlow(key = "arg", initialValue = "")
    val savingsNavigationData: SavingsSummaryData =
        Json.decodeFromString<SavingsSummaryData>(arg.value)

    private val _savingsAccountSummaryUiState =
        MutableStateFlow<SavingsAccountSummaryUiState>(SavingsAccountSummaryUiState.ShowProgressbar)
    val savingsAccountSummaryUiState: StateFlow<SavingsAccountSummaryUiState> get() = _savingsAccountSummaryUiState.asStateFlow()

    fun loadSavingAccount(type: String?, accountId: Int) {
        viewModelScope.launch {
            if (type != null) {
                repository.getSavingsAccount(
                    type,
                    accountId,
                    Constants.TRANSACTIONS,
                ).collect { dataState ->
                    when (dataState) {
                        is DataState.Error -> {
                            Logger.e("Error: ${dataState.message}")
                            _savingsAccountSummaryUiState.value =
                                SavingsAccountSummaryUiState.ShowFetchingError(Res.string.feature_savings_failed_to_fetch_savingsaccount)
                        }

                        DataState.Loading ->
                            _savingsAccountSummaryUiState.value =
                                SavingsAccountSummaryUiState.ShowProgressbar

                        is DataState.Success ->
                            _savingsAccountSummaryUiState.value =
                                SavingsAccountSummaryUiState.ShowSavingAccount(
                                    dataState.data ?: SavingsAccountWithAssociationsEntity(),
                                )
                    }
                }
            }
        }
    }
}
