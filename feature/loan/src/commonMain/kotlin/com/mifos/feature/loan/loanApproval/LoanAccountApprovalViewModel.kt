/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanApproval

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_unknown_error_occured
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.LoanAccountApprovalRepository
import com.mifos.room.entities.accounts.loans.LoanApprovalData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.getString

class LoanAccountApprovalViewModel(
    private val repository: LoanAccountApprovalRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val arg = savedStateHandle.getStateFlow(key = "arg", initialValue = "")
    private val loanAccountData: LoanApprovalData = Json.decodeFromString<LoanApprovalData>(arg.value)

    private val _loanAccountApprovalUiState =
        MutableStateFlow<LoanAccountApprovalUiState>(LoanAccountApprovalUiState.Initial)
    val loanAccountApprovalUiState: StateFlow<LoanAccountApprovalUiState> get() = _loanAccountApprovalUiState

    var loanId = loanAccountData.loanID
    var loanWithAssociations = loanAccountData.loanWithAssociations

    fun approveLoan(loanApproval: com.mifos.core.model.objects.account.loan.LoanApproval?) {
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
