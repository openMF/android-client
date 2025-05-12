/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.domain.useCases

import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.GroupLoanAccountRepository
import com.mifos.core.model.objects.payloads.GroupLoanPayload
import com.mifos.room.entities.accounts.loans.Loan
import kotlinx.coroutines.flow.Flow

class CreateGroupLoansAccountUseCase(
    private val repository: GroupLoanAccountRepository,
) {

    operator fun invoke(
        loansPayload: GroupLoanPayload,
    ): Flow<DataState<Loan>> =
        repository.createGroupLoansAccount(loansPayload)
}
