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

import com.mifos.room.entities.accounts.loans.LoanWithAssociations

/**
 * Created by Aditya Gupta on 12/08/23.
 */
sealed class LoanTransactionsUiState {

    data object ShowProgressBar : LoanTransactionsUiState()

    data class ShowFetchingError(val message: String) : LoanTransactionsUiState()

    data class ShowLoanTransaction(val loanWithAssociations: LoanWithAssociations) :
        LoanTransactionsUiState()
}
