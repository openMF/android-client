/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanAccountGeneral

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_general_summary_row_fees
import androidclient.feature.loan.generated.resources.feature_loan_general_summary_row_interest
import androidclient.feature.loan.generated.resources.feature_loan_general_summary_row_penalties
import androidclient.feature.loan.generated.resources.feature_loan_general_summary_row_principal
import androidclient.feature.loan.generated.resources.feature_loan_general_value_not_available
import androidclient.feature.loan.generated.resources.feature_loan_general_value_unassigned
import androidclient.feature.loan.generated.resources.feature_loan_unknown_error_occured
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.CurrencyFormatter
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.common.utils.Utils
import com.mifos.core.data.repository.LoanAccountGeneralRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

internal class LoanAccountGeneralViewModel(
    savedStateHandle: SavedStateHandle,
    private val networkMonitor: NetworkMonitor,
    private val repository: LoanAccountGeneralRepository,
) : BaseViewModel<LoanAccountGeneralState, LoanAccountGeneralEvent, LoanAccountGeneralAction>(
    initialState = LoanAccountGeneralState(),
) {
    private val loanId = savedStateHandle.toRoute<LoanAccountGeneralRoute>().loanId
    private var loadJob: Job? = null

    init {
        observeNetwork()
        loadLoanById()
    }

    override fun handleAction(action: LoanAccountGeneralAction) {
        when (action) {
            LoanAccountGeneralAction.OnRetry -> loadLoanById()
        }
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isConnected ->
                mutableStateFlow.update { it.copy(networkConnection = isConnected) }
            }
        }
    }

    private fun loadLoanById() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            repository.getLoanById(loanId).collect { dataState ->
                when (dataState) {
                    is DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(dialogState = LoanAccountGeneralState.DialogState.Loading)
                        }
                    }

                    is DataState.Success -> {
                        val loan = dataState.data
                        if (loan != null) {
                            try {
                                fillGeneralState(loan)
                            } catch (e: Exception) {
                                mutableStateFlow.update {
                                    it.copy(dialogState = LoanAccountGeneralState.DialogState.Error(e.message.toString()))
                                }
                            }
                        } else {
                            mutableStateFlow.update {
                                it.copy(dialogState = LoanAccountGeneralState.DialogState.Error(getString(Res.string.feature_loan_unknown_error_occured)))
                            }
                        }
                    }

                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(dialogState = LoanAccountGeneralState.DialogState.Error(getString(Res.string.feature_loan_unknown_error_occured)))
                        }
                    }
                }
            }
        }
    }

    private suspend fun fillGeneralState(loan: LoanWithAssociationsEntity) {
        val currencyCode = loan.currency.code
        val maxDigits = loan.currency.decimalPlaces
        val summary = loan.summary

        val maturityDate = formatDateList(loan.timeline.expectedMaturityDate?.map { it })
        val disbursementDate = formatActualDisbursementDate(loan.timeline.actualDisbursementDate)

        val currencyDisplay = if (!loan.currency.name.isNullOrBlank() && !loan.currency.code.isNullOrBlank()) {
            "${loan.currency.name} ${loan.currency.code}"
        } else {
            ""
        }

        mutableStateFlow.update {
            it.copy(
                dialogState = null,
                numberOfRepayments = loan.numberOfRepayments.toString(),
                maturityDate = maturityDate,
                disbursementDate = disbursementDate,
                loanOfficer = loan.loanOfficerName.ifBlank { getString(Res.string.feature_loan_general_value_unassigned) },
                currency = currencyDisplay,
                loanPurpose = loan.loanPurposeName.ifBlank { getString(Res.string.feature_loan_general_value_not_available) },
                externalId = loan.accountNo.ifBlank { getString(Res.string.feature_loan_general_value_not_available) },
                proposedAmount = CurrencyFormatter.format(loan.approvedPrincipal, currencyCode, maxDigits),
                approvedAmount = CurrencyFormatter.format(loan.approvedPrincipal, currencyCode, maxDigits),
                disbursedAmount = CurrencyFormatter.format(loan.principal, currencyCode, maxDigits),
                summaryRows = listOf(
                    LoanAccountGeneralState.SummaryRowState(
                        component = getString(Res.string.feature_loan_general_summary_row_principal),
                        original = CurrencyFormatter.format(summary.principalDisbursed, currencyCode, maxDigits),
                        paid = CurrencyFormatter.format(summary.principalPaid, currencyCode, maxDigits),
                        waived = CurrencyFormatter.format(0.0, currencyCode, maxDigits),
                        writtenOff = CurrencyFormatter.format(summary.principalWrittenOff, currencyCode, maxDigits),
                        outstanding = CurrencyFormatter.format(summary.principalOutstanding, currencyCode, maxDigits),
                        overDue = CurrencyFormatter.format(summary.principalOverdue, currencyCode, maxDigits),
                    ),
                    LoanAccountGeneralState.SummaryRowState(
                        component = getString(Res.string.feature_loan_general_summary_row_interest),
                        original = CurrencyFormatter.format(summary.interestCharged, currencyCode, maxDigits),
                        paid = CurrencyFormatter.format(summary.interestPaid, currencyCode, maxDigits),
                        waived = CurrencyFormatter.format(summary.interestWaived, currencyCode, maxDigits),
                        writtenOff = CurrencyFormatter.format(summary.interestWrittenOff, currencyCode, maxDigits),
                        outstanding = CurrencyFormatter.format(summary.interestOutstanding, currencyCode, maxDigits),
                        overDue = CurrencyFormatter.format(summary.interestOverdue, currencyCode, maxDigits),
                    ),
                    LoanAccountGeneralState.SummaryRowState(
                        component = getString(Res.string.feature_loan_general_summary_row_fees),
                        original = CurrencyFormatter.format(summary.feeChargesCharged, currencyCode, maxDigits),
                        paid = CurrencyFormatter.format(summary.feeChargesPaid, currencyCode, maxDigits),
                        waived = CurrencyFormatter.format(summary.feeChargesWaived, currencyCode, maxDigits),
                        writtenOff = CurrencyFormatter.format(summary.feeChargesWrittenOff, currencyCode, maxDigits),
                        outstanding = CurrencyFormatter.format(summary.feeChargesOutstanding, currencyCode, maxDigits),
                        overDue = CurrencyFormatter.format(summary.feeChargesOverdue, currencyCode, maxDigits),
                    ),
                    LoanAccountGeneralState.SummaryRowState(
                        component = getString(Res.string.feature_loan_general_summary_row_penalties),
                        original = CurrencyFormatter.format(summary.penaltyChargesCharged, currencyCode, maxDigits),
                        paid = CurrencyFormatter.format(summary.penaltyChargesPaid, currencyCode, maxDigits),
                        waived = CurrencyFormatter.format(summary.penaltyChargesWaived, currencyCode, maxDigits),
                        writtenOff = CurrencyFormatter.format(summary.penaltyChargesWrittenOff, currencyCode, maxDigits),
                        outstanding = CurrencyFormatter.format(summary.penaltyChargesOutstanding, currencyCode, maxDigits),
                        overDue = CurrencyFormatter.format(summary.penaltyChargesOverdue, currencyCode, maxDigits),
                    ),
                ),
                totalOriginal = CurrencyFormatter.format(summary.totalExpectedRepayment, currencyCode, maxDigits),
                totalPaid = CurrencyFormatter.format(summary.totalRepayment, currencyCode, maxDigits),
                totalWaived = CurrencyFormatter.format(summary.totalWaived, currencyCode, maxDigits),
                totalWrittenOff = CurrencyFormatter.format(summary.totalWrittenOff, currencyCode, maxDigits),
                totalOutstanding = CurrencyFormatter.format(summary.totalOutstanding, currencyCode, maxDigits),
                totalOverDue = CurrencyFormatter.format(summary.totalOverdue, currencyCode, maxDigits),
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

    private fun formatDateList(date: List<Int?>?): String {
        if (date == null || date.size < 3) return ""
        return Utils.getStringOfDate(date)
    }
}

data class LoanAccountGeneralState(
    val dialogState: DialogState? = null,
    val networkConnection: Boolean = false,
    val numberOfRepayments: String = "",
    val maturityDate: String = "",
    val summaryRows: List<SummaryRowState> = emptyList(),
    val totalOriginal: String = "",
    val totalPaid: String = "",
    val totalWaived: String = "",
    val totalWrittenOff: String = "",
    val totalOutstanding: String = "",
    val totalOverDue: String = "",
    val disbursementDate: String = "",
    val loanPurpose: String = "",
    val loanOfficer: String = "",
    val currency: String = "",
    val externalId: String = "",
    val proposedAmount: String = "",
    val approvedAmount: String = "",
    val disbursedAmount: String = "",
) {
    sealed interface DialogState {
        data object Loading : DialogState
        data class Error(val message: String) : DialogState
    }

    data class SummaryRowState(
        val component: String,
        val original: String,
        val paid: String,
        val waived: String,
        val writtenOff: String,
        val outstanding: String,
        val overDue: String,
    )
}

sealed interface LoanAccountGeneralEvent

sealed interface LoanAccountGeneralAction {
    data object OnRetry : LoanAccountGeneralAction
}
