package com.mifos.core.domain.useCases

import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.RecurringAccountRepository
import com.mifos.core.model.objects.template.recurring.approval.RecurringDepositApproval
import com.mifos.core.network.GenericResponse
import kotlinx.coroutines.flow.Flow

class ApproveRecurringDepositUseCase(
    private val repository: RecurringAccountRepository,
) {
    operator suspend fun invoke(
        accountId: String,
        approval: RecurringDepositApproval,
    ): Flow<DataState<GenericResponse>> {
        return repository.approveRecurringDepositAccount(accountId, approval)
    }
}