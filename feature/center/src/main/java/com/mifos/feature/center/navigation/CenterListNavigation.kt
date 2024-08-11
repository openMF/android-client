package com.mifos.feature.center.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.feature.center.center_list.ui.CenterListScreen

/**
 * Created by Pronay Sarker on 10/08/2024 (6:55 AM)
 */
const val CENTER_LIST_SCREEN_ROUTE = "center_list_route"

fun NavController.navigateToCenterList() {
    this.navigate(CENTER_LIST_SCREEN_ROUTE)
}

fun NavGraphBuilder.centerListScreen(
    paddingValues: PaddingValues,
    createNewCenter: () -> Unit,
    syncClicked: () -> Unit,
    onCenterSelect: () -> Unit
) {
    composable(CENTER_LIST_SCREEN_ROUTE) {
        CenterListScreen(
            paddingValues = paddingValues,
            createNewCenter = { /*TODO*/ },
            syncClicked = { },
            onCenterSelect = { }
        )
    }
}