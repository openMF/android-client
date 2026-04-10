/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.mapper.loan

import com.mifos.core.model.entity.accounts.loan.LoanForAssignOfficer
import com.mifos.core.model.entity.accounts.loan.StaffOption
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import com.mifos.room.entities.organisation.StaffEntity

fun LoanWithAssociationsEntity.toModel(): LoanForAssignOfficer =
    LoanForAssignOfficer(
        id = id,
        clientOfficeId = clientOfficeId,
        loanOfficerId = loanOfficerId,
        loanOfficerName = loanOfficerName,
    )

fun StaffEntity.toModel(): StaffOption? {
    val staffId = id ?: return null
    return StaffOption(
        id = staffId,
        firstname = firstname,
        lastname = lastname,
        displayName = displayName,
    )
}
