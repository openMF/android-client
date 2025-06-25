/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanAccountSummary

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_unknown_error_occured
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.LoanAccountSummaryRepository
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class LoanAccountSummaryViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: LoanAccountSummaryRepository,
) : ViewModel() {

    val loanAccountNumber =
        savedStateHandle.getStateFlow(key = Constants.LOAN_ACCOUNT_NUMBER, initialValue = 0)

    private val _loanAccountSummaryUiState =
        MutableStateFlow<LoanAccountSummaryUiState>(LoanAccountSummaryUiState.ShowProgressbar)

    val loanAccountSummaryUiState: StateFlow<LoanAccountSummaryUiState>
        get() = _loanAccountSummaryUiState

    fun loadLoanById(loanAccountNumber: Int) {
        viewModelScope.launch {
            repository.getLoanById(loanAccountNumber).collect { dataState ->
                when (dataState) {
                    is DataState.Loading -> {
                        _loanAccountSummaryUiState.value = LoanAccountSummaryUiState.ShowProgressbar
                    }

                    is DataState.Success -> {
                        _loanAccountSummaryUiState.value = LoanAccountSummaryUiState.ShowLoanById(
                            dataState.data ?: LoanWithAssociationsEntity(),
                        )
                    }

                    is DataState.Error -> {
                        _loanAccountSummaryUiState.value = LoanAccountSummaryUiState.ShowFetchingError(
                            getString(Res.string.feature_loan_unknown_error_occured),
                        )
                    }
                }
            }
        }
    }
}
