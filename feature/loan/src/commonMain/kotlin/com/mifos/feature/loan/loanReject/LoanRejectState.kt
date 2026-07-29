/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanReject

import com.mifos.core.common.utils.DateHelper
import org.jetbrains.compose.resources.StringResource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class LoanRejectState
@OptIn(ExperimentalTime::class)
constructor(
    val loanId: Int,
    val rejectedOnDate: Long = defaultRejectedOnDate,
    val rejectedOnDateText: String = DateHelper.getDateAsStringFromLong(defaultRejectedOnDate),
    val note: String = "",
    val showDatePicker: Boolean = false,
    val isSubmitting: Boolean = false,
    val dialogMessage: StringResource? = null,
    val isRejectSuccessful: Boolean = false,
)

private val defaultRejectedOnDate = Clock.System.now().toEpochMilliseconds()
