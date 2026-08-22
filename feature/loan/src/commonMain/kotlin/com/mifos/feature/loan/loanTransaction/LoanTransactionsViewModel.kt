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

import kpt.feature.loan.generated.resources.Res
import kpt.feature.loan.generated.resources.feature_loan_value_not_available
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.CurrencyFormatter
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.repository.loan.LoanTransactionsRepository
import com.mifos.core.model.objects.account.loan.loanWithAssociations.LoanWithAssociations
import kpt.core.base.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

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

    private var loadTransactionsJob: Job? = null

    private fun loadLoanTransaction() {
        loadTransactionsJob?.cancel()
        loadTransactionsJob = viewModelScope.launch {
            val notAvailableString = getString(Res.string.feature_loan_value_not_available)
            mutableStateFlow.update {
                it.copy(viewState = LoanTransactionsState.ViewState.Loading)
            }
            repository.getLoanTransactions(loanId)
                .catch { error ->
                    mutableStateFlow.update {
                        it.copy(
                            viewState = LoanTransactionsState.ViewState.Error(
                                error.message ?: notAvailableString,
                            ),
                        )
                    }
                }
                .collect { loanWithAssociations ->
                    sendAction(
                        LoanTransactionsAction.Internal.ReceiveTransactionsResult(
                            loanWithAssociations,
                            notAvailableString,
                        ),
                    )
                }
        }
    }

    private fun handleTransactionsResult(result: LoanWithAssociations, notAvailableString: String) {
        val loanWithAssociations = result
        val currencyCode = loanWithAssociations.currency?.code
        val maxDigits = loanWithAssociations.currency?.decimalPlaces

                val transactionsData =
                    loanWithAssociations.transactions?.mapIndexed { index, transaction ->
                        LoanTransactionsState.TransactionRowData(
                            number = (index + 1).toString(),
                            id = transaction.id?.toString() ?: notAvailableString,
                            office = transaction.officeName ?: notAvailableString,
                            externalId = notAvailableString,
                            transactionDate = if (transaction.date.isNotEmpty()) {
                                DateHelper.getDateAsString(transaction.date)
                            } else {
                                notAvailableString
                            },
                            transactionType = transaction.type?.value?.let {
                                TransactionType.fromValue(it)
                            } ?: TransactionType.UNKNOWN,
                            amount = transaction.amount?.let {
                                CurrencyFormatter.format(
                                    it,
                                    currencyCode,
                                    maxDigits,
                                )
                            } ?: notAvailableString,
                            principal = transaction.principalPortion?.let {
                                CurrencyFormatter.format(
                                    it,
                                    currencyCode,
                                    maxDigits,
                                )
                            } ?: notAvailableString,
                            interest = transaction.interestPortion?.let {
                                CurrencyFormatter.format(
                                    it,
                                    currencyCode,
                                    maxDigits,
                                )
                            } ?: notAvailableString,
                            fees = transaction.feeChargesPortion?.let {
                                CurrencyFormatter.format(
                                    it,
                                    currencyCode,
                                    maxDigits,
                                )
                            } ?: notAvailableString,
                            penalties = transaction.penaltyChargesPortion?.let {
                                CurrencyFormatter.format(
                                    it,
                                    currencyCode,
                                    maxDigits,
                                )
                            } ?: notAvailableString,
                            loanBalance = transaction.outstandingLoanBalance?.let {
                                CurrencyFormatter.format(
                                    it,
                                    currencyCode,
                                    maxDigits,
                                )
                            } ?: notAvailableString,
                            manuallyReversed = transaction.manuallyReversed ?: false,
                        )
                    }

                mutableStateFlow.update {
                    it.copy(
                        viewState = LoanTransactionsState.ViewState.Success(
                            transactionsTableData = LoanTransactionsState.LoanTransactionsTableData(
                                transactions = transactionsData ?: emptyList(),
                            ),
                        ),
                    )
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
                handleTransactionsResult(action.result, action.notAvailableString)
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
                // TODO: Implement report generation
                handleAction(LoanTransactionsAction.DismissExportDialog)
            }
        }
    }
}
