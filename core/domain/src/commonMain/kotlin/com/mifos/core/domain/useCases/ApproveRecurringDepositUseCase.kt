/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.domain.useCases

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.RecurringAccountRepository
import com.mifos.core.model.objects.responses.RecurringDepositApprovalResponse
import com.mifos.core.model.objects.template.recurring.approval.RecurringDepositApproval
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ApproveRecurringDepositUseCase(
    private val repository: RecurringAccountRepository,
) {
    operator fun invoke(
        accountId: String,
        approval: RecurringDepositApproval,
    ): Flow<DataState<RecurringDepositApprovalResponse>> {
        return flow {
            emit(repository.approveRecurringDepositAccount(accountId, approval))
        }.asDataStateFlow()
    }
}
