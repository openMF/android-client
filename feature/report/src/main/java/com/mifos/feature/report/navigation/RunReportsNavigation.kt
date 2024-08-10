package com.mifos.feature.report.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.feature.report.run_report.RunReportScreen

/**
 * Created by Pronay Sarker on 10/08/2024 (7:32 AM)
 */
const val RUN_REPORTS_SCREEN_ROUTE = "run_reports_route"

fun NavController.navigateToRunReports() {
    this.navigate(RUN_REPORTS_SCREEN_ROUTE)
}

fun NavGraphBuilder.runReportsScreen(
    onReportClick: () -> Unit,
    onBackPressed: () -> Unit
) {
    composable(RUN_REPORTS_SCREEN_ROUTE) {
        RunReportScreen(
            onBackPressed = {},
            onReportClick = {}
        )
    }
}