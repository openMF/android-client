package com.mifos.feature.savings.di

import org.koin.dsl.module
import  com.mifos.feature.savings.savingsAccount.SavingAccountViewModel
import  com.mifos.feature.savings.savingsAccountActivate.SavingsAccountActivateViewModel
import  com.mifos.feature.savings.savingsAccountApproval.SavingsAccountApprovalViewModel
import  com.mifos.feature.savings.savingsAccountSummary.SavingsAccountSummaryViewModel
import  com.mifos.feature.savings.savingsAccountTransaction.SavingsAccountTransactionViewModel
import org.koin.core.module.dsl.viewModelOf

val SavingsModule = module {
    viewModelOf(::SavingAccountViewModel)
    viewModelOf(::SavingsAccountActivateViewModel)
    viewModelOf(::SavingsAccountApprovalViewModel)
    viewModelOf(::SavingsAccountSummaryViewModel)
    viewModelOf(::SavingsAccountTransactionViewModel)
}