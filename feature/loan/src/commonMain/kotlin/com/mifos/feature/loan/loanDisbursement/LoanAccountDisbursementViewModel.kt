/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanDisbursement

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.LoanAccountDisbursementRepository
import com.mifos.core.model.objects.account.loan.LoanDisbursement
import com.mifos.room.basemodel.APIEndPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoanAccountDisbursementViewModel(
    private val repository: LoanAccountDisbursementRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val loadId = savedStateHandle.getStateFlow(key = Constants.LOAN_ACCOUNT_NUMBER, initialValue = 0)

    private val _loanAccountDisbursementUiState = MutableStateFlow<LoanAccountDisbursementUiState>(LoanAccountDisbursementUiState.ShowProgressbar)

    val loanAccountDisbursementUiState: StateFlow<LoanAccountDisbursementUiState>
        get() = _loanAccountDisbursementUiState

    fun loadLoanTemplate(loanId: Int) {
        viewModelScope.launch {
            repository.getLoanTransactionTemplate(loanId, APIEndPoint.DISBURSE)
                .collect { state ->
                    when (state) {
                        is DataState.Error ->
                            _loanAccountDisbursementUiState.value =
                                LoanAccountDisbursementUiState.ShowError(state.message)
                        DataState.Loading ->
                            _loanAccountDisbursementUiState.value =
                                LoanAccountDisbursementUiState.ShowProgressbar
                        is DataState.Success ->
                            _loanAccountDisbursementUiState.value =
                                LoanAccountDisbursementUiState.ShowLoanTransactionTemplate(
                                    state.data,
                                )
                    }
                }
        }
    }

    fun disburseLoan(loanId: Int, loanDisbursement: LoanDisbursement?) {
        viewModelScope.launch {
            repository.disburseLoan(loanId, loanDisbursement)
                .collect { state ->
                    when (state) {
                        is DataState.Error ->
                            _loanAccountDisbursementUiState.value =
                                LoanAccountDisbursementUiState.ShowError(state.message)
                        DataState.Loading ->
                            _loanAccountDisbursementUiState.value = LoanAccountDisbursementUiState.ShowProgressbar
                        is DataState.Success ->
                            _loanAccountDisbursementUiState.value =
                                LoanAccountDisbursementUiState.ShowDisburseLoanSuccessfully(
                                    state.data,
                                )
                    }
                }
        }
    }
}
