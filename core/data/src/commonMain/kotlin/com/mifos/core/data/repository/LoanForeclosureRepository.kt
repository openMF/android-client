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
import com.mifos.core.model.objects.account.loan.foreclosure.LoanForeclosureInput
import com.mifos.core.model.objects.account.loan.foreclosure.LoanForeclosureTemplate

interface LoanForeclosureRepository {

    suspend fun getLoanForeclosureTemplate(
        loanId: Int,
        transactionDate: String,
        dateFormat: String,
        locale: String,
    ): DataState<LoanForeclosureTemplate>

    suspend fun submitLoanForeclosure(
        loanId: Int,
        input: LoanForeclosureInput,
    ): DataState<Unit>
}
