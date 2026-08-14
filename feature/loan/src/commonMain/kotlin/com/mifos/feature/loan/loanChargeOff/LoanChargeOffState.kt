/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanChargeOff

import com.mifos.core.common.utils.DateHelper
import com.mifos.core.model.objects.account.loan.ChargeOffReasonOption
import org.jetbrains.compose.resources.StringResource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class LoanChargeOffState
@OptIn(ExperimentalTime::class)
constructor(
    val loanId: Int,
    val viewState: ViewState = ViewState.Loading,
    val selectedReason: ChargeOffReasonOption? = null,
    val transactionDate: Long = defaultTransactionDate,
    val transactionDateText: String = DateHelper.getDateAsStringFromLong(defaultTransactionDate),
    val externalId: String = "",
    val note: String = "",
    val showDatePicker: Boolean = false,
    val isReasonError: Boolean = false,
    val isSubmitting: Boolean = false,
    val dialogMessage: StringResource? = null,
    val isChargeOffSuccessful: Boolean = false,
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: StringResource) : ViewState
        data class Success(val reasonOptions: List<ChargeOffReasonOption>) : ViewState
    }
}

private val defaultTransactionDate = Clock.System.now().toEpochMilliseconds()
