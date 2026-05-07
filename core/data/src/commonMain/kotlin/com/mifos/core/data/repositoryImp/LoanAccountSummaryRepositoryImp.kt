/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.mapper.loan.toDomain
import com.mifos.core.data.mapper.loan.toDto
import com.mifos.core.data.repository.LoanAccountSummaryRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.data.util.runAsDataState
import com.mifos.core.model.entity.accounts.loan.LoanForAssignOfficer
import com.mifos.core.model.entity.accounts.loan.StaffOption
import com.mifos.core.model.objects.account.loan.AssignLoanOfficerInput
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.core.network.datamanager.DataManagerStaff
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import com.mifos.room.entities.organisation.StaffEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import template.core.base.common.manager.DispatcherManager

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class LoanAccountSummaryRepositoryImp(
    private val dataManagerLoan: DataManagerLoan,
    private val dataManagerStaff: DataManagerStaff,
    private val dispatcher: DispatcherManager,
    private val networkMonitor: NetworkMonitor,
) : LoanAccountSummaryRepository {

    override fun getLoanById(loanId: Int): Flow<DataState<LoanWithAssociationsEntity?>> {
        return dataManagerLoan.getLoanById(loanId)
            .asDataStateFlow()
            .flowOn(dispatcher.io)
    }

    override fun getLoanForAssignOfficer(loanId: Int): Flow<DataState<LoanForAssignOfficer?>> {
        return dataManagerLoan.getLoanById(loanId)
            .map { entity -> entity?.toDomain() }
            .asDataStateFlow()
            .flowOn(dispatcher.io)
    }

    override fun getLoanOfficersForOffice(officeId: Int): Flow<DataState<List<StaffEntity>>> {
        return dataManagerStaff.getStaffInOffice(officeId)
            .map { staff -> staff.filter { it.isLoanOfficer == true } }
            .asDataStateFlow()
            .flowOn(dispatcher.io)
    }

    override fun getLoanOfficerStaffOptions(officeId: Int): Flow<DataState<List<StaffOption>>> {
        return dataManagerStaff.getStaffInOffice(officeId)
            .map { staff ->
                staff.filter { it.isLoanOfficer == true }.mapNotNull { it.toDomain() }
            }
            .asDataStateFlow()
            .flowOn(dispatcher.io)
    }

    override suspend fun assignLoanOfficer(
        loanId: Int,
        assignLoanOfficerInput: AssignLoanOfficerInput,
    ): DataState<GenericResponse> {
        return runAsDataState(
            networkMonitor,
            dispatcher.io,
        ) {
            dataManagerLoan.assignLoanOfficer(loanId, assignLoanOfficerInput.toDto())
        }
    }
}
