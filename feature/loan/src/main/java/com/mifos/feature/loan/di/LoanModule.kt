/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.di

import com.mifos.feature.loan.groupLoanAccount.GroupLoanAccountViewModel
import com.mifos.feature.loan.loanAccount.LoanAccountViewModel
import com.mifos.feature.loan.loanAccountSummary.LoanAccountSummaryViewModel
import com.mifos.feature.loan.loanApproval.LoanAccountApprovalViewModel
import com.mifos.feature.loan.loanCharge.LoanChargeViewModel
import com.mifos.feature.loan.loanChargeDialog.LoanChargeDialogViewModel
import com.mifos.feature.loan.loanDisbursement.LoanAccountDisbursementViewModel
import com.mifos.feature.loan.loanRepayment.LoanRepaymentViewModel
import com.mifos.feature.loan.loanRepaymentSchedule.LoanRepaymentScheduleViewModel
import com.mifos.feature.loan.loanTransaction.LoanTransactionsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val LoanModule = module {
    viewModelOf(::GroupLoanAccountViewModel)
    viewModelOf(::LoanAccountViewModel)
    viewModelOf(::LoanAccountSummaryViewModel)
    viewModelOf(::LoanAccountApprovalViewModel)
    viewModelOf(::LoanChargeViewModel)
    viewModelOf(::LoanChargeDialogViewModel)
    viewModelOf(::LoanAccountDisbursementViewModel)
    viewModelOf(::LoanRepaymentViewModel)
    viewModelOf(::LoanRepaymentScheduleViewModel)
    viewModelOf(::LoanTransactionsViewModel)
}
