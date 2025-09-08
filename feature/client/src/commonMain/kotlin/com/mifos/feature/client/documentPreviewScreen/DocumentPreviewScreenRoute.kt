package com.mifos.feature.client.documentPreviewScreen

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object DocumentPreviewScreenRoute

fun NavGraphBuilder.createDocumentPreviewRoute(
    navigateOnCancelUpdating: () -> Unit,
    navigateOnDocumentRejected: () -> Unit,
    navigateOnSubmitClicked: () -> Unit,
    navigateBack: () -> Unit,
){

    composable<DocumentPreviewScreenRoute> {
        DocumentPreviewScreen(
            navigateOnCancelUpdating = navigateOnCancelUpdating,
            navigateOnDocumentRejected = navigateOnDocumentRejected,
            navigateOnSubmitClicked = navigateOnSubmitClicked,
            navigateBack = navigateBack
        )
    }
}


fun NavController.navigateToDocumentPreviewForUpdatingLocal(){
  this.navigate(DocumentPreviewScreenRoute)
}
