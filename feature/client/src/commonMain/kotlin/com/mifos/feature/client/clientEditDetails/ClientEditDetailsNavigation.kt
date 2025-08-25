package com.mifos.feature.client.clientEditDetails

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class ClientEditDetailsRoute(
    val id: Int = -1,
    val name: String = "",
    val accountNo: String = "",
)

fun NavGraphBuilder.clientEditDetailsDestination(
    onNavigateBack: () -> Unit,
) {
    composable<ClientEditDetailsRoute> {
        ClientEditDetailsScreen(
            onNavigateBack,
        )
    }
}

fun NavController.navigateToClientEditDetailsRoute(
    id: Int,
    name: String,
    account: String,
) {
    this.navigate(
        ClientEditDetailsRoute(
            id = id,
            name = name,
            accountNo = account,
        ),
    )
}
