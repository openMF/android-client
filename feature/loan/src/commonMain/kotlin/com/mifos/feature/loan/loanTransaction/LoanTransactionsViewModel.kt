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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.mifos.core.common.utils.CurrencyFormatter
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.repository.LoanTransactionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoanTransactionsViewModel(
    private val repository: LoanTransactionsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val loanId = savedStateHandle.toRoute<LoanTransactionScreenRoute>().loanAccountNumber

    private val _loanTransactionsUiState =
        MutableStateFlow<LoanTransactionsUiState>(LoanTransactionsUiState.ShowProgressBar)
    val loanTransactionsUiState: StateFlow<LoanTransactionsUiState> get() = _loanTransactionsUiState

    suspend fun loadLoanTransaction() {
        repository.getLoanTransactions(loanId).collect { state ->
            when (state) {
                is DataState.Error ->
                    _loanTransactionsUiState.value =
                        LoanTransactionsUiState.ShowFetchingError(state.message)

                DataState.Loading ->
                    _loanTransactionsUiState.value = LoanTransactionsUiState.ShowProgressBar

                is DataState.Success -> {
                    val loanWithAssociations = state.data
                    val currencyCode = loanWithAssociations.currency.code
                    val maxDigits = loanWithAssociations.currency.decimalPlaces

                    val transactionsData =
                        loanWithAssociations.transactions.mapIndexed { index, transaction ->

                            LoanTransactionsUiState.LoanTransactionsTableData.TransactionRowData(
                                number = (index + 1).toString(),
                                id = transaction.id?.toString() ?: "-",
                                office = transaction.officeName ?: "-",
                                externalId = "-",
                                transactionDate = if (transaction.date.isNotEmpty()) {
                                    DateHelper.getDateAsString(
                                        transaction.date,
                                    )
                                } else {
                                    "-"
                                },
                                transactionType = transaction.type?.value?.let {
                                    TransactionType.fromValue(it)
                                } ?: TransactionType.UNKNOWN,
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

                    _loanTransactionsUiState.value =
                        LoanTransactionsUiState.ShowLoanTransaction(
                            transactionsTableData = LoanTransactionsUiState.LoanTransactionsTableData(
                                transactions = transactionsData,
                            ),
                        )
                }
            }
        }
    }

    fun onRowAction(row: LoanTransactionsUiState.LoanTransactionsTableData.TransactionRowData) {
        val currentState = _loanTransactionsUiState.value
        if (currentState is LoanTransactionsUiState.ShowLoanTransaction) {
            _loanTransactionsUiState.value =
                currentState.copy(selectedRow = row, isBottomSheetOpen = true)
        }
    }

    fun dismissBottomSheet() {
        val currentState = _loanTransactionsUiState.value
        if (currentState is LoanTransactionsUiState.ShowLoanTransaction) {
            _loanTransactionsUiState.value =
                currentState.copy(isBottomSheetOpen = false, selectedRow = null)
        }
    }

    fun onActionSelected(action: TransactionAction, id: Int) {
        // TODO: Handle the action based on action string and id
        dismissBottomSheet()
    }
}
