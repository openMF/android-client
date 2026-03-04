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

/**
 * User actions for reject-loan interactions.
 */
internal sealed interface RejectLoanAction {
    data class RejectedOnDateChanged(val date: LocalDate) : RejectLoanAction
    data class NoteChanged(val note: String) : RejectLoanAction
    data object SubmitClicked : RejectLoanAction
    data object CancelClicked : RejectLoanAction
    data object DismissError : RejectLoanAction
    data object DiscardConfirmed : RejectLoanAction
    data object DiscardDismissed : RejectLoanAction
}
