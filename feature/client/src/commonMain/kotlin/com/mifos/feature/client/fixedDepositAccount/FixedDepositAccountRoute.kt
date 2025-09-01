package com.mifos.feature.client.fixedDepositAccount

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class FixedDepositAccountRoute(
    val clientId: Int = -1,
)

fun NavGraphBuilder.clientFixedDepositAccountDestination(
    navigateBack: () -> Unit,
    onApproveAccount: (String) -> Unit,
    onViewAccount: (String) -> Unit,
) {
    composable<FixedDepositAccountRoute> {
        FixedDepositAccountScreen(
            navigateBack = navigateBack,
            onApproveAccount = {
                onApproveAccount(it)
            },
            onViewAccount = {
                onViewAccount(it)
            },
        )
    }
}

fun NavController.navigateToFixedDepositAccountRoute(
    clientId: Int,
) {
    this.navigate(FixedDepositAccountRoute(clientId = clientId))
}
