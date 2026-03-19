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

/**
 * One-shot events emitted by the reject-loan ViewModel.
 */
internal sealed interface RejectLoanEvent {
    data object NavigateBack : RejectLoanEvent
    data object RejectSuccess : RejectLoanEvent
    data class SubmissionError(val message: String) : RejectLoanEvent
}
