/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.center.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mifos.core.common.utils.Constants
import com.mifos.feature.center.centerDetails.CenterDetailsScreen
import com.mifos.feature.center.centerGroupList.GroupListScreen
import com.mifos.feature.center.centerList.ui.CenterListScreen
import com.mifos.feature.center.createCenter.CreateNewCenterScreen
import com.mifos.room.entities.client.ClientEntity
import kotlinx.serialization.Serializable

@Serializable
data object CenterNavGraph

fun NavGraphBuilder.centerNavGraph(
    navController: NavController,
    onActivateCenter: (Int, String) -> Unit,
    addSavingsAccount: (Int) -> Unit,
) {
    navigation<CenterNavGraph>(
        startDestination = CenterListRoute,
    ) {
        centerListScreenRoute(
            createNewCenter = navController::navigateCreateCenterScreenRoute,
            onCenterSelect = navController::navigateCenterDetailsScreenRoute,
        )
        centerDetailScreenRoute(
            onBackPressed = navController::popBackStack,
            onActivateCenter = onActivateCenter,
            addSavingsAccount = addSavingsAccount,
            groupList = navController::navigateCenterGroupListScreenRoute,
        )
        centerGroupListScreenRoute(
            onBackPressed = navController::popBackStack,
            loadClientsOfGroup = { },
        )
        createCenterScreenRoute(
            onBackPressed = navController::popBackStack,
            onCreateSuccess = navController::popBackStack,
        )
    }
}

@Serializable
data object CenterListRoute

fun NavGraphBuilder.centerListScreenRoute(
    createNewCenter: () -> Unit,
    onCenterSelect: (Int) -> Unit,
) {
    composable<CenterListRoute> {
        CenterListScreen(
            createNewCenter = createNewCenter,
            onCenterSelect = onCenterSelect,
        )
    }
}

@Serializable
data class CenterDetailRoute(
    val centerId: Int = 0,
)

fun NavGraphBuilder.centerDetailScreenRoute(
    onBackPressed: () -> Unit,
    onActivateCenter: (Int, String) -> Unit,
    addSavingsAccount: (Int) -> Unit,
    groupList: (Int) -> Unit,
) {
    composable<CenterDetailRoute> {
        CenterDetailsScreen(
            onBackPressed = onBackPressed,
            onActivateCenter = { onActivateCenter(it, Constants.ACTIVATE_CENTER) },
            addSavingsAccount = addSavingsAccount,
            groupList = groupList,
        )
    }
}

@Serializable
data class CenterGroupListRoute(
    val centerId: Int = 0,
)

fun NavGraphBuilder.centerGroupListScreenRoute(
    onBackPressed: () -> Unit,
    loadClientsOfGroup: (List<ClientEntity>) -> Unit,
) {
    composable<CenterGroupListRoute> {
        GroupListScreen(
            onBackPressed = onBackPressed,
            loadClientsOfGroup = loadClientsOfGroup,
        )
    }
}

@Serializable
data object CreateCenterRoute

fun NavGraphBuilder.createCenterScreenRoute(
    onBackPressed: () -> Unit,
    onCreateSuccess: () -> Unit,
) {
    composable<CreateCenterRoute> {
        CreateNewCenterScreen(
            onCreateSuccess = onCreateSuccess,
            onBackPressed = onBackPressed,
        )
    }
}

fun NavController.navigateCenterDetailsScreenRoute(centerId: Int) {
    navigate(CenterDetailRoute(centerId))
}

fun NavController.navigateCreateCenterScreenRoute() {
    navigate(CreateCenterRoute)
}

fun NavController.navigateCenterGroupListScreenRoute(centerId: Int) {
    navigate(CenterGroupListRoute(centerId))
}

fun NavController.navigateToCenterListScreenRoute() {
    navigate(CenterListRoute)
}
