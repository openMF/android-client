package com.mifos.feature.recurringDeposit.di

import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.RecurringAccountViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val RecurringDepositModule = module {
    viewModelOf(::RecurringAccountViewModel)
}