/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanRepaymentSchedule

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.LoanRepaymentScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoanRepaymentScheduleViewModel(
    private val repository: LoanRepaymentScheduleRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val loanId =
        savedStateHandle.getStateFlow(key = Constants.LOAN_ACCOUNT_NUMBER, initialValue = 0)

    private val _loanRepaymentScheduleUiState =
        MutableStateFlow<LoanRepaymentScheduleUiState>(LoanRepaymentScheduleUiState.ShowProgressbar)
    val loanRepaymentScheduleUiState: StateFlow<LoanRepaymentScheduleUiState> get() = _loanRepaymentScheduleUiState

    suspend fun loadLoanRepaySchedule(loanId: Int) {
        repository.getLoanRepaySchedule(loanId).collect { state ->
            when (state) {
                is DataState.Error ->
                    _loanRepaymentScheduleUiState.value =
                        LoanRepaymentScheduleUiState.ShowFetchingError(state.message)
                DataState.Loading ->
                    _loanRepaymentScheduleUiState.value = LoanRepaymentScheduleUiState.ShowProgressbar
                is DataState.Success ->
                    _loanRepaymentScheduleUiState.value =
                        LoanRepaymentScheduleUiState.ShowLoanRepaySchedule(
                            state.data,
                        )
            }
        }
    }
}
