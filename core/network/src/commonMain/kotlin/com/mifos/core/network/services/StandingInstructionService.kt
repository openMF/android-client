/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
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
        @Query("clientId") clientId: Long,
        @Query("clientName") clientName: String,
        @Query("fromAccountId") fromAccountId: Long,
        @Query("fromAccountType") fromAccountType: Int,
        @Query("locale") locale: String = "en",
        @Query("dateFormat") dateFormat: String = "dd MMMM yyyy",
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
    ): Flow<Page<StandingInstructionDto>>
}
