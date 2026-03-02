/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.di

import com.mifos.feature.loan.amountTransfer.AmountTransferViewModel
import com.mifos.feature.loan.assignLoanOfficer.AssignLoanOfficerViewModel
import com.mifos.feature.loan.createGuarantor.CreateGuarantorViewModel
import com.mifos.feature.loan.createLoanReschedules.LoanRescheduleFormViewModel
import com.mifos.feature.loan.groupLoanAccount.GroupLoanAccountViewModel
import com.mifos.feature.loan.loanAccount.LoanAccountViewModel
import com.mifos.feature.loan.loanAccountGeneral.LoanAccountGeneralViewModel
import com.mifos.feature.loan.loanAccountAction.LoanAccountActionsViewModel
import com.mifos.feature.loan.loanAccountAction.payments.LoanPaymentsActionViewModel
import com.mifos.feature.loan.loanAccountProfile.LoanAccountProfileViewModel
import com.mifos.feature.loan.loanAccountSummary.LoanAccountSummaryViewModel
import com.mifos.feature.loan.loanApproval.LoanAccountApprovalViewModel
import com.mifos.feature.loan.loanCharge.LoanChargeViewModel
import com.mifos.feature.loan.loanChargeOff.LoanChargeOffViewModel
import com.mifos.feature.loan.loanDashboard.LoanDashboardViewModel
import com.mifos.feature.loan.loanDisburse.LoanDisburseViewModel
import com.mifos.feature.loan.loanReject.LoanRejectViewModel
import com.mifos.feature.loan.loanRepayment.LoanRepaymentViewModel
import com.mifos.feature.loan.loanRepaymentSchedule.LoanRepaymentScheduleViewModel
import com.mifos.feature.loan.loanReschedules.LoanReschedulesViewModel
import com.mifos.feature.loan.loanTransaction.LoanTransactionsViewModel
import com.mifos.feature.loan.newLoanAccount.NewLoanAccountViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val LoanModule = module {
    viewModelOf(::GroupLoanAccountViewModel)
    viewModelOf(::LoanAccountViewModel)
    viewModelOf(::LoanAccountGeneralViewModel)
    viewModelOf(::LoanAccountSummaryViewModel)
    viewModelOf(::LoanAccountApprovalViewModel)
    viewModelOf(::LoanChargeViewModel)
    viewModelOf(::LoanChargeOffViewModel)
    viewModelOf(::LoanRejectViewModel)
    viewModelOf(::LoanRepaymentViewModel)
    viewModelOf(::LoanRepaymentScheduleViewModel)
    viewModelOf(::LoanTransactionsViewModel)
    viewModelOf(::NewLoanAccountViewModel)
    viewModelOf(::LoanAccountProfileViewModel)
    viewModelOf(::AmountTransferViewModel)
    viewModelOf(::LoanAccountActionsViewModel)
    viewModelOf(::LoanPaymentsActionViewModel)
    viewModelOf(::LoanDashboardViewModel)
    viewModelOf(::CreateGuarantorViewModel)
    viewModelOf(::LoanReschedulesViewModel)
    viewModelOf(::LoanRescheduleFormViewModel)
    viewModelOf(::AssignLoanOfficerViewModel)
    viewModelOf(::LoanDisburseViewModel)
}
