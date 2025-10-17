package com.mifos.core.data.repository

import com.mifos.core.common.utils.DataState
import com.mifos.room.entities.templates.recurringDeposit.RecurringDepositAccountTemplate
import kotlinx.coroutines.flow.Flow


interface RecurringAccountRepository {

    fun getRecuttingAccountRepository(): Flow<DataState<RecurringDepositAccountTemplate>>

    fun getRecuttingAccountRepositoryBtProduct(
        clientId: Int,
        productId: Int,
    ): Flow<DataState<RecurringDepositAccountTemplate>>

    fun createRecurringDepositAccount()

}