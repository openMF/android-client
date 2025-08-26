package com.mifos.feature.client.clientCollateral

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class ClientCollateralRoute(
    val clientId: Int = -1,
)

fun NavGraphBuilder.clientCollateralDestination(
    onNavigateBack: () -> Unit,
    onNavigateNext: (Int) -> Unit,
) {
    composable<ClientCollateralRoute> {
        ClientCollateralScreen(
            onNavigateBack = onNavigateBack,
            onNavigateNext = onNavigateNext,
        )
    }
}

fun NavController.navigateToClientCollateralRoute(
    clientId: Int,
) {
    this.navigate(
        ClientCollateralRoute(
            clientId = clientId,
        ),
    )
}