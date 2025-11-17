package com.mifos.core.network.services

import com.mifos.core.network.model.FixedDepositTemplate
import com.mifos.room.basemodel.APIEndPoint
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.GET
import kotlinx.coroutines.flow.Flow






interface FixedDepositService {

    @GET( APIEndPoint.FIXED_DEPOSIT + "/template")
    fun fixedDepositProductTemplate(
        @Query("clientId") clientId: Int,
        @Query("productId") productId: Int?,
    ): Flow<FixedDepositTemplate>
}





