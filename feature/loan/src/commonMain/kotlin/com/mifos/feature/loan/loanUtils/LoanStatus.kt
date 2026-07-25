/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanUtils

import com.mifos.room.entities.accounts.loans.LoanStatusEntity

enum class LoanStatus {
    PENDING_APPROVAL,
    APPROVED,
    WITHDRAWN_BY_APPLICANT,
    ACTIVE,
    CLOSED_OVERPAID,
    CLOSED_OBLIGATIONS_MET,
    CLOSED_WRITTEN_OFF,
    CLOSED_RESCHEDULED,
    REJECTED,
}

fun LoanStatusEntity.getLoanStatus(): LoanStatus {
    return when {
        this.code == "loanStatusType.withdrawn.by.client" -> LoanStatus.WITHDRAWN_BY_APPLICANT
        this.overpaid == true -> LoanStatus.CLOSED_OVERPAID
        this.closedWrittenOff == true -> LoanStatus.CLOSED_WRITTEN_OFF
        this.closedRescheduled == true -> LoanStatus.CLOSED_RESCHEDULED
        this.closedObligationsMet == true -> LoanStatus.CLOSED_OBLIGATIONS_MET
        this.active == true -> LoanStatus.ACTIVE
        this.waitingForDisbursal == true -> LoanStatus.APPROVED
        this.pendingApproval == true -> LoanStatus.PENDING_APPROVAL
        else -> LoanStatus.REJECTED
    }
}
