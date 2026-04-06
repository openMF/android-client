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
import com.mifos.core.data.repository.LoanAccountSummaryRepository
import com.mifos.core.model.objects.account.loan.AssignLoanOfficerRequest
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.core.network.datamanager.DataManagerStaff
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import com.mifos.room.entities.organisation.StaffEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class LoanAccountSummaryRepositoryImp(
    private val dataManagerLoan: DataManagerLoan,
    private val dataManagerStaff: DataManagerStaff,
) : LoanAccountSummaryRepository {

    override fun getLoanById(loanId: Int): Flow<DataState<LoanWithAssociationsEntity?>> {
        return dataManagerLoan.getLoanById(loanId)
            .asDataStateFlow()
    }

    override fun getLoanOfficersForOffice(officeId: Int): Flow<DataState<List<StaffEntity>>> {
        return dataManagerStaff.getStaffInOffice(officeId)
            .map { staff -> staff.filter { it.isLoanOfficer == true } }
            .asDataStateFlow()
    }

    override fun assignLoanOfficer(loanId: Int, request: AssignLoanOfficerRequest): Flow<DataState<GenericResponse>> {
        return flow {
            emit(DataState.Loading)
            emit(DataState.Success(dataManagerLoan.assignLoanOfficer(loanId, request)))
        }.catch { throwable ->
            if (throwable is CancellationException) throw throwable
            emit(DataState.Error(throwable))
        }
    }
}
