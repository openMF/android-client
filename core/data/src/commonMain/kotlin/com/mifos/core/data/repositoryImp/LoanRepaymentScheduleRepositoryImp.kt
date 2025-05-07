/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.LoanRepaymentScheduleRepository
import com.mifos.core.network.DataManager
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 12/08/23.
 */
class LoanRepaymentScheduleRepositoryImp(
    private val dataManager: DataManager,
) : LoanRepaymentScheduleRepository {

    override fun getLoanRepaySchedule(loanId: Int): Flow<DataState<LoanWithAssociationsEntity>> {
        return dataManager.getLoanRepaySchedule(loanId).asDataStateFlow()
    }
}
