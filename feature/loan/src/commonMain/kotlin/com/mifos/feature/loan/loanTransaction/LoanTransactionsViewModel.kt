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
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.CurrencyFormatter
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.repository.loan.LoanTransactionsRepository
import com.mifos.core.model.objects.account.loan.loanWithAssociations.LoanWithAssociations
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoanTransactionsViewModel(
    private val repository: LoanTransactionsRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<LoanTransactionsState, LoanTransactionsEvent, LoanTransactionsAction>(
    initialState = LoanTransactionsState(),
) {

    val loanId = savedStateHandle.toRoute<LoanTransactionScreenRoute>().loanAccountNumber

    init {
        loadLoanTransaction()
    }

    private fun loadLoanTransaction() {
        viewModelScope.launch {
            repository.getLoanTransactions(loanId).collect { state ->
                sendAction(LoanTransactionsAction.Internal.ReceiveTransactionsResult(state))
            }
        }
    }

    private fun handleTransactionsResult(result: DataState<LoanWithAssociations>) {
        when (result) {
            is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(viewState = LoanTransactionsState.ViewState.Error(result.message))
                }
            }
            DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(viewState = LoanTransactionsState.ViewState.Loading)
                }
            }
            is DataState.Success -> {
                val loanWithAssociations = result.data
                val currencyCode = loanWithAssociations.currency?.code
                val maxDigits = loanWithAssociations.currency?.decimalPlaces

                val transactionsData =
                    loanWithAssociations.transactions?.mapIndexed { index, transaction ->
                        LoanTransactionsState.TransactionRowData(
                            number = (index + 1).toString(),
                            id = transaction.id?.toString() ?: "-",
                            office = transaction.officeName ?: "-",
                            externalId = "-",
                            transactionDate = if (transaction.date.isNotEmpty()) {
                                DateHelper.getDateAsString(transaction.date)
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


                mutableStateFlow.update {
                    it.copy(
                        viewState = LoanTransactionsState.ViewState.Success(
                            transactionsTableData = LoanTransactionsState.LoanTransactionsTableData(
                                transactions = transactionsData,
                            ),
                        ),
                    )
                }
            }
        }
    }

    override fun handleAction(action: LoanTransactionsAction) {
        when (action) {
            LoanTransactionsAction.NavigateBack -> sendEvent(LoanTransactionsEvent.NavigateBack)
            LoanTransactionsAction.OnRetry -> loadLoanTransaction()
            LoanTransactionsAction.DismissBottomSheet -> {
                mutableStateFlow.update {
                    it.copy(isBottomSheetOpen = false, selectedRow = null)
                }
            }
            is LoanTransactionsAction.RowSelected -> {
                mutableStateFlow.update {
                    it.copy(selectedRow = action.row, isBottomSheetOpen = true)
                }
            }
            is LoanTransactionsAction.TransactionActionSelected -> {
                // TODO: Handle the action based on action string and id
                handleAction(LoanTransactionsAction.DismissBottomSheet)
            }
            is LoanTransactionsAction.Internal.ReceiveTransactionsResult -> {
                handleTransactionsResult(action.result)
            }
            LoanTransactionsAction.ExportClicked -> {
                mutableStateFlow.update {
                    it.copy(exportDialogState = it.exportDialogState.copy(isVisible = true))
                }
            }
            LoanTransactionsAction.DismissExportDialog -> {
                mutableStateFlow.update {
                    it.copy(exportDialogState = it.exportDialogState.copy(isVisible = false))
                }
            }
            LoanTransactionsAction.OpenFromDatePicker -> {
                mutableStateFlow.update {
                    it.copy(exportDialogState = it.exportDialogState.copy(showFromDatePicker = true))
                }
            }
            LoanTransactionsAction.DismissFromDatePicker -> {
                mutableStateFlow.update {
                    it.copy(exportDialogState = it.exportDialogState.copy(showFromDatePicker = false))
                }
            }
            is LoanTransactionsAction.FromDateSelected -> {
                mutableStateFlow.update {
                    it.copy(exportDialogState = it.exportDialogState.copy(fromDate = action.dateMillis))
                }
            }
            LoanTransactionsAction.OpenToDatePicker -> {
                mutableStateFlow.update {
                    it.copy(exportDialogState = it.exportDialogState.copy(showToDatePicker = true))
                }
            }
            LoanTransactionsAction.DismissToDatePicker -> {
                mutableStateFlow.update {
                    it.copy(exportDialogState = it.exportDialogState.copy(showToDatePicker = false))
                }
            }
            is LoanTransactionsAction.ToDateSelected -> {
                mutableStateFlow.update {
                    it.copy(exportDialogState = it.exportDialogState.copy(toDate = action.dateMillis))
                }
            }
            LoanTransactionsAction.GenerateReportClicked -> {
                // No backend API call for report generation in this PR (placeholder only)
                // We'll just dismiss the dialog for now.
                handleAction(LoanTransactionsAction.DismissExportDialog)
            }
        }
    }
}
