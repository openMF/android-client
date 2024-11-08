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
import com.mifos.core.objects.group.Center
import com.mifos.feature.center.center_details.CenterDetailsScreen
import com.mifos.feature.center.center_group_list.GroupListScreen
import com.mifos.feature.center.center_list.ui.CenterListScreen
import com.mifos.feature.center.create_center.CreateNewCenterScreen
import com.mifos.feature.center.sync_centers_dialog.SyncCenterDialogScreen

fun NavGraphBuilder.centerNavGraph(
    navController: NavController,
    paddingValues: PaddingValues,
    onActivateCenter: (Int, String) -> Unit,
    addSavingsAccount: (Int) -> Unit
) {
    navigation(
        startDestination = CenterScreens.CenterListScreen.route,
        route = "center_screen_route"
    ) {
        centerListScreenRoute(
            paddingValues = paddingValues,
            createNewCenter = navController::navigateCreateCenterScreenRoute,
            syncClicked = {  }, // TODO open sync dialog inside center list screen
            onCenterSelect = navController::navigateCenterDetailsScreenRoute
        )
        centerDetailScreenRoute(
            onBackPressed = navController::popBackStack,
            onActivateCenter = onActivateCenter,
            addSavingsAccount = addSavingsAccount,
            groupList = navController::navigateCenterGroupListScreenRoute
        )
        centerGroupListScreenRoute(
            onBackPressed = navController::popBackStack,
            loadClientsOfGroup = { }
        )
        createCenterScreenRoute(
            onCreateSuccess = navController::popBackStack
        )

    }
}

fun NavGraphBuilder.centerListScreenRoute(
    paddingValues: PaddingValues,
    createNewCenter: () -> Unit,
    syncClicked: (List<Center>) -> Unit,
    onCenterSelect: (Int) -> Unit
) {
    composable(
        route = CenterScreens.CenterListScreen.route
    ) {
        CenterListScreen(
            paddingValues = paddingValues,
            createNewCenter = createNewCenter,
            syncClicked = syncClicked,
            onCenterSelect = onCenterSelect
        )
    }
}

fun NavGraphBuilder.centerDetailScreenRoute(
    onBackPressed: () -> Unit,
    onActivateCenter: (Int, String) -> Unit,
    addSavingsAccount: (Int) -> Unit,
    groupList: (Int) -> Unit
) {
    composable(
        route = CenterScreens.CenterDetailScreen.route,
        arguments = listOf(navArgument(Constants.CENTER_ID, builder = { type = NavType.IntType }))
    ) {
        CenterDetailsScreen(
            onBackPressed = onBackPressed,
            onActivateCenter = { onActivateCenter(it, Constants.ACTIVATE_CENTER) },
            addSavingsAccount = addSavingsAccount,
            groupList = groupList
        )
    }
}

fun NavGraphBuilder.centerGroupListScreenRoute(
    onBackPressed: () -> Unit,
    loadClientsOfGroup: (List<Client>) -> Unit
) {
    composable(
        route = CenterScreens.CenterGroupListScreen.route,
        arguments = listOf(navArgument(Constants.CENTER_ID, builder = { type = NavType.IntType }))
    ) {
        GroupListScreen(
            onBackPressed = onBackPressed,
            loadClientsOfGroup = loadClientsOfGroup
        )
    }
}

fun NavGraphBuilder.createCenterScreenRoute(
    onCreateSuccess: () -> Unit
) {
    composable(
        route = CenterScreens.CreateCenterScreen.route
    ) {
        CreateNewCenterScreen(
            onCreateSuccess = onCreateSuccess
        )
    }
}

fun NavGraphBuilder.syncCentersDialogRoute(
    dismiss : ()->Unit,
    hide : ()->Unit,
) {
    composable(
        route = CenterScreens.SyncCenterPayloadsScreen.route,
        arguments = listOf(navArgument(Constants.CENTER, builder = { type = NavType.IntType }))
    ) {
        SyncCenterDialogScreen(
            dismiss = dismiss,
            hide = hide,
        )
    }
}

fun NavController.navigateSyncCentersDialog(list: List<Center>) {
    navigate(CenterScreens.SyncCenterPayloadsScreen.arguments(list))
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
