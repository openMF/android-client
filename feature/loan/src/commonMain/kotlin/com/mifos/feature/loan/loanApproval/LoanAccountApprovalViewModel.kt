/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanApproval

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_unknown_error_occured
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.loan.LoanAccountApprovalRepository
import com.mifos.core.model.objects.account.loan.LoanApproval
import com.mifos.core.model.objects.account.loan.loanWithAssociations.LoanWithAssociations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class LoanAccountApprovalViewModel(
    private val repository: LoanAccountApprovalRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val loanId = savedStateHandle.toRoute<LoanApprovalRoute>().loanId

    private val _loanAccountApprovalUiState =
        MutableStateFlow<LoanAccountApprovalUiState>(LoanAccountApprovalUiState.Initial)
    val loanAccountApprovalUiState: StateFlow<LoanAccountApprovalUiState> get() = _loanAccountApprovalUiState

    val loanWithAssociations: LoanWithAssociations? = null

    fun approveLoan(loanApproval: LoanApproval?) {
        viewModelScope.launch {
            repository.approveLoan(loanId, loanApproval).collect { dataState ->
                when (dataState) {
                    is DataState.Loading -> {
                        _loanAccountApprovalUiState.value =
                            LoanAccountApprovalUiState.ShowProgressbar
                    }

                    is DataState.Success -> {
                        val response = dataState.data
                        _loanAccountApprovalUiState.value =
                            LoanAccountApprovalUiState.ShowLoanApproveSuccessfully(response)
                    }

                    is DataState.Error -> {
                        _loanAccountApprovalUiState.value =
                            LoanAccountApprovalUiState.ShowLoanApproveFailed(
                                getString(Res.string.feature_loan_unknown_error_occured),
                            )
                    }
                }
            }
        }
    }
}
