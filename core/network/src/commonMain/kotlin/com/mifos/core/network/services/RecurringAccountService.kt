package com.mifos.core.network.services

import com.mifos.core.model.objects.payloads.RecurringDepositAccountPayload
import com.mifos.room.basemodel.APIEndPoint
import com.mifos.room.entities.accounts.recurring.RecurringDeposit
import com.mifos.room.entities.templates.recurringDeposit.RecurringDepositAccountTemplate
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query
import kotlinx.coroutines.flow.Flow

interface RecurringAccountService {

    @POST(APIEndPoint.CREATE_RECURRING_DEPOSIT_ACCOUNTS)
    fun createRecurringDepositAccount(
        @Body recurringDepositAccountPayload: RecurringDepositAccountPayload?
    ): Flow<RecurringDeposit>

    @GET(APIEndPoint.CREATE_RECURRING_DEPOSIT_ACCOUNTS + "/template")
    fun getRecurringDepositAccountTemplate(): Flow<RecurringDepositAccountTemplate>

    @GET(APIEndPoint.CREATE_RECURRING_DEPOSIT_ACCOUNTS + "/template")
    fun getRecurringDepositAccountTemplateByProduct(
        @Query("clientId") clientId: Int,
        @Query("productId") productId: Int,
    ): Flow<RecurringDepositAccountTemplate>
}