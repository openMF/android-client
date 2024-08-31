package com.mifos.feature.offline.dashboard.navigation

/**
 * Created by Pronay Sarker on 31/08/2024 (4:24 PM)
 */
sealed class OfflineScreens( val route : String) {
    data object OfflineDashboardScreens : OfflineScreens("offline_dashboard_screen")
}