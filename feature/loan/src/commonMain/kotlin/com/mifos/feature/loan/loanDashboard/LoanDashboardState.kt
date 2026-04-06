/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanDashboard

import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import org.jetbrains.compose.resources.StringResource

data class LoanDashboardState(
    val loanStatus: LoanStatus? = null,
    val heroLabel: StringResource? = null,
    val heroValue: String = "",

    val nextRepaymentDueDate: String = "",
    val nextRepaymentAmount: String = "",

    val recentTransactions: List<RecentTransaction> = emptyList(),

    val loanDetails: LoanWithAssociationsEntity = LoanWithAssociationsEntity(),

    val periodsGraphValues: List<PeriodGraphValue> = emptyList(),
    val isGraphInterestVisible: Boolean = true,
    val isGraphPrincipalVisible: Boolean = true,

    val repaymentProgressData: RepaymentProgressData? = null,

    val timelineSteps: List<TimelineStep> = emptyList(),

    val viewState: ViewState = ViewState.Loading,
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: StringResource) : ViewState
        data object Success : ViewState
        data object Empty : ViewState
    }
}

data class RepaymentProgressData(
    val primaryLabel: StringResource,
    val primaryAmount: String,
    val primaryPercent: Float,
    val primaryPercentText: String,
    val secondaryLabel: StringResource,
    val secondaryAmount: String,
    val secondaryPercent: Float,
    val secondaryPercentText: String,
)

data class PeriodGraphValue(
    val principal: Double,
    val interest: Double,
)

data class RecentTransaction(
    val type: StringResource,
    val amount: String,
    val date: String,
    val isIncreasingDebt: Boolean?,
)

enum class LoanStatus {
    PENDING_APPROVAL,
    WAITING_FOR_DISBURSAL,
    WITHDRAWN_BY_APPLICANT,
    ACTIVE,
    OVERPAID,
    CLOSED_OBLIGATIONS_MET,
    CLOSED_WRITTEN_OFF,
    CLOSED_RESCHEDULED,
    REJECTED,
}

enum class TimelineStepState {
    COMPLETED,
    CURRENT,
    ERROR,
}

data class TimelineStep(
    val title: StringResource,
    val date: String,
    val state: TimelineStepState,
)
