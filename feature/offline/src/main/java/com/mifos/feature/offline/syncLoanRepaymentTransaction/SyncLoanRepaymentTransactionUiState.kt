/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.offline.syncLoanRepaymentTransaction

import com.mifos.core.objects.PaymentTypeOption
import com.mifos.core.objects.accounts.loan.LoanRepaymentRequest

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class SyncLoanRepaymentTransactionUiState {

    data object ShowProgressbar : SyncLoanRepaymentTransactionUiState()

    data class ShowError(val message: Int) : SyncLoanRepaymentTransactionUiState()

    data class ShowLoanRepaymentTransactions(
        val loanRepaymentRequests: List<LoanRepaymentRequest>,
        val paymentTypeOptions: List<PaymentTypeOption>,
    ) : SyncLoanRepaymentTransactionUiState()

    data class ShowEmptyLoanRepayments(val message: String) : SyncLoanRepaymentTransactionUiState()
}
