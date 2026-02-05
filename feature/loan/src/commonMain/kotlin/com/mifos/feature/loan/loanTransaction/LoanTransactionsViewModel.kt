/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanTransaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.LoanTransactionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoanTransactionsViewModel(
    private val repository: LoanTransactionsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val loanId = savedStateHandle.toRoute<LoanTransactionScreenRoute>().loanAccountNumber

    private val _loanTransactionsUiState =
        MutableStateFlow<LoanTransactionsUiState>(LoanTransactionsUiState.ShowProgressBar)
    val loanTransactionsUiState: StateFlow<LoanTransactionsUiState> get() = _loanTransactionsUiState

    private val _hideReversed = MutableStateFlow(false)
    val hideReversed: StateFlow<Boolean> get() = _hideReversed

    private val _hideAccruals = MutableStateFlow(false)
    val hideAccruals: StateFlow<Boolean> get() = _hideAccruals

    fun setHideReversed(value: Boolean) {
        _hideReversed.value = value
    }

    fun setHideAccruals(value: Boolean) {
        _hideAccruals.value = value
    }

    fun refresh() {
        viewModelScope.launch {
            loadLoanTransaction()
        }
    }

    private suspend fun loadLoanTransaction() {
        repository.getLoanTransactions(loanId).collect { state ->
            when (state) {
                is DataState.Error ->
                    _loanTransactionsUiState.value =
                        LoanTransactionsUiState.ShowFetchingError(state.message)
                DataState.Loading ->
                    _loanTransactionsUiState.value = LoanTransactionsUiState.ShowProgressBar
                is DataState.Success ->
                    _loanTransactionsUiState.value =
                        LoanTransactionsUiState.ShowLoanTransaction(
                            state.data,
                        )
            }
        }
    }
}
