/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp.loan

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.loan.LoanRepaymentScheduleRepository
import com.mifos.core.model.objects.account.loan.loanWithAssociations.LoanWithAssociations
import com.mifos.core.network.DataManager
import com.mifos.core.network.mappers.loan.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Created by Aditya Gupta on 12/08/23.
 */
class LoanRepaymentScheduleRepositoryImp(
    private val dataManager: DataManager,
) : LoanRepaymentScheduleRepository {

    override fun getLoanRepaySchedule(loanId: Int): Flow<DataState<LoanWithAssociations>> {
        return dataManager.getLoanRepaySchedule(loanId).map { it.toDomain() }
            .asDataStateFlow()
    }
}
