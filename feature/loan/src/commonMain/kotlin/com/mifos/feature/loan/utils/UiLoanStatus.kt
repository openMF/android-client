/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.utils

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_active
import androidclient.feature.loan.generated.resources.feature_loan_approved
import androidclient.feature.loan.generated.resources.feature_loan_closed_obligations_met
import androidclient.feature.loan.generated.resources.feature_loan_closed_overpaid
import androidclient.feature.loan.generated.resources.feature_loan_closed_rescheduled
import androidclient.feature.loan.generated.resources.feature_loan_closed_written_off
import androidclient.feature.loan.generated.resources.feature_loan_pending_approval
import androidclient.feature.loan.generated.resources.feature_loan_rejected
import androidclient.feature.loan.generated.resources.feature_loan_unknown
import androidclient.feature.loan.generated.resources.feature_loan_withdrawn_by_applicant
import androidx.compose.ui.graphics.Color
import com.mifos.core.designsystem.theme.AppColors
import com.mifos.core.model.objects.account.loan.loanWithAssociations.LoanStatus

import org.jetbrains.compose.resources.StringResource

enum class UiLoanStatus(
    val label: StringResource,
    val color: Color,
) {
    PENDING_APPROVAL(
        Res.string.feature_loan_pending_approval,
        AppColors.loanPendingStatus,
    ),
    APPROVED(
        Res.string.feature_loan_approved,
        AppColors.loanApprovedStatus,
    ),
    WITHDRAWN_BY_APPLICANT(
        Res.string.feature_loan_withdrawn_by_applicant,
        AppColors.loanWithdrawnByApplicantStatus,
    ),
    ACTIVE(
        Res.string.feature_loan_active,
        AppColors.loanActiveStatus,
    ),
    CLOSED_OVERPAID(
        Res.string.feature_loan_closed_overpaid,
        AppColors.loanClosedOverpaidStatus,
    ),
    CLOSED_OBLIGATIONS_MET(
        Res.string.feature_loan_closed_obligations_met,
        AppColors.loanClosedObligationsMetStatus,
    ),
    CLOSED_WRITTEN_OFF(
        Res.string.feature_loan_closed_written_off,
        AppColors.loanClosedWrittenOffStatus,
    ),
    CLOSED_RESCHEDULED(
        Res.string.feature_loan_closed_rescheduled,
        AppColors.loanClosedRescheduled,
    ),
    REJECTED(
        Res.string.feature_loan_rejected,
        AppColors.loanRejectedStatus,
    ),
    UNKNOWN(
        Res.string.feature_loan_unknown,
        AppColors.loanUnknownStatus,
    ),
}

fun LoanStatus?.getLoanStatus(): UiLoanStatus {
    if (this == null) return UiLoanStatus.UNKNOWN
    return when {
        this.code == "loanStatusType.withdrawn.by.client" -> UiLoanStatus.WITHDRAWN_BY_APPLICANT
        this.code == "loanStatusType.rejected" -> UiLoanStatus.REJECTED
        this.overpaid == true -> UiLoanStatus.CLOSED_OVERPAID
        this.closedWrittenOff == true -> UiLoanStatus.CLOSED_WRITTEN_OFF
        this.closedRescheduled == true -> UiLoanStatus.CLOSED_RESCHEDULED
        this.closedObligationsMet == true -> UiLoanStatus.CLOSED_OBLIGATIONS_MET
        this.active == true -> UiLoanStatus.ACTIVE
        this.waitingForDisbursal == true -> UiLoanStatus.APPROVED
        this.pendingApproval == true -> UiLoanStatus.PENDING_APPROVAL
        else -> UiLoanStatus.UNKNOWN
    }
}

