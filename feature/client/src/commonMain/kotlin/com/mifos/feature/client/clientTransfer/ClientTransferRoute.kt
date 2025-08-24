package com.mifos.feature.client.clientTransfer

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.feature.client.clientStaff.ClientStaffScreen
import kotlinx.serialization.Serializable


@Serializable
data class ClientTransferRoute(
    val id: Int = -1,
)

fun NavGraphBuilder.clientTransferDestination(
    onNavigateBack: () -> Unit,
    navigateToHome: () -> Unit,
) {
    composable<ClientTransferRoute> {
        ClientTransferScreen(
            onNavigateBack = onNavigateBack,
            onNavigateNext = navigateToHome,
        )
    }
}

fun NavController.navigateToClientTransferRoute(
    id: Int,
) {
    this.navigate(
        ClientTransferRoute(
            id = id,
        ),
    )
}
