package com.mifos.feature.about.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.feature.about.AboutScreen

/**
 * Created by Pronay Sarker on 10/08/2024 (7:56 AM)
 */
fun NavGraphBuilder.aboutScreen(
    onBackPressed: () -> Unit
) {
    composable(AboutScreens.AboutScreen.route) {
        AboutScreen(
            onBackPressed = onBackPressed
        )
    }
}

fun NavController.navigateToAboutScreen() {
    navigate(AboutScreens.AboutScreen.route)
}
