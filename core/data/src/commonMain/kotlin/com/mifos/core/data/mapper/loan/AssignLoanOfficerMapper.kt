/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.mapper.loan

import com.mifos.core.model.entity.accounts.loan.AssignLoanOfficerLoan
import com.mifos.core.model.entity.accounts.loan.StaffOption
import com.mifos.core.model.objects.account.loan.AssignLoanOfficerInput
import com.mifos.core.network.dto.loan.AssignLoanOfficerRequestDto
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import com.mifos.room.entities.organisation.StaffEntity

fun LoanWithAssociationsEntity.toAssignLoanOfficerLoan(): AssignLoanOfficerLoan =
    AssignLoanOfficerLoan(
        id = id,
        clientOfficeId = clientOfficeId,
        loanOfficerId = loanOfficerId,
        loanOfficerName = loanOfficerName,
    )

fun StaffEntity.toStaffOption(): StaffOption? {
    val staffId = id ?: return null
    return StaffOption(
        id = staffId,
        firstname = firstname,
        lastname = lastname,
        displayName = displayName,
    )
}

fun AssignLoanOfficerInput.toDto(): AssignLoanOfficerRequestDto =
    AssignLoanOfficerRequestDto(
        toLoanOfficerId = toLoanOfficerId,
        assignmentDate = assignmentDate,
        locale = locale,
        dateFormat = dateFormat,
        fromLoanOfficerId = fromLoanOfficerId,
    )
