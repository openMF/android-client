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

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.data.repository.LoanAccountSummaryRepository
import com.mifos.room.entities.accounts.loans.LoanWithAssociations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
@HiltViewModel
class LoanAccountSummaryViewModel @Inject constructor(
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
            _loanAccountSummaryUiState.value = LoanAccountSummaryUiState.ShowProgressbar

            repository.getLoanById(loanAccountNumber)
                .catch {
                    Log.d("ErrorDebug", it.message.toString())
                    _loanAccountSummaryUiState.value =
                        LoanAccountSummaryUiState.ShowFetchingError("Loan Account not found.")
                }.collect { loanWithAssociations ->
                    _loanAccountSummaryUiState.value =
                        LoanAccountSummaryUiState.ShowLoanById(
                            loanWithAssociations ?: LoanWithAssociations(),
                        )
                }
        }
    }
}
