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

import com.mifos.core.common.utils.DateHelper.today
import kotlinx.datetime.LocalDate

/**
 * State holder for the reject-loan screen.
 */
internal data class RejectLoanViewState(
    val rejectedOnDate: LocalDate = today(),
    val note: String = "",
    val isLoading: Boolean = false,
    val rejectedOnDateError: String? = null,
    val showDiscardDialog: Boolean = false,
    val dialogState: DialogState? = null,
)

/**
 * Dialog states for success/error messages.
 */
internal sealed interface DialogState {
    data class Error(val message: String) : DialogState
    data class Success(val message: String) : DialogState
}
