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
import com.google.gson.Gson
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.Resource
import com.mifos.core.domain.use_cases.GetSavingsAccountUseCase
import com.mifos.core.objects.accounts.savings.SavingsAccountWithAssociations
import com.mifos.core.objects.accounts.savings.SavingsSummaryData
import com.mifos.feature.savings.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
@HiltViewModel
class SavingsAccountSummaryViewModel @Inject constructor(
    private val getSavingsAccountUseCase: GetSavingsAccountUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val arg = savedStateHandle.getStateFlow(key = "arg", initialValue = "")
    val savingsNavigationData: SavingsSummaryData = Gson().fromJson(arg.value, SavingsSummaryData::class.java)

    private val _savingsAccountSummaryUiState =
        MutableStateFlow<SavingsAccountSummaryUiState>(SavingsAccountSummaryUiState.ShowProgressbar)
    val savingsAccountSummaryUiState: StateFlow<SavingsAccountSummaryUiState> get() = _savingsAccountSummaryUiState

    fun loadSavingAccount(type: String?, accountId: Int) = viewModelScope.launch(Dispatchers.IO) {
        getSavingsAccountUseCase(type, accountId, Constants.TRANSACTIONS).collect { result ->
            when (result) {
                is Resource.Error ->
                    _savingsAccountSummaryUiState.value =
                        SavingsAccountSummaryUiState.ShowFetchingError(R.string.feature_savings_failed_to_fetch_savingsaccount)

                is Resource.Loading ->
                    _savingsAccountSummaryUiState.value =
                        SavingsAccountSummaryUiState.ShowProgressbar

                is Resource.Success ->
                    _savingsAccountSummaryUiState.value =
                        SavingsAccountSummaryUiState.ShowSavingAccount(
                            result.data ?: SavingsAccountWithAssociations(),
                        )
            }
        }
    }
}
