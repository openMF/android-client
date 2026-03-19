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
) : BaseViewModel<LoanTransactionsState, LoanTransactionsEvent, LoanTransactionsAction>(
    initialState = LoanTransactionsState(uiState = LoanTransactionsState.UiState.Loading),
) {

    val loanId = savedStateHandle.toRoute<LoanTransactionScreenRoute>().loanAccountNumber

    init {
        viewModelScope.launch {
            loadLoanTransaction()
        }
    }

    override fun handleAction(action: LoanTransactionsAction) {
        when (action) {
            LoanTransactionsAction.Refresh -> {
                viewModelScope.launch { loadLoanTransaction() }
            }
            LoanTransactionsAction.ShowFilterSheet -> {
                mutableStateFlow.update { it.copy(showFilterSheet = true) }
            }
            LoanTransactionsAction.DismissFilterSheet -> {
                mutableStateFlow.update { it.copy(showFilterSheet = false) }
            }
            is LoanTransactionsAction.SetHideReversed -> {
                mutableStateFlow.update { currentState ->
                    val newHideReversed = action.value
                    val currentUiState = currentState.uiState
                    
                    if (currentUiState is LoanTransactionsState.UiState.Success) {
                        val unfilteredData = currentUiState.unfilteredTransactionsTableData?.transactions ?: emptyList()
                        
                        val filteredTransactions = unfilteredData.filter { row ->
                            val hideReversedCondition = !newHideReversed || !row.manuallyReversed
                            val hideAccrualsCondition = !currentState.hideAccruals || row.transactionType != TransactionType.ACCRUAL
                            hideReversedCondition && hideAccrualsCondition
                        }
                        
                        currentState.copy(
                            hideReversed = newHideReversed,
                            uiState = currentUiState.copy(
                                transactionsTableData = LoanTransactionsTableData(
                                    transactions = filteredTransactions,
                                ),
                            ),
                        )
                    } else {
                        currentState.copy(hideReversed = newHideReversed)
                    }
                }
            }
            is LoanTransactionsAction.SetHideAccruals -> {
                mutableStateFlow.update { currentState ->
                    val newHideAccruals = action.value
                    val currentUiState = currentState.uiState
                    
                    if (currentUiState is LoanTransactionsState.UiState.Success) {
                        val unfilteredData = currentUiState.unfilteredTransactionsTableData?.transactions ?: emptyList()
                        
                        val filteredTransactions = unfilteredData.filter { row ->
                            val hideReversedCondition = !currentState.hideReversed || !row.manuallyReversed
                            val hideAccrualsCondition = !newHideAccruals || row.transactionType != TransactionType.ACCRUAL
                            hideReversedCondition && hideAccrualsCondition
                        }
                        
                        currentState.copy(
                            hideAccruals = newHideAccruals,
                            uiState = currentUiState.copy(
                                transactionsTableData = LoanTransactionsTableData(
                                    transactions = filteredTransactions,
                                ),
                            ),
                        )
                    } else {
                        currentState.copy(hideAccruals = newHideAccruals)
                    }
                }
            }
            LoanTransactionsAction.ExportTransactions -> {
                // TODO: Will be implemented in future PR
            }
            LoanTransactionsAction.ClearFilters -> {
                mutableStateFlow.update { currentState ->
                    val currentUiState = currentState.uiState
                    
                    if (currentUiState is LoanTransactionsState.UiState.Success) {
                        val unfilteredData = currentUiState.unfilteredTransactionsTableData
                        
                        currentState.copy(
                            hideReversed = false,
                            hideAccruals = false,
                            uiState = currentUiState.copy(
                                transactionsTableData = unfilteredData,
                            ),
                        )
                    } else {
                        currentState.copy(
                            hideReversed = false,
                            hideAccruals = false,
                        )
                    }
                }
            }
            is LoanTransactionsAction.SelectRow -> {
                mutableStateFlow.update { currentState ->
                    val currentUiState = currentState.uiState
                    if (currentUiState is LoanTransactionsState.UiState.Success) {
                        currentState.copy(
                            uiState = currentUiState.copy(
                                selectedRow = action.row,
                                isBottomSheetOpen = true,
                            ),
                        )
                    } else {
                        currentState
                    }
                }
            }
            LoanTransactionsAction.DismissBottomSheet -> {
                mutableStateFlow.update { currentState ->
                    val currentUiState = currentState.uiState
                    if (currentUiState is LoanTransactionsState.UiState.Success) {
                        currentState.copy(
                            uiState = currentUiState.copy(
                                isBottomSheetOpen = false,
                                selectedRow = null,
                            ),
                        )
                    } else {
                        currentState
                    }
                }
            }
            is LoanTransactionsAction.OnActionSelected -> {
                // TODO: Handle the action based on action string and id
                mutableStateFlow.update { currentState ->
                    val currentUiState = currentState.uiState
                    if (currentUiState is LoanTransactionsState.UiState.Success) {
                        currentState.copy(
                            uiState = currentUiState.copy(
                                isBottomSheetOpen = false,
                                selectedRow = null,
                            ),
                        )
                    } else {
                        currentState
                    }
                }
            }
        }
    }

    suspend fun loadLoanTransaction() {
        mutableStateFlow.update {
            it.copy(uiState = LoanTransactionsState.UiState.Loading)
        }
        repository.getLoanTransactions(loanId).collect { state ->
            when (state) {
                is DataState.Error -> {
                    mutableStateFlow.update {
                        it.copy(uiState = LoanTransactionsState.UiState.Error(state.message))
                    }
                }

                DataState.Loading -> {
                    mutableStateFlow.update {
                        it.copy(uiState = LoanTransactionsState.UiState.Loading)
                    }
                }

                is DataState.Success -> {
                    val loanWithAssociations = state.data
                    val currencyCode = loanWithAssociations.currency.code
                    val maxDigits = loanWithAssociations.currency.decimalPlaces

                    val transactionsData =
                        loanWithAssociations.transactions.mapIndexed { index, transaction ->

                            LoanTransactionsTableData.TransactionRowData(
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

                    val tableData = LoanTransactionsTableData(transactions = transactionsData)

                    mutableStateFlow.update {
                        it.copy(
                            uiState = LoanTransactionsState.UiState.Success(
                                transactionsTableData = tableData,
                                unfilteredTransactionsTableData = tableData,
                            ),
                        )
                    }
                }
            }
        }
    }
}

data class LoanTransactionsState(
    val hideReversed: Boolean = false,
    val hideAccruals: Boolean = false,
    val showFilterSheet: Boolean = false,
    val uiState: UiState = UiState.Loading,
) {
    sealed interface UiState {
        data object Loading : UiState
        data class Error(val message: String) : UiState
        data class Success(
            val transactionsTableData: LoanTransactionsTableData? = null,
            val unfilteredTransactionsTableData: LoanTransactionsTableData? = null,
            val selectedRow: LoanTransactionsTableData.TransactionRowData? = null,
            val isBottomSheetOpen: Boolean = false,
        ) : UiState
    }
}

sealed interface LoanTransactionsEvent {
    data object NavigateBack : LoanTransactionsEvent
}

sealed interface LoanTransactionsAction {
    data object Refresh : LoanTransactionsAction
    data object ShowFilterSheet : LoanTransactionsAction
    data object DismissFilterSheet : LoanTransactionsAction
    data class SetHideReversed(val value: Boolean) : LoanTransactionsAction
    data class SetHideAccruals(val value: Boolean) : LoanTransactionsAction
    data object ClearFilters : LoanTransactionsAction
    data object ExportTransactions : LoanTransactionsAction
    data class SelectRow(val row: LoanTransactionsTableData.TransactionRowData) : LoanTransactionsAction
    data object DismissBottomSheet : LoanTransactionsAction
    data class OnActionSelected(val action: TransactionAction, val id: Int) : LoanTransactionsAction
}
