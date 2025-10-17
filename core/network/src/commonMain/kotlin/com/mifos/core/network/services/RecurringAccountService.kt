package com.mifos.core.network.services

import com.mifos.room.basemodel.APIEndPoint
import com.mifos.room.entities.templates.recurringDeposit.RecurringDepositAccountTemplate
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import kotlinx.coroutines.flow.Flow

interface RecurringAccountService {
    @GET(APIEndPoint.CREATE_RECURRING_DEPOSIT_ACCOUNTS + "/template")
    fun getClientSavingsAccountTemplateByProduct(
        @Query("clientId") clientId: Int,
        @Query("productId") productId: Int,
    ): Flow<RecurringDepositAccountTemplate>
}