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
import com.mifos.core.data.repository.LoanAccountRepository
import com.mifos.core.model.objects.organisations.LoanProducts
import kotlinx.coroutines.flow.Flow

class GetAllLoanUseCase(
    private val loanAccountRepository: LoanAccountRepository,
) {

    operator fun invoke(): Flow<DataState<List<LoanProducts>>> =
        loanAccountRepository.allLoans()
}
