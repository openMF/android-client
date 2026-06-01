package com.mifos.core.data.repositoryImp

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.Page
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.mappers.standingInstructions.toDomain
import com.mifos.core.data.repository.StandingInstructionsRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.data.util.withNetworkCheck
import com.mifos.core.model.objects.standingInstructions.StandingInstruction
import com.mifos.core.network.datamanager.DataManagerStandingInstructions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import template.core.base.common.manager.DispatcherManager

class StandingInstructionsRepositoryImp(
    private val dataManagerStandingInstructions: DataManagerStandingInstructions,
    private val networkMonitor: NetworkMonitor,
    private val dispatcher: DispatcherManager,
) : StandingInstructionsRepository {

    override fun getStandingInstructionList(
        fromAccountType: Int,
        fromAccountId: Long
    ): Flow<DataState<Page<StandingInstruction>>> {
        return networkMonitor.withNetworkCheck(
            dataManagerStandingInstructions
                .retrieveListStandingInstructions(fromAccountType, fromAccountId)
                .map { pageDto ->
                    Page(
                        totalFilteredRecords = pageDto.totalFilteredRecords,
                        pageItems = pageDto.pageItems.map { it.toDomain() }
                    )
                }
                .asDataStateFlow()
        ).flowOn(dispatcher.io)
    }
}