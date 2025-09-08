package com.mifos.feature.client.documentPreviewScreen

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.feature.client.clientAddDocuments.DocumentState
import kotlinx.serialization.Serializable

@Serializable
data class DocumentPreviewScreenRoute(
    @Serializable val documentState: DocumentState,
    val newDocumentPath: String? = null,
    val canUpdateDocument: Boolean = false,
    val comingFromServer: Boolean = false,
)

fun NavGraphBuilder.createDocumentPreviewRoute(
    navigateOnCancelUpdating: (documentState: DocumentState) -> Unit,
    navigateOnDocumentRejected: (documentState: DocumentState) -> Unit,
    navigateOnSubmitClicked: (
        documentState: DocumentState,
        newDocumentPath: String,
        updateForServer: Boolean,
    ) -> Unit,
    navigateBack: (documentState: DocumentState) -> Unit,
){

    composable<DocumentPreviewScreenRoute>{
        DocumentPreviewScreen(
            navigateOnCancelUpdating = navigateOnCancelUpdating,
            navigateOnDocumentRejected = navigateOnDocumentRejected,
            navigateOnSubmitClicked = navigateOnSubmitClicked,
            navigateBack = navigateBack
        )
    }
}


fun NavController.navigateToDocumentPreviewForUpdatingLocal(
    documentState: DocumentState,
){
  this.navigate(
      DocumentPreviewScreenRoute(
          documentState = documentState,
          canUpdateDocument = true
      )
  )
}


fun NavController.navigateToDocumentPreviewWhenCanUpdateToServer(
    restoredDocumentPath: DocumentState,
    newDocumentPath: String,
) {
  this.navigate(
      DocumentPreviewScreenRoute(
          restoredDocumentPath,
          canUpdateDocument = true,
          comingFromServer = true
      )
  )
}

fun NavController.navigateToDocumentPreviewForPreview(
    documentState: DocumentState,
){
    this.navigate(
        DocumentPreviewScreenRoute(documentState = documentState)
    )
}
