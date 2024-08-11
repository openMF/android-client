package com.mifos.feature.about.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.feature.about.AboutScreen

/**
 * Created by Pronay Sarker on 10/08/2024 (7:56 AM)
 */
const val ABOUT_SCREEN_ROUTE = "about_screen_route"

fun NavController.navigateToAboutScreen() {
    this.navigate(ABOUT_SCREEN_ROUTE)
}

fun NavGraphBuilder.aboutScreen(
    onBackPressed: () -> Unit
) {
    composable(ABOUT_SCREEN_ROUTE) {
        AboutScreen(
            onBackPressed = onBackPressed
        )
    }
}