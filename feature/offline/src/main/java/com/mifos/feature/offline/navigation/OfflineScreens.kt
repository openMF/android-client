package com.mifos.feature.offline.navigation

/**
 * Created by Pronay Sarker on 31/08/2024 (4:24 PM)
 */
sealed class OfflineScreens( val route : String) {

    data object OfflineDashboardScreens : OfflineScreens("offline_dashboard_screen")

    data object SyncCenterPayloadsScreens : OfflineScreens("sync_center_payloads_screen")

    data object SyncClientPayloadsScreens : OfflineScreens("sync_client_payloads_screen")

    data object SyncGroupPayloadsScreens : OfflineScreens("sync_group_payloads_screen")

    data object SyncSavingsAccountTransactionsScreens : OfflineScreens("sync_savings_account_transactions_screen")

    data object SyncLoanRepaymentsScreens : OfflineScreens("sync_loan_repayments_screen")

}