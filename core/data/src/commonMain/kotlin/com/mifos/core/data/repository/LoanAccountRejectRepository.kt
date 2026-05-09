/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import com.mifos.core.common.utils.DataState
import com.mifos.core.model.objects.loan.RejectLoanInput

/**
 * Repository contract for rejecting loan applications.
 */
interface LoanAccountRejectRepository {

    /**
     * Rejects a submitted-and-pending loan application.
     *
     * The wire response is intentionally discarded — the UI only cares about
     * success or failure, so callers receive [DataState.Success] with [Unit]
     * on success and [DataState.Error] otherwise (offline emits
     * [com.mifos.core.data.util.NetworkUnavailableException]).
     */
    suspend fun rejectLoan(
        loanId: Int,
        input: RejectLoanInput,
    ): DataState<Unit>
}
