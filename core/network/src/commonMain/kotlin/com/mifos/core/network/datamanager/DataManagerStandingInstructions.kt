package com.mifos.core.network.datamanager

import com.mifos.core.common.utils.Page
import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.dto.standingInstruction.StandingInstructionDto
import kotlinx.coroutines.flow.Flow

class DataManagerStandingInstructions(
    private val mBaseApiManager: BaseApiManager
) {
    fun retrieveListStandingInstructions(
        fromAccountType: Int,
        fromAccountId: Long,
    ) : Flow<Page<StandingInstructionDto>> {

        return mBaseApiManager.standingInstructionService.getStandingInstructions(
            fromAccountType = fromAccountType,
            fromAccountId = fromAccountId
        )
    }
}