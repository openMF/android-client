package com.mifos.feature.client.NewFixedDepositAccount

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable


@Serializable
 data  object FixedDepositRoute

fun NavGraphBuilder.FixedAccountdestination(){
    composable <FixedDepositRoute> {
        FixedDepositAccountScreen(
            onNavigateBack = {},
            onFinish = {},
        )
    }
}
fun NavController.navigateToNewFixedDepositRoute(){
    this.navigate(
        FixedDepositRoute,
    )
}