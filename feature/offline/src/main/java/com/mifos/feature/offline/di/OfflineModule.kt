/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.offline.di

import com.mifos.feature.offline.dashboard.OfflineDashboardViewModel
import com.mifos.feature.offline.syncCenterPayloads.SyncCenterPayloadsViewModel
import com.mifos.feature.offline.syncClientPayloads.SyncClientPayloadsViewModel
import com.mifos.feature.offline.syncGroupPayloads.SyncGroupPayloadsViewModel
import com.mifos.feature.offline.syncLoanRepaymentTransaction.SyncLoanRepaymentTransactionViewModel
import com.mifos.feature.offline.syncSavingsAccountTransaction.SyncSavingsAccountTransactionViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val OfflineModule = module {
    viewModelOf(::OfflineDashboardViewModel)
    viewModelOf(::SyncCenterPayloadsViewModel)
    viewModelOf(::SyncClientPayloadsViewModel)
    viewModelOf(::SyncGroupPayloadsViewModel)
    viewModelOf(::SyncLoanRepaymentTransactionViewModel)
    viewModelOf(::SyncSavingsAccountTransactionViewModel)
}
