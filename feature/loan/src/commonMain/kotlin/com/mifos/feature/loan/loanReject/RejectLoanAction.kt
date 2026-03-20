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

import com.mifos.core.common.utils.DataState
import com.mifos.core.model.objects.account.loan.RejectLoanResponse
import kotlinx.datetime.LocalDate

/**
 * User actions for reject-loan interactions.
 */
internal sealed interface RejectLoanAction {
    data class RejectedOnDateChanged(val date: LocalDate) : RejectLoanAction
    data class NoteChanged(val note: String) : RejectLoanAction
    data object SubmitClicked : RejectLoanAction
    data object CancelClicked : RejectLoanAction
    data object DiscardConfirmed : RejectLoanAction
    data object DiscardDismissed : RejectLoanAction
    data object DismissDialog : RejectLoanAction
    data object DismissSuccessDialog : RejectLoanAction

    /**
     * Internal-only actions triggered by repository/data flow results.
     */
    sealed interface Internal : RejectLoanAction {
        data class RejectResultReceived(
            val dataState: DataState<RejectLoanResponse>,
        ) : Internal
    }
}
