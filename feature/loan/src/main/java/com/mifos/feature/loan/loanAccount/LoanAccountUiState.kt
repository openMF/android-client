/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanAccount

import com.mifos.core.modelobjects.organisations.LoanProducts

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class LoanAccountUiState {

    data object Loading : LoanAccountUiState()

    data class AllLoan(val productLoans: List<LoanProducts>) : LoanAccountUiState()

    data class Error(val message: Int) : LoanAccountUiState()

    data object LoanAccountCreatedSuccessfully : LoanAccountUiState()
}
