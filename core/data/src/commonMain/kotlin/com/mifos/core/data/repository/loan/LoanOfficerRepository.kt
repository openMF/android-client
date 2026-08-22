/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository.loan

import com.mifos.core.model.objects.account.loan.assignLoanOfficer.AssignLoanOfficerInput
import com.mifos.core.model.objects.account.loan.assignLoanOfficer.AssignLoanOfficerResponse
import com.mifos.core.model.objects.template.loan.LoanOfficerOption

interface LoanOfficerRepository {
    suspend fun getLoanOfficerOptions(loanId: Int): List<LoanOfficerOption>

    suspend fun assignLoanOfficer(
        loanId: Int,
        input: AssignLoanOfficerInput,
    ): AssignLoanOfficerResponse
}
