/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.offline.navigation

/**
 * Created by Pronay Sarker on 31/08/2024 (4:24 PM)
 */
sealed class OfflineScreens(val route: String) {

    data object OfflineDashboardScreens : OfflineScreens("offline_dashboard_screen")

    data object SyncCenterPayloadsScreens : OfflineScreens("sync_center_payloads_screen")

    data object SyncClientPayloadsScreens : OfflineScreens("sync_client_payloads_screen")

    data object SyncGroupPayloadsScreens : OfflineScreens("sync_group_payloads_screen")

    data object SyncSavingsAccountTransactionsScreens : OfflineScreens("sync_savings_account_transactions_screen")

    data object SyncLoanRepaymentsScreens : OfflineScreens("sync_loan_repayments_screen")
}
