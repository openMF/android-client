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

    fun getPrimaryAction(status: com.mifos.room.entities.accounts.loans.LoanStatusEntity): LoanPrimaryAction {
        return when {
            status.active == true -> LoanPrimaryAction.MAKE_REPAYMENT
            status.closedObligationsMet == true -> LoanPrimaryAction.MAKE_REPAYMENT
            status.pendingApproval == true -> LoanPrimaryAction.APPROVE_LOAN
            status.waitingForDisbursal == true -> LoanPrimaryAction.DISBURSE_LOAN
            status.overpaid == true -> LoanPrimaryAction.OVERPAID
            else -> LoanPrimaryAction.CLOSED
        }
    }

    fun getButtonActiveStatus(status: com.mifos.room.entities.accounts.loans.LoanStatusEntity): Boolean {
        return when {
            status.active == true || status.pendingApproval == true || status.waitingForDisbursal == true -> {
                true
            }

            else -> {
                false
            }
        }
    }

    fun getInflateLoanSummaryValue(status: com.mifos.room.entities.accounts.loans.LoanStatusEntity): Boolean {
        return when {
            status.active == true || status.closedObligationsMet == true -> {
                true
            }

            status.pendingApproval == true || status.waitingForDisbursal == true -> {
                false
            }

            else -> {
                true
            }
        }
    }

    fun formatCurrency(
        amount: Double?,
        currencyCode: String?,
        decimalPlaces: Int?,
    ): String {
        if (amount == null) return ""
        if (currencyCode.isNullOrBlank()) return amount.toString()

        return com.mifos.core.common.utils.CurrencyFormatter.format(
            balance = amount,
            currencyCode = currencyCode,
            maximumFractionDigits = decimalPlaces,
        )
    }

    fun formatAmount(
        amount: Double?,
        currencyCode: String?,
        decimalPlaces: Int?,
    ): String {
        if (amount == null) return ""
        if (currencyCode.isNullOrBlank()) return amount.toString()

        return com.mifos.core.common.utils.CurrencyFormatter.format(
            balance = amount,
            currencyCode = currencyCode,
            maximumFractionDigits = decimalPlaces,
        )
    }

    suspend fun getActualDisbursementDateInStringFormat(
        actualDisbursementDate: Any?,
    ): String {
        return try {
            (actualDisbursementDate as? List<*>)?.filterIsInstance<Int>()?.let { dateList ->
                if (dateList.size >= 3) {
                    com.mifos.core.common.utils.DateHelper.getDateAsString(dateList)
                } else {
                    ""
                }
            } ?: ""
        } catch (exception: Exception) {
            ""
        }
    }
}
