/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanTransaction

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.CurrencyFormatter
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.repository.LoanTransactionsRepository
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class LoanTransactionsViewModel(
    private val repository: LoanTransactionsRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<LoanTransactionsState, LoanTransactionsEvent, LoanTransactionsAction>(
    initialState = LoanTransactionsState(),
) {

    private val loanId = savedStateHandle.toRoute<LoanTransactionScreenRoute>().loanAccountNumber

    init {
        loadLoanTransactions()
    }

    override fun handleAction(action: LoanTransactionsAction) {
        when (action) {
            LoanTransactionsAction.Retry -> loadLoanTransactions()
            LoanTransactionsAction.NavigateBack -> sendEvent(LoanTransactionsEvent.NavigateBack)

            is LoanTransactionsAction.RowSelected -> {
                mutableStateFlow.update { it.copy(selectedRow = action.row) }
            }

            LoanTransactionsAction.DismissBottomSheet -> {
                mutableStateFlow.update { it.copy(selectedRow = null) }
            }

            is LoanTransactionsAction.TransactionActionClicked -> {
                // TODO: Handle the action based on action type and id
                mutableStateFlow.update { it.copy(selectedRow = null) }
            }

            LoanTransactionsAction.ExportClicked -> {
                mutableStateFlow.update {
                    it.copy(exportDialogState = ExportDialogState(isVisible = true))
                }
            }

            LoanTransactionsAction.DismissExportDialog -> {
                mutableStateFlow.update { it.copy(exportDialogState = ExportDialogState()) }
            }

            is LoanTransactionsAction.FromDateSelected -> {
                mutableStateFlow.update {
                    it.copy(
                        exportDialogState = it.exportDialogState.copy(fromDate = action.date),
                    )
                }
            }

            is LoanTransactionsAction.ToDateSelected -> {
                mutableStateFlow.update {
                    it.copy(
                        exportDialogState = it.exportDialogState.copy(toDate = action.date),
                    )
                }
            }

            is LoanTransactionsAction.ShowFromDatePicker -> {
                mutableStateFlow.update {
                    it.copy(
                        exportDialogState = it.exportDialogState.copy(
                            showFromDatePicker = action.show,
                        ),
                    )
                }
            }

            is LoanTransactionsAction.ShowToDatePicker -> {
                mutableStateFlow.update {
                    it.copy(
                        exportDialogState = it.exportDialogState.copy(
                            showToDatePicker = action.show,
                        ),
                    )
                }
            }

            LoanTransactionsAction.GenerateReportClicked -> {
                // Placeholder for future API call. UI-only ticket.
                mutableStateFlow.update { it.copy(exportDialogState = ExportDialogState()) }
            }
        }
    }

    private fun loadLoanTransactions() {
        viewModelScope.launch {
            repository.getLoanTransactions(loanId).collect { dataState ->
                mutableStateFlow.update { currentState ->
                    when (dataState) {
                        is DataState.Error -> currentState.copy(
                            uiState = LoanTransactionsUiState.Error(dataState.message),
                        )

                        DataState.Loading -> currentState.copy(
                            uiState = LoanTransactionsUiState.Loading,
                        )

                        is DataState.Success -> {
                            val loanWithAssociations = dataState.data
                            val currencyCode = loanWithAssociations.currency.code
                            val maxDigits = loanWithAssociations.currency.decimalPlaces

                            val transactionsData =
                                loanWithAssociations.transactions.mapIndexed { index, transaction ->
                                    TransactionRowData(
                                        number = (index + 1).toString(),
                                        id = transaction.id?.toString() ?: "-",
                                        office = transaction.officeName ?: "-",
                                        externalId = "-",
                                        transactionDate = if (transaction.date.isNotEmpty()) {
                                            DateHelper.getDateAsString(transaction.date)
                                        } else {
                                            "-"
                                        },
                                        transactionType = TransactionType.fromValue(
                                            transaction.type?.value ?: "",
                                        ),
                                        amount = CurrencyFormatter.format(
                                            transaction.amount,
                                            currencyCode,
                                            maxDigits,
                                        ),
                                        principal = CurrencyFormatter.format(
                                            transaction.principalPortion,
                                            currencyCode,
                                            maxDigits,
                                        ),
                                        interest = CurrencyFormatter.format(
                                            transaction.interestPortion,
                                            currencyCode,
                                            maxDigits,
                                        ),
                                        fees = CurrencyFormatter.format(
                                            transaction.feeChargesPortion,
                                            currencyCode,
                                            maxDigits,
                                        ),
                                        penalties = CurrencyFormatter.format(
                                            transaction.penaltyChargesPortion,
                                            currencyCode,
                                            maxDigits,
                                        ),
                                        loanBalance = CurrencyFormatter.format(
                                            transaction.outstandingLoanBalance,
                                            currencyCode,
                                            maxDigits,
                                        ),
                                        manuallyReversed = transaction.manuallyReversed ?: false,
                                    )
                                }

                            currentState.copy(
                                uiState = LoanTransactionsUiState.Success(
                                    LoanTransactionsTableData(
                                        transactions = transactionsData,
                                    ),
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Unified UI state for the loan transactions screen.
 */
@Immutable
internal data class LoanTransactionsState(
    val uiState: LoanTransactionsUiState = LoanTransactionsUiState.Loading,
    val selectedRow: TransactionRowData? = null,
    val exportDialogState: ExportDialogState = ExportDialogState(),
)

/**
 * One-shot events emitted by the loan transactions ViewModel.
 */
internal sealed interface LoanTransactionsEvent {
    data object NavigateBack : LoanTransactionsEvent
}

/**
 * User-initiated actions for the loan transactions screen.
 */
internal sealed interface LoanTransactionsAction {
    data object NavigateBack : LoanTransactionsAction
    data object Retry : LoanTransactionsAction
    data class RowSelected(val row: TransactionRowData) : LoanTransactionsAction
    data object DismissBottomSheet : LoanTransactionsAction
    data class TransactionActionClicked(
        val action: TransactionAction,
        val id: Int,
    ) : LoanTransactionsAction

    data object ExportClicked : LoanTransactionsAction
    data object DismissExportDialog : LoanTransactionsAction
    data class FromDateSelected(val date: Long) : LoanTransactionsAction
    data class ToDateSelected(val date: Long) : LoanTransactionsAction
    data class ShowFromDatePicker(val show: Boolean) : LoanTransactionsAction
    data class ShowToDatePicker(val show: Boolean) : LoanTransactionsAction
    data object GenerateReportClicked : LoanTransactionsAction
}

@Immutable
internal data class ExportDialogState(
    val isVisible: Boolean = false,
    val fromDate: Long? = null,
    val toDate: Long? = null,
    val showFromDatePicker: Boolean = false,
    val showToDatePicker: Boolean = false,
) {
    val isValidDateRange: Boolean
        get() = fromDate != null && toDate != null && toDate >= fromDate

    val isInvalidDateRange: Boolean
        get() = fromDate != null && toDate != null && toDate < fromDate
}
