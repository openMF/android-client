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

import com.mifos.core.common.utils.DataState
import com.mifos.core.model.objects.account.loan.guarantor.CreateGuarantor
import com.mifos.core.model.objects.account.loan.guarantor.CreateGuarantorInput
import com.mifos.core.model.objects.account.loan.guarantor.GuarantorAccountTemplate
import com.mifos.core.model.objects.account.loan.guarantor.GuarantorTemplate

interface LoanCreateGuarantorRepository {

    suspend fun getGuarantorTemplate(loanId: Int): DataState<GuarantorTemplate>

    suspend fun createGuarantor(
        loanId: Int,
        createGuarantorInput: CreateGuarantorInput,
    ): DataState<CreateGuarantor>

    suspend fun getGuarantorAccountTemplate(
        loanId: Int,
        clientId: Int,
    ): DataState<GuarantorAccountTemplate>
}
