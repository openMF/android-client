package com.mifos.feature.offline.di

import org.koin.dsl.module
import com.mifos.feature.offline.syncCenterPayloads.SyncCenterPayloadsViewModel
import com.mifos.feature.offline.syncClientPayloads.SyncClientPayloadsViewModel
import com.mifos.feature.offline.syncGroupPayloads.SyncGroupPayloadsViewModel
import com.mifos.feature.offline.dashboard.OfflineDashboardViewModel
import com.mifos.feature.offline.syncLoanRepaymentTransaction.SyncLoanRepaymentTransactionViewModel
import com.mifos.feature.offline.syncSavingsAccountTransaction.SyncSavingsAccountTransactionViewModel
import org.koin.core.module.dsl.viewModelOf

val OfflineModule = module {
    viewModelOf(::OfflineDashboardViewModel)
    viewModelOf(::SyncCenterPayloadsViewModel)
    viewModelOf(::SyncClientPayloadsViewModel)
    viewModelOf(::SyncGroupPayloadsViewModel)
    viewModelOf(::SyncLoanRepaymentTransactionViewModel)
    viewModelOf(::SyncSavingsAccountTransactionViewModel)
}
 