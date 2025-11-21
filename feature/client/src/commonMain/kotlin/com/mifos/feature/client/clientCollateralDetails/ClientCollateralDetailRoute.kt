package com.mifos.feature.client.clientCollateralDetails

import androidx.compose.material3.Text
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class ClientCollateralDetailRoute(
    val clientId: Int = -1,
)

fun NavGraphBuilder.clientCollateralDetailDestination(
    navController: NavController,
) {
    composable<ClientCollateralDetailRoute> {
        ClientCollateralDetailScreen(navController)
    }
}

fun NavController.navigateToClientCollateralDetailRoute(
    clientId: Int,
) {
    this.navigate(
        ClientCollateralDetailRoute(
            clientId = clientId,
        ),
    )
}
