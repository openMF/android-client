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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.data.repository.LoanAccountApprovalRepository
import com.mifos.room.entities.accounts.loans.LoanApprovalData
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

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
            repository.approveLoan(loanId, loanApproval)
                .catch {
                    when (it) {
                        is ClientRequestException, is ServerResponseException -> {
                            _loanAccountApprovalUiState.value =
                                LoanAccountApprovalUiState.ShowLoanApproveFailed(
                                    it.message ?: "Server error occurred",
                                )
                        }
                        is IOException -> {
                            _loanAccountApprovalUiState.value =
                                LoanAccountApprovalUiState.ShowLoanApproveFailed(
                                    it.message ?: "Network error occurred",
                                )
                        }
                        is SerializationException -> {
                            _loanAccountApprovalUiState.value =
                                LoanAccountApprovalUiState.ShowLoanApproveFailed(
                                    it.message ?: "Data parsing error",
                                )
                        }
                        else -> {
                            _loanAccountApprovalUiState.value =
                                LoanAccountApprovalUiState.ShowLoanApproveFailed(
                                    it.message ?: "Unknown error",
                                )
                        }
                    }
                }
                .collect {
                    _loanAccountApprovalUiState.value = it.data?.let { genericResponse ->
                        LoanAccountApprovalUiState.ShowLoanApproveSuccessfully(genericResponse)
                    } ?: LoanAccountApprovalUiState.ShowLoanApproveFailed("Something went wrong")
                }
        }
    }
}
