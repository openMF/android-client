/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.mappers.loan

import com.mifos.core.model.objects.account.loan.assignLoanOfficer.AssignLoanOfficerInput
import com.mifos.core.model.objects.account.loan.assignLoanOfficer.AssignLoanOfficerResponse
import com.mifos.core.model.objects.template.loan.LoanOfficerOption
import com.mifos.core.network.dto.loans.assignLoanOfficer.AssignLoanOfficerRequestDto
import com.mifos.core.network.dto.loans.assignLoanOfficer.AssignLoanOfficerResponseDto
import com.mifos.core.network.dto.loans.template.LoanOfficerOptionDto
import com.mifos.core.network.dto.loans.template.LoanOfficerOptionsTemplateDto

// Unwrap the DTO directly into a List for the Domain
fun LoanOfficerOptionsTemplateDto.toDomain(): List<LoanOfficerOption> =
    this.loanOfficerOptions.map { it.toDomain() }

fun LoanOfficerOptionDto.toDomain(): LoanOfficerOption =
    LoanOfficerOption(
        id = id,
        firstname = firstname,
        lastname = lastname,
        displayName = displayName,
        mobileNo = mobileNo,
        officeId = officeId,
        officeName = officeName,
        isLoanOfficer = isLoanOfficer,
        isActive = isActive,
    )

fun AssignLoanOfficerInput.toDto(): AssignLoanOfficerRequestDto =
    AssignLoanOfficerRequestDto(
        assignmentDate = assignmentDate,
        dateFormat = dateFormat,
        locale = locale,
        fromLoanOfficerId = fromLoanOfficerId,
        toLoanOfficerId = toLoanOfficerId,
    )

fun AssignLoanOfficerResponseDto.toDomain(): AssignLoanOfficerResponse =
    AssignLoanOfficerResponse(
        officeId = officeId,
        clientId = clientId,
        loanId = loanId,
        resourceId = resourceId,
    )
