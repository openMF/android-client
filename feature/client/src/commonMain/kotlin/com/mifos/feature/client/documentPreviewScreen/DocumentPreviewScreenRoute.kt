package com.mifos.feature.client.documentPreviewScreen

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class DocumentPreviewScreenRoute(
    val documentPath: String = "",
    val canUpdateDocument: Boolean = false,
)

fun NavGraphBuilder.createDocumentPreviewRoute(
    navigateOnDocumentUpdate: (String) -> Unit,
    navigateOnCancelUpdating: () -> Unit,
    navigateOnDocumentRejected: () -> Unit,
    navigateOnSubmitClicked: () -> Unit,
){

    composable<DocumentPreviewScreenRoute>{

    }

}


fun NavController.navigateToDocumentPreviewForUpdating(
    documentPath: String,
){
  this.navigate(DocumentPreviewScreenRoute(documentPath, canUpdateDocument = true))
}

fun NavController.navigateToDocumentPreviewForViewing(documentPath: String){
    this.navigate(DocumentPreviewScreenRoute(documentPath, canUpdateDocument = false))
}
