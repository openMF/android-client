package com.mifos.feature.client.clientGeneral

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class ClientProfileGeneralRoute(
    val id: Int = -1
)

fun NavGraphBuilder.clientProfileGeneralDestination(
    onNavigateBack: () -> Unit = {},
    upcomingCharges: (Int) -> Unit = {},
    loanAccounts: (Int) -> Unit = {},
    savingAccounts: (Int) -> Unit= {},
    fixedDepositAccounts: (Int) -> Unit= {},
    recurringDepositAccounts: (Int) -> Unit= {},
    sharesAccounts: (Int) -> Unit= {},
) {
    composable<ClientProfileGeneralRoute> {
        ClientProfileGeneralScreen(
            onNavigateBack,
            upcomingCharges,
            loanAccounts,
            savingAccounts,
            fixedDepositAccounts,
            recurringDepositAccounts,
            sharesAccounts
        )
    }
}

fun NavController.navigateToClientProfileGeneralRoute(id: Int){
    this.navigate(
        ClientProfileGeneralRoute(
            id = id,
        )
    )
}