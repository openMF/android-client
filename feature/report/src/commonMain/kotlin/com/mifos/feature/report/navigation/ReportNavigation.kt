/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.report.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.mifos.core.common.utils.Constants
import com.mifos.core.model.objects.runreport.client.ClientReportTypeItem
import com.mifos.feature.report.reportDetail.ReportDetailScreen
import com.mifos.feature.report.runReport.RunReportScreen
import kotlinx.serialization.json.Json

fun NavGraphBuilder.reportNavGraph(
    navController: NavController,
) {
    navigation(
        startDestination = ReportScreens.RunReportScreen.route,
        route = "run_report_route",
    ) {
        runReportScreenRoute(
            onBackPressed = navController::popBackStack,
            onReportSelected = navController::navigateReportDetailsScreen,
        )
        reportDetailsScreenRoute(
            onBackPressed = navController::popBackStack,
        )
    }
}

fun NavGraphBuilder.runReportScreenRoute(
    onBackPressed: () -> Unit,
    onReportSelected: (ClientReportTypeItem) -> Unit,
) {
    composable(
        route = ReportScreens.RunReportScreen.route,
    ) {
        RunReportScreen(
            onBackPressed = onBackPressed,
            onReportClick = onReportSelected,
        )
    }
}

fun NavGraphBuilder.reportDetailsScreenRoute(
    onBackPressed: () -> Unit,
) {
    composable(
        route = ReportScreens.ReportDetailScreen.route,
        arguments = listOf(navArgument(Constants.REPORT_TYPE_ITEM, builder = { type = NavType.StringType })),
    ) {
        ReportDetailScreen(
            onBackPressed = onBackPressed,
        )
    }
}

fun NavController.navigateReportDetailsScreen(clientReportTypeItem: ClientReportTypeItem) {
    val arg = Json.encodeToString(clientReportTypeItem)
    navigate(ReportScreens.ReportDetailScreen.argument(arg))
}
