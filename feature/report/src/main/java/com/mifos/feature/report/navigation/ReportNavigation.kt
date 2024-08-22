package com.mifos.feature.report.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.google.gson.Gson
import com.mifos.core.common.utils.Constants
import com.mifos.core.objects.runreports.FullParameterListResponse
import com.mifos.core.objects.runreports.client.ClientReportTypeItem
import com.mifos.feature.report.report.ReportScreen
import com.mifos.feature.report.report_detail.ReportDetailScreen
import com.mifos.feature.report.run_report.RunReportScreen

fun NavGraphBuilder.reportNavGraph(
    navController: NavController
) {
    navigation(
        startDestination = ReportScreens.RunReportScreen.route,
        route = "run_report_route"
    ) {
        runReportScreenRoute(
            onBackPressed = navController::popBackStack,
            onReportSelected = navController::navigateReportDetailsScreen
        )
        reportDetailsScreenRoute(
            onBackPressed = navController::popBackStack,
            runReport = navController::navigateReportScreens
        )
        reportScreenRoute(
            onBackPressed = navController::popBackStack
        )
    }
}

fun NavGraphBuilder.runReportScreenRoute(
    onBackPressed: () -> Unit,
    onReportSelected: (ClientReportTypeItem) -> Unit
) {
    composable(
        route = ReportScreens.RunReportScreen.route
    ) {
        RunReportScreen(
            onBackPressed = onBackPressed,
            onReportClick = onReportSelected
        )
    }
}

fun NavGraphBuilder.reportDetailsScreenRoute(
    onBackPressed: () -> Unit,
    runReport: (FullParameterListResponse) -> Unit
) {
    composable(
        route = ReportScreens.ReportDetailScreen.route,
        arguments = listOf(navArgument(Constants.REPORT_TYPE_ITEM, builder = {type = NavType.StringType}))
    ) {
        ReportDetailScreen(
            onBackPressed = onBackPressed,
            runReport = runReport
        )
    }
}

fun NavGraphBuilder.reportScreenRoute(
    onBackPressed: () -> Unit
) {
    composable(
        route = ReportScreens.ReportScreen.route,
        arguments = listOf(navArgument(Constants.REPORT_PARAMETER_RESPONSE, builder = {type = NavType.StringType}))
    ) {
        ReportScreen(
            onBackPressed = onBackPressed
        )
    }
}

fun NavController.navigateReportDetailsScreen(clientReportTypeItem: ClientReportTypeItem) {
    val arg = Gson().toJson(clientReportTypeItem)
    navigate(ReportScreens.ReportDetailScreen.argument(arg))
}

fun NavController.navigateReportScreens(fullParameterListResponse: FullParameterListResponse) {
    val arg = Gson().toJson(fullParameterListResponse)
    navigate(ReportScreens.ReportScreen.argument(arg))
}