/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanCharge

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_failed_to_load_loan_charges
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.domain.useCases.GetListOfLoanChargesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoanChargeViewModel(
    private val getListOfLoanChargesUseCase: GetListOfLoanChargesUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val loanAccountNumber = savedStateHandle.toRoute<LoanChargesScreenRoute>().loanAccountNumber

    private val _loanChargeUiState = MutableStateFlow<LoanChargeUiState>(LoanChargeUiState.Loading)
    val loanChargeUiState = _loanChargeUiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    fun refreshLoanChargeList() {
        _isRefreshing.value = true
        loadLoanChargesList()
        _isRefreshing.value = false
    }

    fun loadLoanChargesList() = viewModelScope.launch {
        getListOfLoanChargesUseCase(loanAccountNumber).collect { result ->
            when (result) {
                is DataState.Error ->
                    _loanChargeUiState.value =
                        LoanChargeUiState.Error(Res.string.feature_loan_failed_to_load_loan_charges)

                is DataState.Loading -> _loanChargeUiState.value = LoanChargeUiState.Loading

                is DataState.Success ->
                    _loanChargeUiState.value =
                        LoanChargeUiState.LoanChargesList(result.data)
            }
        }
    }
}
