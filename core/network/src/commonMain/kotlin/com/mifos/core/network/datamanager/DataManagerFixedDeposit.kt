package com.mifos.core.network.datamanager

import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.model.FixedDepositTemplate
import kotlinx.coroutines.flow.Flow

class DataManagerFixedDeposit (private val baseApiManager: BaseApiManager){

    fun getFixedDepositTemplate(clientId:Int,productId: Int?): Flow<FixedDepositTemplate> =
        baseApiManager.fixedDepositService.fixedDepositProductTemplate(clientId,productId)

}