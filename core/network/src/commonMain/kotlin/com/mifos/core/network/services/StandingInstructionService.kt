package com.mifos.core.network.services

import com.mifos.core.common.utils.Page
import com.mifos.core.network.dto.standingInstruction.StandingInstructionDto
import com.mifos.room.basemodel.APIEndPoint
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import kotlinx.coroutines.flow.Flow

interface StandingInstructionService {
    /**
     * Retrieve a list of standing instructions.
     * Responses:
     *  - 200: OK
     *
     * @param fromAccountType The type of the source account (integer)
     * @param fromAccountId The ID of the source account (integer/int64)
     * @return [Flow<Page<StandingInstructionDto>>]
     */
    @GET(APIEndPoint.STANDING_INSTRUCTIONS)
    fun getStandingInstructions(
        @Query("fromAccountType") fromAccountType: Int,
        @Query("fromAccountId") fromAccountId: Long,
    ): Flow<Page<StandingInstructionDto>>
}