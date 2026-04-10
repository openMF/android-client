/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import com.mifos.core.common.utils.DataState
import com.mifos.core.model.entity.accounts.loan.LoanForAssignOfficer
import com.mifos.core.model.entity.accounts.loan.StaffOption
import com.mifos.core.model.objects.account.loan.AssignLoanOfficerRequest
import com.mifos.core.network.GenericResponse
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import com.mifos.room.entities.organisation.StaffEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface LoanAccountSummaryRepository {

    fun getLoanById(loanId: Int): Flow<DataState<LoanWithAssociationsEntity?>>
    fun getLoanForAssignOfficer(loanId: Int): Flow<DataState<LoanForAssignOfficer?>>

    fun getLoanOfficersForOffice(officeId: Int): Flow<DataState<List<StaffEntity>>>
    fun getLoanOfficerStaffOptions(officeId: Int): Flow<DataState<List<StaffOption>>>

    suspend fun assignLoanOfficer(loanId: Int, request: AssignLoanOfficerRequest): DataState<GenericResponse>
}
