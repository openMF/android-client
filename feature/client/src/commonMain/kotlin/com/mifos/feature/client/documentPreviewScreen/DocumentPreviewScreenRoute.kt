package com.mifos.feature.client.documentPreviewScreen

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object DocumentPreviewScreenRoute

fun NavGraphBuilder.createDocumentPreviewRoute(
    navigateBack: () -> Unit,
){

    composable<DocumentPreviewScreenRoute> {
        DocumentPreviewScreen(
            navigateBack = navigateBack
        )
    }
}


fun NavController.navigateToDocumentPreviewRoute(){
  this.navigate(DocumentPreviewScreenRoute)
}
