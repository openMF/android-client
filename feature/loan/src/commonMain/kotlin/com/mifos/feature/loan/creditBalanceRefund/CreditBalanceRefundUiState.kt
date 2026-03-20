/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.creditBalanceRefund

import org.jetbrains.compose.resources.StringResource

sealed class CreditBalanceRefundUiState {

    data object Initial : CreditBalanceRefundUiState()

    data object Loading : CreditBalanceRefundUiState()

    data class ShowRefundForm(
        val loanId: Int,
        val clientName: String,
        val loanAccountNumber: String,
        val overpaidAmount: Double,
        val currencyCode: String?,
        val decimalPlaces: Int?,
    ) : CreditBalanceRefundUiState()

    data class Success(val transactionId: String) : CreditBalanceRefundUiState()

    data class Error(val message: StringResource) : CreditBalanceRefundUiState()
}
