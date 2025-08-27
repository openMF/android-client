package com.mifos.feature.about

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object AboutNavRoute

fun NavGraphBuilder.aboutDestination(
    onBackPressed: () -> Unit,
) {
    composable<AboutNavRoute> {
        AboutScreen(
            onBackPressed = onBackPressed,
        )
    }
}

fun NavController.navigateToAboutScreen() {
    this.navigate(
        AboutNavRoute
    )
}