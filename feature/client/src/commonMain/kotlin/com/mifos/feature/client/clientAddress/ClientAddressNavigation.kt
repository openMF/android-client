package com.mifos.feature.client.clientAddress

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.feature.client.clientDetailsProfile.ClientProfileDetailsRoute
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data class ClientAddressRoute(
    val id: Int = -1,
)

fun NavGraphBuilder.clientAddressNavigation(
    onNavigateBack: () -> Unit,
    onNavigateNext: (Int) -> Unit,
    navigateToAddAddressForm: (Int) -> Unit,
) {
    composable<ClientAddressRoute> {
        ClientAddressScreen(
            onNavigateBack = onNavigateBack,
            onNavigateNext = onNavigateNext,
            navigateToAddAddressForm = navigateToAddAddressForm
        )
    }
}

fun NavController.navigateToClientAddressRoute(
    id: Int,
) {
    this.navigate(
        ClientAddressRoute(id = id),
    )
}

fun NavController.navigateToClientAddressRouteOnStatus(id: Int) {
    this.navigate(
        ClientAddressRoute(id = id),
    ) {
        popUpTo(ClientAddressRoute(id = id)) { inclusive = true }
        launchSingleTop = true
    }
}
