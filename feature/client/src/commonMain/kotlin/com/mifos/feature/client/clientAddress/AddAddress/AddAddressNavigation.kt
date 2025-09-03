package com.mifos.feature.client.clientAddress.AddAddress

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class AddAddressRoute(
    val id: Int = -1
)

fun NavGraphBuilder.clientAddAddressRoute(
    onNavigateBack: () -> Unit,
    navController: NavController,
    onNavigateNext: (Int) -> Unit
) {
    composable<AddAddressRoute> {
        AddAddressScreen(
            onNavigateBack = onNavigateBack,
            onNavigateNext = onNavigateNext,
            navController = navController
        )
    }
}

fun NavController.navigateToClientAddAddressRoute(
   id: Int
){
    this.navigate(
        AddAddressRoute(id = id),
    ) {
        popUpTo<AddAddressRoute> { inclusive = true }
        launchSingleTop = true
    }
}
