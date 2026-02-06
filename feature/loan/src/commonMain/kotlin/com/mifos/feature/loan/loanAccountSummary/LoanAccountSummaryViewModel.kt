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
import com.mifos.core.common.utils.CurrencyFormatter
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.repository.LoanAccountSummaryRepository
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.room.entities.accounts.loans.LoanStatusEntity
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
            LoanAccountSummaryAction.ToggleDropdown -> {
                mutableStateFlow.update { it.copy(openDropdown = !it.openDropdown) }
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
                        val actualDisbursementDate = formatActualDisbursementDate(
                            dataState.data?.timeline?.actualDisbursementDate,
                        )
                        mutableStateFlow.update {
                            it.copy(
                                loanWithAssociations = dataState.data,
                                actualDisbursementDate = actualDisbursementDate,
                                dialogState = LoanAccountSummaryState.DialogState.Idle,
                            )
                        }
                    }
                    is DataState.Error -> {
                        val errorMessage = getString(Res.string.feature_loan_unknown_error_occured)
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = LoanAccountSummaryState.DialogState.Error(errorMessage),
                            )
                        }
                    }
                }
            }
        }
    }

    private fun formatActualDisbursementDate(date: List<Int?>?): String {
        return if (date != null && date.isNotEmpty() && date.all { it != null }) {
            @Suppress("UNCHECKED_CAST")
            DateHelper.getDateAsString(date as List<Int>)
        } else {
            ""
        }
    }
}

/**
 * Formats currency amount with proper currency code and decimal places.
 */
internal fun formatCurrency(
    amount: Double?,
    currencyCode: String?,
    decimalPlaces: Int?,
): String {
    if (amount == null) return ""
    if (currencyCode.isNullOrBlank()) return amount.toString()

    return CurrencyFormatter.format(
        balance = amount,
        currencyCode = currencyCode,
        maximumFractionDigits = decimalPlaces,
    )
}

/**
 * Extension function to determine if loan summary data should be displayed.
 * Returns true for active, closed (obligations met), or overpaid loans.
 */
internal fun LoanStatusEntity.shouldInflateLoanSummary(): Boolean {
    return active == true || closedObligationsMet == true || overpaid == true
}

/**
 * Extension function to determine the primary action button for a loan based on its status.
 */
internal fun LoanStatusEntity.getPrimaryAction(): LoanPrimaryAction {
    return when {
        active == true -> LoanPrimaryAction.MAKE_REPAYMENT
        pendingApproval == true -> LoanPrimaryAction.APPROVE_LOAN
        waitingForDisbursal == true -> LoanPrimaryAction.DISBURSE_LOAN
        overpaid == true -> LoanPrimaryAction.OVERPAID
        closedObligationsMet == true -> LoanPrimaryAction.CLOSED
        else -> LoanPrimaryAction.CLOSED
    }
}

/**
 * Extension function to determine if the primary action button should be enabled.
 */
internal fun LoanStatusEntity.isButtonActive(): Boolean {
    return active == true || pendingApproval == true || waitingForDisbursal == true
}
