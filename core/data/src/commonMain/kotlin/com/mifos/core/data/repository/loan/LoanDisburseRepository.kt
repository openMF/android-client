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

import com.mifos.core.model.objects.account.loan.loanDisburse.LoanDisburseInput
import com.mifos.core.model.objects.account.loan.loanDisburse.LoanDisburseTemplate

interface LoanDisburseRepository {

    suspend fun getDisburseTemplate(loanId: Int): LoanDisburseTemplate

    suspend fun disburse(loanId: Int, loanDisburseInput: LoanDisburseInput): Unit
}
