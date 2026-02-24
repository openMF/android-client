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
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.LoanTransactionsRepository
import com.mifos.core.model.objects.account.loan.Transaction
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoanTransactionsViewModel(
    private val repository: LoanTransactionsRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<LoanTransactionsState, LoanTransactionsEvent, LoanTransactionsAction>(
    initialState = LoanTransactionsState(),
) {

    val loanId = savedStateHandle.toRoute<LoanTransactionScreenRoute>().loanAccountNumber

    private val rawTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    private val hideReversed = MutableStateFlow(false)
    private val hideAccruals = MutableStateFlow(false)

    private val filteredTransactionsFlow: StateFlow<List<Transaction>> = combine(
        rawTransactions,
        hideReversed,
        hideAccruals,
    ) { transactions, hideReversed, hideAccruals ->
        transactions.filter { transaction ->
            val typeValue = transaction.type?.value ?: ""
            val isAccrual = transaction.type?.accrual == true
            when {
                hideReversed && typeValue.equals("Reversed", ignoreCase = true) -> false
                hideAccruals && isAccrual -> false
                else -> true
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList(),
    )

    init {
        loadLoanTransaction()
        viewModelScope.launch {
            filteredTransactionsFlow.collect { filtered ->
                mutableStateFlow.update {
                    it.copy(
                        transactions = filtered,
                        isTransactionsEmpty = filtered.isEmpty(),
                    )
                }
            }
        }
    }

    override fun setInitialState(): LoanTransactionsState {
        return LoanTransactionsState(dialogState = LoanTransactionsState.DialogState.Loading)
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
                hideReversed.value = action.value
                mutableStateFlow.update { it.copy(hideReversed = action.value) }
            }
            is LoanTransactionsAction.SetHideAccruals -> {
                hideAccruals.value = action.value
                mutableStateFlow.update { it.copy(hideAccruals = action.value) }
            }
            is LoanTransactionsAction.Internal.ReceiveTransactionResult -> {
                handleTransactionResult(action.result)
            }
        }
    }

    private fun loadLoanTransaction() {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(dialogState = LoanTransactionsState.DialogState.Loading)
            }
            repository.getLoanTransactions(loanId).collect { dataState ->
                sendAction(LoanTransactionsAction.Internal.ReceiveTransactionResult(dataState))
            }
        }
    }

    private fun handleTransactionResult(result: DataState<LoanWithAssociationsEntity>) {
        when (result) {
            is DataState.Error -> mutableStateFlow.update {
                it.copy(dialogState = LoanTransactionsState.DialogState.Error(result.message))
            }
            DataState.Loading -> mutableStateFlow.update {
                it.copy(dialogState = LoanTransactionsState.DialogState.Loading)
            }
            is DataState.Success -> {
                rawTransactions.value = result.data.transactions ?: emptyList()
                mutableStateFlow.update { it.copy(dialogState = null) }
            }
        }
    }
}

data class LoanTransactionsState(
    val transactions: List<Transaction> = emptyList(),
    val hideReversed: Boolean = false,
    val hideAccruals: Boolean = false,
    val showFilterSheet: Boolean = false,
    val isTransactionsEmpty: Boolean = false,
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

    sealed class Internal : LoanTransactionsAction {
        data class ReceiveTransactionResult(
            val result: DataState<LoanWithAssociationsEntity>,
        ) : Internal()
    }
}
