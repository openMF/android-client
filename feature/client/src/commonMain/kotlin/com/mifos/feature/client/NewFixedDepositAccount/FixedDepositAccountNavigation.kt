package com.mifos.feature.client.NewFixedDepositAccount

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

import kotlinx.serialization.Serializable

@Serializable
data class  FixedDepositAccountRoute(
   val  clientId : Int = -1,
)

fun NavGraphBuilder.fixedAccountDestination(
    navController: NavController,
    onNavigateBack: () -> Unit,
    onFinish: () -> Unit,

    ){
    composable<FixedDepositAccountRoute> {
        NewFixedDepositAccountScreen(
            onNavigateBack = onNavigateBack,
            onFinish = onFinish,
            navController = navController,
        )
    }
}
fun NavController.navigateToNewFixedDepositAccountRoute(clientId: Int){
    this.navigate(
        FixedDepositAccountRoute(clientId = clientId)

        )
}
