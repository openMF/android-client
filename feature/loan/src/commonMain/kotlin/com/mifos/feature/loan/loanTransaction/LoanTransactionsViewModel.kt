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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoanTransactionsViewModel(
    private val repository: LoanTransactionsRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<LoanTransactionsState, LoanTransactionsEvent, LoanTransactionsAction>(
    initialState = LoanTransactionsState(dialogState = LoanTransactionsState.DialogState.Loading),
) {

    val loanId = savedStateHandle.toRoute<LoanTransactionScreenRoute>().loanAccountNumber

    private val loanTransactionsUiStateFlow =
        MutableStateFlow<LoanTransactionsUiState>(LoanTransactionsUiState.ShowProgressBar)
    val loanTransactionsUiState = loanTransactionsUiStateFlow

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
                mutableStateFlow.update { it.copy(hideReversed = action.value) }
                applyFilters()
            }
            is LoanTransactionsAction.SetHideAccruals -> {
                mutableStateFlow.update { it.copy(hideAccruals = action.value) }
                applyFilters()
            }
        }
    }

    suspend fun loadLoanTransaction() {
        mutableStateFlow.update {
            it.copy(dialogState = LoanTransactionsState.DialogState.Loading)
        }
        repository.getLoanTransactions(loanId).collect { state ->
            when (state) {
                is DataState.Error -> {
                    mutableStateFlow.update {
                        it.copy(dialogState = LoanTransactionsState.DialogState.Error(state.message))
                    }
                    loanTransactionsUiStateFlow.value =
                        LoanTransactionsUiState.ShowFetchingError(state.message)
                }

                DataState.Loading -> {
                    mutableStateFlow.update {
                        it.copy(dialogState = LoanTransactionsState.DialogState.Loading)
                    }
                    loanTransactionsUiStateFlow.value = LoanTransactionsUiState.ShowProgressBar
                }

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

                    loanTransactionsUiStateFlow.value =
                        LoanTransactionsUiState.ShowLoanTransaction(
                            transactionsTableData = LoanTransactionsUiState.LoanTransactionsTableData(
                                transactions = transactionsData,
                            ),
                        )

                    mutableStateFlow.update { it.copy(dialogState = null) }
                    applyFilters()
                }
            }
        }
    }

    private fun applyFilters() {
        val currentUiState = loanTransactionsUiStateFlow.value
        if (currentUiState is LoanTransactionsUiState.ShowLoanTransaction) {
            val currentState = stateFlow.value
            val allTransactions = currentUiState.transactionsTableData?.transactions ?: emptyList()

            val filteredTransactions = allTransactions.filter { row ->
                val hideReversedCondition = !currentState.hideReversed || !row.manuallyReversed
                val hideAccrualsCondition = !currentState.hideAccruals || row.transactionType != TransactionType.ACCRUAL
                hideReversedCondition && hideAccrualsCondition
            }

            loanTransactionsUiStateFlow.value = currentUiState.copy(
                transactionsTableData = LoanTransactionsUiState.LoanTransactionsTableData(
                    transactions = filteredTransactions,
                ),
            )
        }
    }

    fun onRowAction(row: LoanTransactionsUiState.LoanTransactionsTableData.TransactionRowData) {
        val currentState = loanTransactionsUiStateFlow.value
        if (currentState is LoanTransactionsUiState.ShowLoanTransaction) {
            loanTransactionsUiStateFlow.value =
                currentState.copy(selectedRow = row, isBottomSheetOpen = true)
        }
    }

    fun dismissBottomSheet() {
        val currentState = loanTransactionsUiStateFlow.value
        if (currentState is LoanTransactionsUiState.ShowLoanTransaction) {
            loanTransactionsUiStateFlow.value =
                currentState.copy(isBottomSheetOpen = false, selectedRow = null)
        }
    }

    fun onActionSelected(action: TransactionAction, id: Int) {
        // TODO: Handle the action based on action string and id
        dismissBottomSheet()
    }
}

data class LoanTransactionsState(
    val hideReversed: Boolean = false,
    val hideAccruals: Boolean = false,
    val showFilterSheet: Boolean = false,
    val dialogState: DialogState? = null,
) {
    sealed interface DialogState {
        data object Loading : DialogState
        data class Error(val message: String) : DialogState
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
}
