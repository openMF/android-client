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

import FormWidgetDTO
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.mifos.core.common.utils.Constants
import com.mifos.feature.dataTable.dataTable.DataTableScreen
import com.mifos.feature.dataTable.dataTableData.DataTableDataScreen
import com.mifos.feature.dataTable.dataTableList.DataTableListNavArgs
import com.mifos.feature.dataTable.dataTableList.DataTableListScreen
import com.mifos.room.entities.client.ClientPayloadEntity
import com.mifos.room.entities.navigation.DataTableDataNavigationArg
import com.mifos.room.entities.noncore.DataTableEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data object DataTableNavGraph

fun NavGraphBuilder.dataTableNavGraph(
    navController: NavController,
    clientCreated: (ClientPayloadEntity, Boolean) -> Unit,
) {
    navigation<DataTableNavGraph>(
        startDestination = DataTableRoute(),
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
    onClick: (table: String, entityId: Int, dataTable: DataTableEntity) -> Unit,
) {
    composable<DataTableRoute> {
        DataTableScreen(
            navigateBack = onBackPressed,
            onClick = onClick,
        )
    }
}

// TODO : change during new screens migration
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

// TODO : change during new screens migration
fun NavGraphBuilder.dataTableListRoute(
    onBackPressed: () -> Unit,
    clientCreated: (ClientPayloadEntity, Boolean) -> Unit,
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

@Serializable
data class DataTableRoute(
    val tableName: String = "",
    val entityId: Int = -1,
)

fun NavController.navigateToDataTable(
    tableName: String,
    entityId: Int,
) {
    navigate(DataTableRoute(tableName, entityId))
}

fun NavController.navigateDataTableData(
    tableName: String,
    entityId: Int,
    dataTable: DataTableEntity,
) {
    val arg = Json.encodeToString(DataTableDataNavigationArg(tableName, entityId, dataTable))
    navigate(DataTableScreens.DataTableDataScreen.argument(arg))
}

fun NavController.navigateDataTableList(
    dataTableList: List<DataTableEntity>,
    payload: Any?,
    requestType: Int,
    formWidget: MutableList<List<FormWidgetDTO>>,
) {
    val arg = Json.encodeToString(DataTableListNavArgs(dataTableList, requestType, payload, formWidget))
    navigate(DataTableScreens.DataTableListScreen.argument(arg))
}
