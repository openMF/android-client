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

sealed class LoanTransactionsUiState {

    data object ShowProgressBar : LoanTransactionsUiState()

    data class ShowFetchingError(val message: String) : LoanTransactionsUiState()

    data class ShowLoanTransaction(
        val transactionsTableData: LoanTransactionsTableData? = null,
        val selectedRow: LoanTransactionsTableData.TransactionRowData? = null,
        val isBottomSheetOpen: Boolean = false,
    ) : LoanTransactionsUiState()

    data class LoanTransactionsTableData(
        val transactions: List<TransactionRowData>,
    ) {
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
