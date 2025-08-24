package com.mifos.feature.client.clientStaff

import androidx.compose.material3.Text
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.feature.client.clientEditProfile.ClientProfileEditScreen
import kotlinx.serialization.Serializable

@Serializable
data class ClientStaffRoute(
    val id: Int = -1,
)

fun NavGraphBuilder.clientStaffDestination(
    onNavigateBack: () -> Unit,
    navigateToHome:()->Unit,
) {
    composable<ClientStaffRoute> {
        ClientStaffScreen(
            onNavigateBack = onNavigateBack,
            onNavigateNext = navigateToHome,
        )
    }
}

fun NavController.navigateToClientStaffRoute(
    id: Int,
) {
    this.navigate(
        ClientStaffRoute(
            id = id,
        ),
    )
}
