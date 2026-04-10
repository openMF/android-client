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
import com.mifos.core.data.mapper.loan.toModel
import com.mifos.core.data.repository.LoanAccountSummaryRepository
import com.mifos.core.model.entity.accounts.loan.LoanForAssignOfficer
import com.mifos.core.model.entity.accounts.loan.StaffOption
import com.mifos.core.model.objects.account.loan.AssignLoanOfficerRequest
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.core.network.datamanager.DataManagerStaff
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import com.mifos.room.entities.organisation.StaffEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class LoanAccountSummaryRepositoryImp(
    private val dataManagerLoan: DataManagerLoan,
    private val dataManagerStaff: DataManagerStaff,
    private val ioDispatcher: CoroutineDispatcher,
) : LoanAccountSummaryRepository {

    override fun getLoanById(loanId: Int): Flow<DataState<LoanWithAssociationsEntity?>> {
        return dataManagerLoan.getLoanById(loanId)
            .asDataStateFlow()
            .flowOn(ioDispatcher)
    }

    override fun getLoanForAssignOfficer(loanId: Int): Flow<DataState<LoanForAssignOfficer?>> {
        return dataManagerLoan.getLoanById(loanId)
            .map { entity -> entity?.toModel() }
            .asDataStateFlow()
            .flowOn(ioDispatcher)
    }

    override fun getLoanOfficersForOffice(officeId: Int): Flow<DataState<List<StaffEntity>>> {
        return dataManagerStaff.getStaffInOffice(officeId)
            .map { staff -> staff.filter { it.isLoanOfficer == true } }
            .asDataStateFlow()
            .flowOn(ioDispatcher)
    }

    override fun getLoanOfficerStaffOptions(officeId: Int): Flow<DataState<List<StaffOption>>> {
        return dataManagerStaff.getStaffInOffice(officeId)
            .map { staff ->
                staff.filter { it.isLoanOfficer == true }.mapNotNull { it.toModel() }
            }
            .asDataStateFlow()
            .flowOn(ioDispatcher)
    }

    override suspend fun assignLoanOfficer(loanId: Int, request: AssignLoanOfficerRequest): DataState<GenericResponse> {
        return withContext(ioDispatcher) {
            try {
                DataState.Success(dataManagerLoan.assignLoanOfficer(loanId, request))
            } catch (throwable: Throwable) {
                if (throwable is CancellationException) throw throwable
                DataState.Error(throwable)
            }
        }
    }
}
