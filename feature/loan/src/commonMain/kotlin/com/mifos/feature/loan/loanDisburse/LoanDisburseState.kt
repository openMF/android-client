/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanDisburse

import com.mifos.core.model.objects.account.loan.loanDisburse.LoanDisburseTemplate
import com.mifos.core.model.objects.account.loan.loanWithAssociations.PaymentType
import org.jetbrains.compose.resources.StringResource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class LoanDisburseState
@OptIn(ExperimentalTime::class)
constructor(
    val loanId: Int,
    val viewState: ViewState = ViewState.Loading,
    val disbursedDate: Long = defaultDisbursedDate,
    val disbursedDateText: String = "",
    val minDisbursementDate: Long = 0L,
    val transactionAmount: Double = 0.0,
    val netDisbursalAmount: Double = 0.0,
    val availableAmount: String = "",
    val transactionAmountError: StringResource? = null,
    val externalId: String = "",
    val selectedPaymentType: PaymentType? = null,
    val paymentTypes: List<PaymentType> = emptyList(),
    val showPaymentDetails: Boolean = false,
    val accountNumber: String = "",
    val chequeNumber: String = "",
    val routingCode: String = "",
    val receiptNumber: String = "",
    val bankNumber: String = "",
    val note: String = "",
    val showDatePicker: Boolean = false,
    val isSubmitting: Boolean = false,
    val dialogMessage: StringResource? = null,
    val isDisburseSuccessful: Boolean = false,
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: StringResource) : ViewState
        data class Success(val template: LoanDisburseTemplate) : ViewState
    }
}

private val defaultDisbursedDate = Clock.System.now().toEpochMilliseconds()
