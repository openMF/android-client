/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.mappers.recurring

import com.mifos.core.model.objects.responses.RecurringDepositApprovalChanges
import com.mifos.core.model.objects.responses.RecurringDepositApprovalResponse
import com.mifos.core.model.objects.responses.RecurringDepositApprovalStatus
import com.mifos.core.model.objects.template.recurring.approval.RecurringDepositApproval
import com.mifos.core.network.model.recurring.RecurringDepositApprovalChangesDto
import com.mifos.core.network.model.recurring.RecurringDepositApprovalRequestDto
import com.mifos.core.network.model.recurring.RecurringDepositApprovalResponseDto
import com.mifos.core.network.model.recurring.RecurringDepositApprovalStatusDto

fun RecurringDepositApproval.toDto(): RecurringDepositApprovalRequestDto =
    RecurringDepositApprovalRequestDto(
        locale = locale,
        dateFormat = dateFormat,
        approvedOnDate = approvedOnDate,
        note = note,
    )

fun RecurringDepositApprovalResponseDto.toModel(): RecurringDepositApprovalResponse =
    RecurringDepositApprovalResponse(
        officeId = officeId,
        clientId = clientId,
        savingsId = savingsId,
        resourceId = resourceId,
        changes = changes?.toModel(),
    )

private fun RecurringDepositApprovalChangesDto.toModel(): RecurringDepositApprovalChanges =
    RecurringDepositApprovalChanges(
        status = status?.toModel(),
        locale = locale,
        dateFormat = dateFormat,
        approvedOnDate = approvedOnDate,
    )

private fun RecurringDepositApprovalStatusDto.toModel(): RecurringDepositApprovalStatus =
    RecurringDepositApprovalStatus(
        id = id,
        code = code,
        value = value,
        submittedAndPendingApproval = submittedAndPendingApproval,
        approved = approved,
        rejected = rejected,
        withdrawnByApplicant = withdrawnByApplicant,
        active = active,
        closed = closed,
    )
