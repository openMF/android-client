/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.savings.di

import com.mifos.feature.savings.savingsAccount.SavingAccountViewModel
import com.mifos.feature.savings.savingsAccountActivate.SavingsAccountActivateViewModel
import com.mifos.feature.savings.savingsAccountApproval.SavingsAccountApprovalViewModel
import com.mifos.feature.savings.savingsAccountSummary.SavingsAccountSummaryViewModel
import com.mifos.feature.savings.savingsAccountTransaction.SavingsAccountTransactionViewModel
import com.mifos.feature.savings.savingsAccountTransactionReceipt.SavingsAccountTransactionReceiptViewModel
import com.mifos.feature.savings.savingsAccountv2.SavingsAccountViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val SavingsModule = module {
    viewModelOf(::SavingAccountViewModel)
    viewModelOf(::SavingsAccountViewModel)
    viewModelOf(::SavingsAccountActivateViewModel)
    viewModelOf(::SavingsAccountApprovalViewModel)
    viewModelOf(::SavingsAccountSummaryViewModel)
    viewModelOf(::SavingsAccountTransactionViewModel)
    viewModelOf(::SavingsAccountTransactionReceiptViewModel)
}
