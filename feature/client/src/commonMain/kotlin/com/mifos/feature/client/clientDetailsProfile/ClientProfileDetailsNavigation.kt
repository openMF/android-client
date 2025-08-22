package com.mifos.feature.client.clientDetailsProfile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.feature.client.clientProfile.ClientProfileScreen
import kotlinx.serialization.Serializable

@Serializable
data class ClientProfileDetailsRoute(
    val id: Int = -1,
)

fun NavGraphBuilder.clientProfileDetailsDestination(
    onNavigateBack: () -> Unit,
    notes: (Int) -> Unit,
    documents: (Int) -> Unit,
    identifiers: (Int) -> Unit,
) {
    composable<ClientProfileDetailsRoute> {
        ClientProfileDetailsScreen(
            notes = notes,
            documents = documents,
            identifiers = identifiers,
            onNavigateBack = onNavigateBack,
        )
    }
}

fun NavController.navigateToClientDetailsProfileRoute(id: Int) {
    this.navigate(
        ClientProfileDetailsRoute(
            id = id,
        ),
    )
}