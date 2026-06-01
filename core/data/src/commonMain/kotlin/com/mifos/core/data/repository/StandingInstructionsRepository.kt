package com.mifos.core.data.repository

import com.mifos.core.model.objects.standingInstructions.StandingInstruction
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.Page
import kotlinx.coroutines.flow.Flow


interface StandingInstructionsRepository {
    fun getStandingInstructionList(
        fromAccountType: Int,
        fromAccountId: Long
    ): Flow<DataState<Page<StandingInstruction>>>
}