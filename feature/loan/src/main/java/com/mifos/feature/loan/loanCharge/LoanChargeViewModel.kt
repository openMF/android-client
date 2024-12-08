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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.Resource
import com.mifos.core.domain.useCases.GetListOfLoanChargesUseCase
import com.mifos.feature.loan.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoanChargeViewModel @Inject constructor(
    private val getListOfLoanChargesUseCase: GetListOfLoanChargesUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val loanAccountNumber = savedStateHandle.getStateFlow(key = Constants.LOAN_ACCOUNT_NUMBER, initialValue = 0)

    private val _loanChargeUiState = MutableStateFlow<LoanChargeUiState>(LoanChargeUiState.Loading)
    val loanChargeUiState = _loanChargeUiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    fun refreshLoanChargeList(loanAccountNumber: Int) {
        _isRefreshing.value = true
        loadLoanChargesList(loanAccountNumber = loanAccountNumber)
        _isRefreshing.value = false
    }

    fun loadLoanChargesList(loanAccountNumber: Int) = viewModelScope.launch(Dispatchers.IO) {
        getListOfLoanChargesUseCase(loanAccountNumber).collect { result ->
            when (result) {
                is Resource.Error ->
                    _loanChargeUiState.value =
                        LoanChargeUiState.Error(R.string.feature_loan_failed_to_load_loan_charges)

                is Resource.Loading -> _loanChargeUiState.value = LoanChargeUiState.Loading

                is Resource.Success ->
                    _loanChargeUiState.value =
                        LoanChargeUiState.LoanChargesList(result.data ?: emptyList())
            }
        }
    }
}
