/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanReject

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * State holder for the reject-loan screen.
 */
internal data class RejectLoanViewState(
    val rejectedOnDate: LocalDate = today(),
    val note: String = "",
    val isLoading: Boolean = false,
    val rejectedOnDateError: String? = null,
    val submissionError: String? = null,
    val isSuccess: Boolean = false,
    val showDiscardDialog: Boolean = false,
    val shouldNavigateBack: Boolean = false,
)

@OptIn(ExperimentalTime::class)
internal fun today(): LocalDate {
    return Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
}
