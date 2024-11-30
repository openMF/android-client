/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanAccountSummary

import com.mifos.core.objects.accounts.loan.LoanWithAssociations

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class LoanAccountSummaryUiState {

    data object ShowProgressbar : LoanAccountSummaryUiState()

    data class ShowFetchingError(val message: String) : LoanAccountSummaryUiState()

    data class ShowLoanById(val loanWithAssociations: LoanWithAssociations) : LoanAccountSummaryUiState()
}
