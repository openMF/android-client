/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.createNewClient

import FormWidgetDTO
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.core.common.utils.Constants
import com.mifos.room.entities.noncore.DataTableEntity
import kotlinx.serialization.Serializable
import kotlin.reflect.KFunction4

@Serializable
data object CreateNewClientRoute

fun NavGraphBuilder.createNewClientDestination(
    navController: NavController,
    hasDatatables: KFunction4<List<DataTableEntity>, Any?, Int, MutableList<List<FormWidgetDTO>>, Unit>,
) {
    composable<CreateNewClientRoute> {
        CreateNewClientScreenRoute(
            navigateBack = navController::popBackStack,
            hasDatatables = { datatables, clientPayload ->
                hasDatatables(datatables, clientPayload, Constants.CREATE_CLIENT, mutableListOf())
            },
        )
    }
}

fun NavController.navigateToCreateNewClientRoute() {
    this.navigate(CreateNewClientRoute)
}
