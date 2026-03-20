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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.CurrencyFormatter
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.repository.LoanTransactionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoanTransactionsViewModel(
    private val repository: LoanTransactionsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val loanId = savedStateHandle.toRoute<LoanTransactionScreenRoute>().loanAccountNumber

    private val _uiState = MutableStateFlow<LoanTransactionsUiState>(LoanTransactionsUiState.Loading)
    val uiState: StateFlow<LoanTransactionsUiState> = _uiState.asStateFlow()

    private val _selectedRow = MutableStateFlow<LoanTransactionsUiState.LoanTransactionsTableData.TransactionRowData?>(null)
    val selectedRow: StateFlow<LoanTransactionsUiState.LoanTransactionsTableData.TransactionRowData?> = _selectedRow.asStateFlow()

    init {
        loadLoanTransactions()
    }

    fun loadLoanTransactions() {
        viewModelScope.launch {
            repository.getLoanTransactions(loanId).collect { dataState ->
                _uiState.value = when (dataState) {
                    is DataState.Error -> LoanTransactionsUiState.Error(dataState.message)
                    DataState.Loading -> LoanTransactionsUiState.Loading
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

                        LoanTransactionsUiState.Success(
                            LoanTransactionsUiState.LoanTransactionsTableData(
                                transactions = transactionsData,
                            ),
                        )
                    }
                }
            }
        }
    }

    fun retry() {
        loadLoanTransactions()
    }

    fun onRowSelected(row: LoanTransactionsUiState.LoanTransactionsTableData.TransactionRowData) {
        _selectedRow.value = row
    }

    fun dismissBottomSheet() {
        _selectedRow.value = null
    }

    fun onTransactionAction(action: TransactionAction, id: Int) {
        // TODO: Handle the action based on action type and id
        _selectedRow.value = null
    }
}
