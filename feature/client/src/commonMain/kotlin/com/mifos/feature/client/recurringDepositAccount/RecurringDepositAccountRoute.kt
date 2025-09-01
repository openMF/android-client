package com.mifos.feature.client.recurringDepositAccount

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class RecurringDepositAccountRoute(
    val clientId: Int = -1
)


fun NavGraphBuilder.clientRecurringDepositAccountDestination(
    navigateBack: () -> Unit,
    onApproveAccount: (Int) -> Unit ={},
    onViewAccount: (Int) -> Unit ={},
){
    composable<RecurringDepositAccountRoute>{
        RecurringDepositAccountScreen(
            navigateBack = navigateBack,
            onApproveAccount = {
                onApproveAccount(it)
            },
            onViewAccount = {
                onViewAccount(it)
            }
        )
    }
}

fun NavController.navigateToRecurringDepositAccountRoute(
    clientId: Int,
) {
    this.navigate(RecurringDepositAccountRoute(clientId = clientId))
}