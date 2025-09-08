package com.mifos.feature.client.documentPreviewScreen

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class DocumentPreviewScreenRoute(
    val documentStateString: String ="",
    val newDocumentPath: String? = null,
    val canUpdateDocument: Boolean = false,
    val comingFromServer: Boolean = false,
)

fun NavGraphBuilder.createDocumentPreviewRoute(
    navigateOnCancelUpdating: (documentStateString: String) -> Unit,
    navigateOnDocumentRejected: (documentStateString: String) -> Unit,
    navigateOnSubmitClicked: (
        documentStateString: String,
        newDocumentPath: String,
        updateForServer: Boolean,
    ) -> Unit,
    navigateBack: (documentStateString: String) -> Unit,
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


fun NavController.navigateToDocumentPreviewForUpdatingLocal(
    documentStateString: String,
){
  this.navigate(
      DocumentPreviewScreenRoute(
          documentStateString = documentStateString,
          canUpdateDocument = true
      )
  )
}


fun NavController.navigateToDocumentPreviewWhenCanUpdateToServer(
    restoredDocumentStateString: String,
) {
  this.navigate(
      DocumentPreviewScreenRoute(
          documentStateString = restoredDocumentStateString,
          canUpdateDocument = true,
          comingFromServer = true
      )
  )
}

fun NavController.navigateToDocumentPreviewForPreview(
    documentStateString: String,
){
    this.navigate(
        DocumentPreviewScreenRoute(documentStateString = documentStateString)
    )
}
