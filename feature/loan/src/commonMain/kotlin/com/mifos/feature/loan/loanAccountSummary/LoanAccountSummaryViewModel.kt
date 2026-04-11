/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
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
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

internal class LoanAccountSummaryViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: LoanAccountSummaryRepository,
) : BaseViewModel<LoanAccountSummaryState, LoanAccountSummaryEvent, LoanAccountSummaryAction>(
    initialState = LoanAccountSummaryState(),
) {
    private val loanId =
        savedStateHandle.toRoute<LoanAccountSummaryScreenRoute>().loanId

    init {
        loadLoanById()
    }

    override fun handleAction(action: LoanAccountSummaryAction) {
        when (action) {
            LoanAccountSummaryAction.OnRetry -> {
                loadLoanById()
            }
            LoanAccountSummaryAction.NavigateBack -> {
                sendEvent(LoanAccountSummaryEvent.NavigateBack)
            }
            is LoanAccountSummaryAction.OnMoreInfoClick -> {
                sendEvent(LoanAccountSummaryEvent.NavigateToMoreInfo(loanId))
            }

            is LoanAccountSummaryAction.OnTransactionsClick -> {
                sendEvent(LoanAccountSummaryEvent.NavigateToTransactions(loanId))
            }

            is LoanAccountSummaryAction.OnRepaymentScheduleClick -> {
                sendEvent(LoanAccountSummaryEvent.NavigateToRepaymentSchedule(loanId))
            }

            is LoanAccountSummaryAction.OnDocumentsClick -> {
                sendEvent(LoanAccountSummaryEvent.NavigateToDocuments(loanId))
            }

            is LoanAccountSummaryAction.OnChargesClick -> {
                sendEvent(LoanAccountSummaryEvent.NavigateToCharges(loanId))
            }

            LoanAccountSummaryAction.OnApproveLoan -> {
                state.loanWithAssociations?.let { loan ->
                    sendEvent(
                        LoanAccountSummaryEvent.NavigateToApproveLoan(
                            loanId,
                            loan,
                        ),
                    )
                }
            }

            LoanAccountSummaryAction.OnDisburseLoan -> {
                sendEvent(LoanAccountSummaryEvent.NavigateToDisburseLoan(loanId))
            }

            LoanAccountSummaryAction.OnMakeRepayment -> {
                state.loanWithAssociations?.let { loan ->
                    sendEvent(LoanAccountSummaryEvent.NavigateToMakeRepayment(loan))
                }
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

            is LoanAccountSummaryAction.DropdownAction -> {
                mutableStateFlow.update { it.copy(openDropdown = false) }
                handleDropdownAction(action.action)
            }

            LoanAccountSummaryAction.NavigateToLoanTransfer -> { }
        }
    }

    private fun handleDropdownAction(action: LoanSummaryDropDownAction) {
        when (action) {
            LoanSummaryDropDownAction.OnMoreInfoClick ->
                sendEvent(LoanAccountSummaryEvent.NavigateToMoreInfo(loanId))

            LoanSummaryDropDownAction.OnTransactionsClick ->
                sendEvent(LoanAccountSummaryEvent.NavigateToTransactions(loanId))

            LoanSummaryDropDownAction.OnRepaymentScheduleClick ->
                sendEvent(LoanAccountSummaryEvent.NavigateToRepaymentSchedule(loanId))

            LoanSummaryDropDownAction.OnDocumentsClick ->
                sendEvent(LoanAccountSummaryEvent.NavigateToDocuments(loanId))

            LoanSummaryDropDownAction.OnChargesClick ->
                sendEvent(LoanAccountSummaryEvent.NavigateToCharges(loanId))
        }
    }

    private fun loadLoanById() {
        viewModelScope.launch {
            mutableStateFlow.update { it.copy(dialogState = LoanAccountSummaryState.DialogState.Loading) }

            repository.getLoanById(loanId).collect { dataState ->
                when (dataState) {
                    is DataState.Loading -> {
                        mutableStateFlow.update { it.copy(dialogState = LoanAccountSummaryState.DialogState.Loading) }
                    }

                    is DataState.Success -> {
                        val loan: LoanWithAssociationsEntity? = dataState.data
                        if (loan != null) {
                            fillLoanSummary(loan)
                        } else {
                            val errorMessage =
                                getString(Res.string.feature_loan_unknown_error_occured)
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = LoanAccountSummaryState.DialogState.Error(
                                        errorMessage,
                                    ),
                                )
                            }
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

    private fun fillLoanSummary(loan: LoanWithAssociationsEntity) {
        val actualDisbursementDate = formatActualDisbursementDate(
            loan.timeline?.actualDisbursementDate,
        )

        val shouldInflateLoanSummary = loan.status?.shouldInflateLoanSummary() ?: false
        // dataTable should be empty if [inflateLoanSummary] is false
        val summary = if (shouldInflateLoanSummary) loan.summary else null

        val currencyCode = loan.currency?.code
        val decimalPlaces = loan.currency?.decimalPlaces

        mutableStateFlow.update {
            it.copy(
                loanWithAssociations = loan,
                actualDisbursementDate = actualDisbursementDate,
                dialogState = null,
                inflateLoanSummary = shouldInflateLoanSummary,

                totalLoanFormat = formatCurrency(
                    summary?.totalExpectedRepayment,
                    currencyCode,
                    decimalPlaces,
                ),
                loanAmountPaid = formatCurrency(
                    summary?.totalRepayment,
                    currencyCode,
                    decimalPlaces,
                ),
                outstandingAmount = formatCurrency(
                    summary?.totalOutstanding,
                    currencyCode,
                    decimalPlaces,
                ),
                overdueAmount = formatCurrency(summary?.totalOverdue, currencyCode, decimalPlaces),

                principalDisbursed = formatCurrency(
                    summary?.principalDisbursed,
                    currencyCode,
                    decimalPlaces,
                ),
                principalPaid = formatCurrency(summary?.principalPaid, currencyCode, decimalPlaces),
                principalOutStanding = formatCurrency(
                    summary?.principalOutstanding,
                    currencyCode,
                    decimalPlaces,
                ),

                interestCharged = formatCurrency(
                    summary?.interestCharged,
                    currencyCode,
                    decimalPlaces,
                ),
                interestPaid = formatCurrency(summary?.interestPaid, currencyCode, decimalPlaces),
                interestOutstanding = formatCurrency(
                    summary?.interestOutstanding,
                    currencyCode,
                    decimalPlaces,
                ),

                feeChargesCharged = formatCurrency(
                    summary?.feeChargesCharged,
                    currencyCode,
                    decimalPlaces,
                ),
                feeChargesPaid = formatCurrency(
                    summary?.feeChargesPaid,
                    currencyCode,
                    decimalPlaces,
                ),
                feeChargesOutstanding = formatCurrency(
                    summary?.feeChargesOutstanding,
                    currencyCode,
                    decimalPlaces,
                ),

                penaltyChargesCharged = formatCurrency(
                    summary?.penaltyChargesCharged,
                    currencyCode,
                    decimalPlaces,
                ),
                penaltyChargesPaid = formatCurrency(
                    summary?.penaltyChargesPaid,
                    currencyCode,
                    decimalPlaces,
                ),
                penaltyChargesOutstanding = formatCurrency(
                    summary?.penaltyChargesOutstanding,
                    currencyCode,
                    decimalPlaces,
                ),
            )
        }
    }

    private fun formatActualDisbursementDate(date: List<Int?>?): String {
        return if (date != null && date.size >= 3 && date.all { it != null }) {
            @Suppress("UNCHECKED_CAST")
            DateHelper.getDateAsString(date as List<Int>)
        } else {
            ""
        }
    }

    private fun LoanStatusEntity.shouldInflateLoanSummary(): Boolean {
        return active == true || closedObligationsMet == true || overpaid == true
    }

    private fun formatCurrency(
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
}

data class LoanAccountSummaryState(
    val loanWithAssociations: LoanWithAssociationsEntity? = null,
    val dialogState: DialogState? = null,
    val showLoanIdCopiedMessage: Boolean = false,
    val openDropdown: Boolean = false,
    val actualDisbursementDate: String = "",
    val inflateLoanSummary: Boolean = false,

    val totalLoanFormat: String = "",
    val loanAmountPaid: String = "",
    val outstandingAmount: String = "",
    val overdueAmount: String = "",

    val principalDisbursed: String = "",
    val principalPaid: String = "",
    val principalOutStanding: String = "",

    val interestCharged: String = "",
    val interestPaid: String = "",
    val interestOutstanding: String = "",

    val feeChargesCharged: String = "",
    val feeChargesPaid: String = "",
    val feeChargesOutstanding: String = "",

    val penaltyChargesCharged: String = "",
    val penaltyChargesPaid: String = "",
    val penaltyChargesOutstanding: String = "",
) {
    sealed interface DialogState {
        data object Loading : DialogState
        data class Error(val message: String) : DialogState
    }
}

internal enum class LoanPrimaryAction {
    MAKE_REPAYMENT,
    APPROVE_LOAN,
    DISBURSE_LOAN,
    OVERPAID,
    CLOSED,
}

enum class LoanSummaryDropDownAction {
    OnMoreInfoClick,
    OnTransactionsClick,
    OnRepaymentScheduleClick,
    OnDocumentsClick,
    OnChargesClick,
}

sealed interface LoanAccountSummaryEvent {
    data object NavigateBack : LoanAccountSummaryEvent
    data class NavigateToLoanTransfer(
        val loanId: Int,
        val officeId: Int?,
        val clientId: Int?,
        val currencyCode: String?,
    ) : LoanAccountSummaryEvent
    data class NavigateToMoreInfo(val loanId: Int) : LoanAccountSummaryEvent
    data class NavigateToTransactions(val loanId: Int) : LoanAccountSummaryEvent
    data class NavigateToRepaymentSchedule(val loanId: Int) : LoanAccountSummaryEvent
    data class NavigateToDocuments(val loanId: Int) : LoanAccountSummaryEvent
    data class NavigateToCharges(val loanId: Int) : LoanAccountSummaryEvent
    data class NavigateToApproveLoan(
        val loanId: Int,
        val loanWithAssociations: LoanWithAssociationsEntity,
    ) : LoanAccountSummaryEvent

    data class NavigateToDisburseLoan(val loanId: Int) : LoanAccountSummaryEvent
    data class NavigateToMakeRepayment(val loanWithAssociations: LoanWithAssociationsEntity) :
        LoanAccountSummaryEvent
}

sealed interface LoanAccountSummaryAction {
    data object OnRetry : LoanAccountSummaryAction
    data object NavigateBack : LoanAccountSummaryAction
    data object OnMoreInfoClick : LoanAccountSummaryAction
    data object OnTransactionsClick : LoanAccountSummaryAction
    data object OnRepaymentScheduleClick : LoanAccountSummaryAction
    data object OnDocumentsClick : LoanAccountSummaryAction
    data object OnChargesClick : LoanAccountSummaryAction
    data object OnApproveLoan : LoanAccountSummaryAction
    data object OnDisburseLoan : LoanAccountSummaryAction
    data object OnMakeRepayment : LoanAccountSummaryAction
    data object OnLoanIdCopied : LoanAccountSummaryAction
    data object OnMessageShown : LoanAccountSummaryAction
    data object ToggleDropdown : LoanAccountSummaryAction
    data object NavigateToLoanTransfer : LoanAccountSummaryAction

    data class DropdownAction(val action: LoanSummaryDropDownAction) : LoanAccountSummaryAction
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
