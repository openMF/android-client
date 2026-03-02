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
 * User intents for reject-loan interactions.
 */
internal sealed class RejectLoanViewIntent {
    data class RejectedOnDateChanged(val date: LocalDate) : RejectLoanViewIntent()
    data class NoteChanged(val note: String) : RejectLoanViewIntent()
    data object SubmitClicked : RejectLoanViewIntent()
    data object CancelClicked : RejectLoanViewIntent()
    data object DismissError : RejectLoanViewIntent()
    data object DiscardConfirmed : RejectLoanViewIntent()
    data object DiscardDismissed : RejectLoanViewIntent()
    data object NavigationHandled : RejectLoanViewIntent()
}
