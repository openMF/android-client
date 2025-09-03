package com.mifos.feature.client.clientAddress

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.feature.client.clientAddress.AddAddress.AddAddressRoute
import kotlinx.serialization.Serializable

@Serializable
data class ClientAddressRoute(
    val id: Int = -1,
)

fun NavGraphBuilder.clientAddressNavigation(
    onNavigateBack: () -> Unit,
    onNavigateNext: (Int) -> Unit,
    navController: NavController,
    navigateToAddAddressForm: (Int) -> Unit,
) {
    composable<ClientAddressRoute> {
        ClientAddressScreen(
            onNavigateBack = onNavigateBack,
            onNavigateNext = onNavigateNext,
            navigateToAddAddressForm = navigateToAddAddressForm,
            navController = navController,
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
        popUpTo<AddAddressRoute>{ inclusive = true }
        launchSingleTop = true
    }
}
