package com.mifos.feature.client.fixedDepositAccount.preview

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class PreviewStepRoute(
    val clientId: Int = -1
)

fun NavGraphBuilder.previewStepDestination(
    navController: NavController,
    navigateBack: () -> Unit,
    onSubmit: () -> Unit
) {
    composable<PreviewStepRoute> {
        PreviewStepScreenContainer(
            navigateBack = navigateBack,
            onSubmit = onSubmit
        )
    }
}

fun NavController.navigateToPreviewStep(clientId: Int) {
    this.navigate(PreviewStepRoute(clientId = clientId))
}