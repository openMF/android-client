/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.dataTable.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.google.gson.Gson
import com.mifos.core.common.utils.Constants
import com.mifos.core.entity.client.Client
import com.mifos.core.entity.noncore.DataTable
import com.mifos.core.objects.nav.DataTableNavigationArg
import com.mifos.feature.dataTable.dataTable.DataTableScreen
import com.mifos.feature.dataTable.dataTableData.DataTableDataScreen
import com.mifos.feature.dataTable.dataTableList.DataTableListNavArgs
import com.mifos.feature.dataTable.dataTableList.DataTableListScreen
import com.mifos.feature.dataTable.dataTableList.FormWidget
import com.mifos.room.entities.navigation.DataTableDataNavigationArg

fun NavGraphBuilder.dataTableNavGraph(
    navController: NavController,
    clientCreated: (Client, Boolean) -> Unit,
) {
    navigation(
        startDestination = DataTableScreens.DataTableScreen.route,
        route = DataTableScreens.DataTableScreenRoute.route,
    ) {
        dataTableRoute(
            onBackPressed = navController::popBackStack,
            onClick = navController::navigateDataTableData,
        )

        dataTableDataRoute(
            onBackPressed = navController::popBackStack,
        )

        dataTableListRoute(
            onBackPressed = navController::popBackStack,
            clientCreated = clientCreated,
        )
    }
}

fun NavGraphBuilder.dataTableRoute(
    onBackPressed: () -> Unit,
    onClick: (table: String, entityId: Int, dataTable: DataTable) -> Unit,
) {
    composable(
        route = DataTableScreens.DataTableScreen.route,
        arguments = listOf(
            navArgument(
                name = Constants.DATA_TABLE_NAV_DATA,
                builder = { type = NavType.StringType },
            ),
        ),
    ) {
        DataTableScreen(
            navigateBack = onBackPressed,
            onClick = onClick,
        )
    }
}

fun NavGraphBuilder.dataTableDataRoute(
    onBackPressed: () -> Unit,
) {
    composable(
        route = DataTableScreens.DataTableDataScreen.route,
        arguments = listOf(
            navArgument(
                name = Constants.DATA_TABLE_DATA_NAV_DATA,
                builder = { type = NavType.StringType },
            ),
        ),
    ) {
        DataTableDataScreen(
            onBackPressed = onBackPressed,
        )
    }
}

fun NavGraphBuilder.dataTableListRoute(
    onBackPressed: () -> Unit,
    clientCreated: (Client, Boolean) -> Unit,
) {
    composable(
        route = DataTableScreens.DataTableListScreen.route,
        arguments = listOf(
            navArgument(
                name = Constants.DATA_TABLE_LIST_NAV_DATA,
                builder = { type = NavType.StringType },
            ),
        ),
    ) {
        DataTableListScreen(
            onBackPressed = onBackPressed,
            clientCreated = clientCreated,
        )
    }
}

fun NavController.navigateToDataTable(
    tableName: String,
    entityId: Int,
) {
    val arg = Gson().toJson(DataTableNavigationArg(tableName, entityId))
    navigate(DataTableScreens.DataTableScreen.argument(arg))
}

fun NavController.navigateDataTableData(
    tableName: String,
    entityId: Int,
    dataTable: DataTable,
) {
    val arg = Gson().toJson(DataTableDataNavigationArg(tableName, entityId, dataTable))
    navigate(DataTableScreens.DataTableDataScreen.argument(arg))
}

fun NavController.navigateDataTableList(
    dataTableList: List<DataTable>,
    payload: Any?,
    requestType: Int,
    formWidget: MutableList<List<FormWidget>>,
) {
    val arg = Gson().toJson(DataTableListNavArgs(dataTableList, requestType, payload, formWidget))
    navigate(DataTableScreens.DataTableListScreen.argument(arg))
}
