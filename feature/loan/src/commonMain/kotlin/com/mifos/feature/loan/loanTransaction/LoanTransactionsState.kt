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

import com.mifos.core.common.utils.DataState
import com.mifos.core.model.objects.account.loan.loanWithAssociations.LoanWithAssociations

data class LoanTransactionsState(
    val viewState: ViewState = ViewState.Loading,
    val selectedRow: TransactionRowData? = null,
    val isBottomSheetOpen: Boolean = false,
    val exportDialogState: ExportDialogState = ExportDialogState(),
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: String) : ViewState
        data class Success(val transactionsTableData: LoanTransactionsTableData) : ViewState
    }

    data class LoanTransactionsTableData(
        val transactions: List<TransactionRowData>,
    )

    data class TransactionRowData(
        val number: String,
        val id: String,
        val office: String,
        val externalId: String,
        val transactionDate: String,
        val transactionType: TransactionType,
        val amount: String,
        val principal: String,
        val interest: String,
        val fees: String,
        val penalties: String,
        val loanBalance: String,
        val manuallyReversed: Boolean = false,
    )
}

data class ExportDialogState(
    val isVisible: Boolean = false,
    val fromDate: Long? = null,
    val toDate: Long? = null,
    val showFromDatePicker: Boolean = false,
    val showToDatePicker: Boolean = false,
) {
    val isInvalidDateRange: Boolean
        get() = if (fromDate != null && toDate != null) {
            toDate < fromDate
        } else {
            false
        }

    val isValidDateRange: Boolean
        get() = fromDate != null && toDate != null && !isInvalidDateRange
}

enum class TransactionType(val value: String) {
    ACCRUAL("Accrual"),
    DISBURSEMENT("Disbursement"),
    REPAYMENT("Repayment"),
    UNKNOWN("-"),
    ;

    companion object {
        fun fromValue(value: String): TransactionType? = entries.find { it.value == value }
    }
}

enum class TransactionAction {
    UNDO_TRANSACTION,
    VIEW_RECEIPTS,
    VIEW_JOURNAL_ENTRIES,
    VIEW_TRANSACTION,
}

sealed interface LoanTransactionsEvent {
    data object NavigateBack : LoanTransactionsEvent
}

sealed interface LoanTransactionsAction {
    data object NavigateBack : LoanTransactionsAction
    data object OnRetry : LoanTransactionsAction
    data object DismissBottomSheet : LoanTransactionsAction
    data class RowSelected(val row: LoanTransactionsState.TransactionRowData) : LoanTransactionsAction
    data class TransactionActionSelected(val action: TransactionAction, val id: Int) : LoanTransactionsAction

    // Export Actions
    data object ExportClicked : LoanTransactionsAction
    data object DismissExportDialog : LoanTransactionsAction
    data object OpenFromDatePicker : LoanTransactionsAction
    data object DismissFromDatePicker : LoanTransactionsAction
    data class FromDateSelected(val dateMillis: Long) : LoanTransactionsAction
    data object OpenToDatePicker : LoanTransactionsAction
    data object DismissToDatePicker : LoanTransactionsAction
    data class ToDateSelected(val dateMillis: Long) : LoanTransactionsAction
    data object GenerateReportClicked : LoanTransactionsAction

    sealed interface Internal : LoanTransactionsAction {
        data class ReceiveTransactionsResult(
            val result: DataState<LoanWithAssociations>,
        ) : Internal
    }
}
