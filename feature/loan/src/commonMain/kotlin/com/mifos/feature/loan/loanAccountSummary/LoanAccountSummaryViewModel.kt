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
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.LoanAccountSummaryRepository
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

internal class LoanAccountSummaryViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: LoanAccountSummaryRepository,
) : BaseViewModel<LoanAccountSummaryState, LoanAccountSummaryEvent, LoanAccountSummaryAction>(
    initialState = LoanAccountSummaryState(),
) {
    private val loanAccountNumber =
        savedStateHandle.toRoute<LoanAccountSummaryScreenRoute>().loanAccountNumber

    init {
        loadLoanById()
    }

    override fun handleAction(action: LoanAccountSummaryAction) {
        when (action) {
            LoanAccountSummaryAction.OnRetry -> loadLoanById()
            LoanAccountSummaryAction.NavigateBack -> sendEvent(LoanAccountSummaryEvent.NavigateBack)
            is LoanAccountSummaryAction.OnMoreInfoClick -> {
                sendEvent(LoanAccountSummaryEvent.NavigateToMoreInfo(loanAccountNumber))
            }
            is LoanAccountSummaryAction.OnTransactionsClick -> {
                sendEvent(LoanAccountSummaryEvent.NavigateToTransactions(loanAccountNumber))
            }
            is LoanAccountSummaryAction.OnRepaymentScheduleClick -> {
                sendEvent(LoanAccountSummaryEvent.NavigateToRepaymentSchedule(loanAccountNumber))
            }
            is LoanAccountSummaryAction.OnDocumentsClick -> {
                sendEvent(LoanAccountSummaryEvent.NavigateToDocuments(loanAccountNumber))
            }
            is LoanAccountSummaryAction.OnChargesClick -> {
                sendEvent(LoanAccountSummaryEvent.NavigateToCharges(loanAccountNumber))
            }
            is LoanAccountSummaryAction.OnApproveLoan -> {
                sendEvent(
                    LoanAccountSummaryEvent.NavigateToApproveLoan(
                        loanAccountNumber,
                        action.loanWithAssociations,
                    ),
                )
            }
            is LoanAccountSummaryAction.OnDisburseLoan -> {
                sendEvent(LoanAccountSummaryEvent.NavigateToDisburseLoan(loanAccountNumber))
            }
            is LoanAccountSummaryAction.OnMakeRepayment -> {
                sendEvent(LoanAccountSummaryEvent.NavigateToMakeRepayment(action.loanWithAssociations))
            }
            LoanAccountSummaryAction.OnLoanIdCopied -> {
                mutableStateFlow.update { it.copy(showLoanIdCopiedMessage = true) }
            }
            LoanAccountSummaryAction.OnMessageShown -> {
                mutableStateFlow.update { it.copy(showLoanIdCopiedMessage = false) }
            }
        }
    }

    private fun loadLoanById() {
        viewModelScope.launch {
            mutableStateFlow.update { it.copy(dialogState = LoanAccountSummaryState.DialogState.Loading) }

            repository.getLoanById(loanAccountNumber).collect { dataState ->
                when (dataState) {
                    is DataState.Loading -> {
                        mutableStateFlow.update { it.copy(dialogState = LoanAccountSummaryState.DialogState.Loading) }
                    }
                    is DataState.Success -> {
                        mutableStateFlow.update {
                            it.copy(
                                loanWithAssociations = dataState.data,
                                dialogState = null,
                            )
                        }
                    }
                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = LoanAccountSummaryState.DialogState.Error(
                                    getString(Res.string.feature_loan_unknown_error_occured),
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}
