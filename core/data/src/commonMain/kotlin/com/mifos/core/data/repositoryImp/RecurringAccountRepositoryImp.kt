package com.mifos.core.data.repositoryImp

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.RecurringAccountRepository
import com.mifos.core.model.objects.payloads.RecurringDepositAccountPayload
import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.datamanager.DataManagerRecurringAccount
import com.mifos.room.entities.accounts.recurring.RecurringDeposit
import com.mifos.room.entities.templates.recurringDeposit.RecurringDepositAccountTemplate
import kotlinx.coroutines.flow.Flow


class RecurringAccountRepositoryImp(
    val dataManagerRecurringAccount: DataManagerRecurringAccount
):  RecurringAccountRepository {
    override fun getRecuttingAccountRepository(): Flow<DataState<RecurringDepositAccountTemplate>> {
        return dataManagerRecurringAccount.getRecurringDepositAccountTemplate
            .asDataStateFlow()
    }

    override fun getRecuttingAccountRepositoryBtProduct(
        clientId: Int,
        productId: Int,
    ): Flow<DataState<RecurringDepositAccountTemplate>> {
        return dataManagerRecurringAccount.getRecurringDepositAccountTemplateByProduct(
            clientId, productId
        ).asDataStateFlow()
    }

    override fun createRecurringDepositAccount(
        recurringDepositAccountPayload: RecurringDepositAccountPayload?
    ): Flow<DataState<RecurringDeposit>> {
        return dataManagerRecurringAccount.createRecurringDepositAccount(
            recurringDepositAccountPayload
        ).asDataStateFlow()
    }

}