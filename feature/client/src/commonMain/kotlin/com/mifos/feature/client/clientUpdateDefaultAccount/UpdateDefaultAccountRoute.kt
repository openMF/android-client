package com.mifos.feature.client.clientUpdateDefaultAccount

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class UpdateDefaultAccountRoute(
    val clientId: Int = -1,
)

fun NavGraphBuilder.updateDefaultAccountDestination(
    onNavigateBack: () -> Unit,
    onNavigateNext: () -> Unit,
) {
    composable<UpdateDefaultAccountRoute> {
        UpdateDefaultAccountScreen(
            onNavigateBack = onNavigateBack,
            onNavigateNext = onNavigateNext,
        )
    }
}

fun NavController.navigateToUpdateDefaultAccountRoute(
    clientId: Int,
) {
    this.navigate(
        UpdateDefaultAccountRoute(
            clientId = clientId,
        ),
    )
}