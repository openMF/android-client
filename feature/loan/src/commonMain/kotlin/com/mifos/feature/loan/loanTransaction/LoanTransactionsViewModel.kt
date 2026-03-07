/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanTransaction

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

class LoanTransactionsViewModel(
    private val repository: LoanTransactionsRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<LoanTransactionsUiState, LoanTransactionsEvent, LoanTransactionsAction>(
    initialState = LoanTransactionsUiState(),
) {

    private val loanId = savedStateHandle.toRoute<LoanTransactionScreenRoute>().loanAccountNumber

    init {
        loadLoanTransaction()
    }

    override fun handleAction(action: LoanTransactionsAction) {
        when (action) {
            LoanTransactionsAction.Retry -> loadLoanTransaction()
            LoanTransactionsAction.ShowExportDialog -> mutableStateFlow.update {
                it.copy(isExportDialogOpen = true)
            }
            LoanTransactionsAction.HideExportDialog -> mutableStateFlow.update {
                it.copy(isExportDialogOpen = false)
            }
            LoanTransactionsAction.DismissBottomSheet -> mutableStateFlow.update {
                it.copy(isBottomSheetOpen = false, selectedRow = null)
            }
            is LoanTransactionsAction.OnRowAction -> mutableStateFlow.update {
                it.copy(selectedRow = action.row, isBottomSheetOpen = true)
            }
            is LoanTransactionsAction.OnTransactionAction -> {
                // TODO: Handle the action based on action type and id
                mutableStateFlow.update {
                    it.copy(isBottomSheetOpen = false, selectedRow = null)
                }
            }
            LoanTransactionsAction.NavigateBack -> sendEvent(LoanTransactionsEvent.NavigateBack)
        }
    }

    private fun loadLoanTransaction() {
        viewModelScope.launch {
            repository.getLoanTransactions(loanId).collect { dataState ->
                when (dataState) {
                    is DataState.Error -> mutableStateFlow.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = dataState.message,
                        )
                    }

                    DataState.Loading -> mutableStateFlow.update {
                        it.copy(isLoading = true, errorMessage = null)
                    }

                    is DataState.Success -> {
                        val loanWithAssociations = dataState.data
                        val currencyCode = loanWithAssociations.currency.code
                        val maxDigits = loanWithAssociations.currency.decimalPlaces

                        val transactionsData =
                            loanWithAssociations.transactions.mapIndexed { index, transaction ->

                                LoanTransactionsUiState.LoanTransactionsTableData.TransactionRowData(
                                    number = (index + 1).toString(),
                                    id = transaction.id?.toString() ?: "-",
                                    office = transaction.officeName ?: "-",
                                    // TODO: map from transaction.externalId once the API field is available
                                    externalId = "-",
                                    transactionDate = if (transaction.date.isNotEmpty()) {
                                        DateHelper.getDateAsString(
                                            transaction.date,
                                        )
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

                        mutableStateFlow.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = null,
                                transactionsTableData = LoanTransactionsUiState.LoanTransactionsTableData(
                                    transactions = transactionsData,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}

sealed interface LoanTransactionsAction {
    data object NavigateBack : LoanTransactionsAction
    data object Retry : LoanTransactionsAction
    data object ShowExportDialog : LoanTransactionsAction
    data object HideExportDialog : LoanTransactionsAction
    data object DismissBottomSheet : LoanTransactionsAction
    data class OnRowAction(
        val row: LoanTransactionsUiState.LoanTransactionsTableData.TransactionRowData,
    ) : LoanTransactionsAction
    data class OnTransactionAction(
        val action: TransactionAction,
        val id: Int,
    ) : LoanTransactionsAction
}

sealed interface LoanTransactionsEvent {
    data object NavigateBack : LoanTransactionsEvent
}
