package com.mifos.core.data.repository

import com.mifos.core.common.utils.DataState
import com.mifos.core.network.model.FixedDepositTemplate
import kotlinx.coroutines.flow.Flow

interface FixedDepositRepository {
    fun getFixedDepositTemplate(
        clientId: Int,
        productId: Int?
    ): Flow<DataState<FixedDepositTemplate>>


}