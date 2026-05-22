/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.dataTable.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.mifos.core.common.utils.Constants
import com.mifos.core.model.objects.payloads.GroupLoanPayload
import com.mifos.core.network.model.LoansPayload
import com.mifos.feature.dataTable.dataTable.DataTableScreen
import com.mifos.feature.dataTable.dataTableData.DataTableDataScreen
import com.mifos.feature.dataTable.dataTableList.DataTableListNavArgs
import com.mifos.feature.dataTable.dataTableList.DataTableListScreen
import com.mifos.room.entities.client.ClientPayloadEntity
import com.mifos.room.entities.navigation.DataTableDataNavigationArg
import com.mifos.room.entities.noncore.DataTableEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

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
    val arg = Json.encodeToString(DataTableDataNavigationArg.serializer(), DataTableDataNavigationArg(tableName, entityId, dataTable))
    navigate(DataTableScreens.DataTableDataScreen.argument(arg))
}

/**
 * Navigate to the data-table list screen.
 *
 * Per GAP-DT-002: the legacy `formWidget` parameter (`List<List<FormWidgetDTO>>`)
 * has been removed. The screen now derives form state directly from
 * `dataTableList[i].columnHeaderData`, eliminating the `FormWidgetDTO`
 * serialisation dependency.
 */
/**
 * Json instance for nav-arg (de)serialization. The `payload: Any?` field on
 * [DataTableListNavArgs] is `@Polymorphic`, so kotlinx-serialization needs every
 * concrete payload subtype registered here. Crash without this:
 *   "Serializer for subclass 'LoansPayload' is not found in the polymorphic scope of 'Any'"
 * (GAP-DT-011, discovered at runtime 2026-05-22 — encoder side; matches the
 * decoder-side module already in DataTableListViewModel).
 */
private val navArgJson = Json {
    serializersModule = SerializersModule {
        polymorphic(Any::class) {
            subclass(LoansPayload::class, LoansPayload.serializer())
            subclass(GroupLoanPayload::class, GroupLoanPayload.serializer())
            subclass(ClientPayloadEntity::class, ClientPayloadEntity.serializer())
        }
    }
}

fun NavController.navigateDataTableList(
    dataTableList: List<DataTableEntity>,
    payload: Any?,
    requestType: Int,
) {
    val arg = navArgJson.encodeToString(
        DataTableListNavArgs.serializer(),
        DataTableListNavArgs(dataTableList, requestType, payload),
    )
    navigate(DataTableScreens.DataTableListScreen.argument(arg))
}
