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

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.mifos.core.common.utils.Constants
import com.mifos.core.objects.client.Client
import com.mifos.feature.center.centerDetails.CenterDetailsScreen
import com.mifos.feature.center.centerGroupList.GroupListScreen
import com.mifos.feature.center.centerList.ui.CenterListScreen
import com.mifos.feature.center.createCenter.CreateNewCenterScreen

fun NavGraphBuilder.centerNavGraph(
    navController: NavController,
    paddingValues: PaddingValues,
    onActivateCenter: (Int, String) -> Unit,
    addSavingsAccount: (Int) -> Unit,
) {
    navigation(
        startDestination = CenterScreens.CenterListScreen.route,
        route = "center_screen_route",
    ) {
        centerListScreenRoute(
            paddingValues = paddingValues,
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
            onCreateSuccess = navController::popBackStack,
            onNavigateBack = navController::popBackStack,
        )
    }
}

fun NavGraphBuilder.centerListScreenRoute(
    paddingValues: PaddingValues,
    createNewCenter: () -> Unit,
    onCenterSelect: (Int) -> Unit,
) {
    composable(
        route = CenterScreens.CenterListScreen.route,
    ) {
        CenterListScreen(
            paddingValues = paddingValues,
            createNewCenter = createNewCenter,
            onCenterSelect = onCenterSelect,
        )
    }
}

fun NavGraphBuilder.centerDetailScreenRoute(
    onBackPressed: () -> Unit,
    onActivateCenter: (Int, String) -> Unit,
    addSavingsAccount: (Int) -> Unit,
    groupList: (Int) -> Unit,
) {
    composable(
        route = CenterScreens.CenterDetailScreen.route,
        arguments = listOf(navArgument(Constants.CENTER_ID, builder = { type = NavType.IntType })),
    ) {
        CenterDetailsScreen(
            onBackPressed = onBackPressed,
            onActivateCenter = { onActivateCenter(it, Constants.ACTIVATE_CENTER) },
            addSavingsAccount = addSavingsAccount,
            groupList = groupList,
        )
    }
}

fun NavGraphBuilder.centerGroupListScreenRoute(
    onBackPressed: () -> Unit,
    loadClientsOfGroup: (List<Client>) -> Unit,
) {
    composable(
        route = CenterScreens.CenterGroupListScreen.route,
        arguments = listOf(navArgument(Constants.CENTER_ID, builder = { type = NavType.IntType })),
    ) {
        GroupListScreen(
            onBackPressed = onBackPressed,
            loadClientsOfGroup = loadClientsOfGroup,
        )
    }
}

fun NavGraphBuilder.createCenterScreenRoute(
    onCreateSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    composable(
        route = CenterScreens.CreateCenterScreen.route,
    ) {
        CreateNewCenterScreen(
            onCreateSuccess = onCreateSuccess,
            onNavigateBack = onNavigateBack,
        )
    }
}

fun NavController.navigateCenterDetailsScreenRoute(centerId: Int) {
    navigate(CenterScreens.CenterDetailScreen.argument(centerId))
}

fun NavController.navigateCreateCenterScreenRoute() {
    navigate(CenterScreens.CreateCenterScreen.route)
}

fun NavController.navigateCenterGroupListScreenRoute(centerId: Int) {
    navigate(CenterScreens.CenterGroupListScreen.arguments(centerId))
}
