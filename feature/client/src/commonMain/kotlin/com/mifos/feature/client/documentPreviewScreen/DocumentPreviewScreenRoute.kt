package com.mifos.feature.client.documentPreviewScreen

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class DocumentPreviewScreenRoute(
    val documentPath: String = "",
    val canUpdateDocument: Boolean = false,
    val comingFromServer: Boolean = false,
)

fun NavGraphBuilder.createDocumentPreviewRoute(
    navigateOnDocumentUpdate: (
         documentPath: String, updateForServer: Boolean
    ) -> Unit,
    navigateOnCancelUpdating: () -> Unit,
    navigateOnDocumentRejected: () -> Unit,
    navigateOnSubmitClicked: (documentPath: String) -> Unit,
    navigateBack: () -> Unit,
){

    composable<DocumentPreviewScreenRoute>{
        DocumentPreviewScreen(
            navigateOnDocumentUpdate = navigateOnDocumentUpdate,
            navigateOnCancelUpdating = navigateOnCancelUpdating,
            navigateOnDocumentRejected = navigateOnDocumentRejected,
            navigateOnSubmitClicked = navigateOnSubmitClicked,
            navigateBack = navigateBack
        )
    }
}


fun NavController.navigateToDocumentPreviewForUpdatingLocal(
    documentPath: String,
){
  this.navigate(
      DocumentPreviewScreenRoute(documentPath, canUpdateDocument = true)
  )
}


fun NavController.navigateToDocumentPreviewWhenCanUpdateToServer(
    documentPath: String,
){
  this.navigate(
      DocumentPreviewScreenRoute(documentPath, canUpdateDocument = true, comingFromServer = true)
  )
}

fun NavController.navigateToDocumentPreviewForPreview(documentPath: String){
    this.navigate(
        DocumentPreviewScreenRoute(
            documentPath, canUpdateDocument = false)
    )
}
