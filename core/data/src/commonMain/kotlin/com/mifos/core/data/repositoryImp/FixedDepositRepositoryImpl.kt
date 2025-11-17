package com.mifos.core.data.repositoryImp


import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.FixedDepositRepository
import com.mifos.core.network.datamanager.DataManagerFixedDeposit
import com.mifos.core.network.model.FixedDepositTemplate
import kotlinx.coroutines.flow.Flow

class FixedDepositRepositoryImpl (private val dataManagerFixedDeposit: DataManagerFixedDeposit): FixedDepositRepository{

    override fun getFixedDepositTemplate(clientId: Int,productId: Int?): Flow<DataState<FixedDepositTemplate>> {
        return dataManagerFixedDeposit.getFixedDepositTemplate(clientId,productId).asDataStateFlow()
    }




}
