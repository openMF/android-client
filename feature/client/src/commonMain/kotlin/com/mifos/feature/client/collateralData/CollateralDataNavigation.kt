package com.mifos.feature.client.collateralData

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable


import kotlinx.serialization.Serializable

@Serializable
data class clientCollateralRoute(
    val clientId : Int = -1,
)
fun NavGraphBuilder.clientCollateralDestination(
    navController: NavController,
    navigateToViewAccount: (Int) -> Unit,
) {
    composable<clientCollateralRoute> {
        CollateralScreenRoute(
            navController = navController,
            viewAccount =  navigateToViewAccount,
        )
    }

}
fun NavController.navigateCollateralScreen(
    clientId : Int,
){
    this.navigate(
        clientCollateralRoute(
            clientId = clientId,
        )


    )
}