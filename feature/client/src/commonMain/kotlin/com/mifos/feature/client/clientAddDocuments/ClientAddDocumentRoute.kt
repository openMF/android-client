package com.mifos.feature.client.clientAddDocuments

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class ClientAddDocumentRoute(
    val clientId: Int = -1,
    val documentId: Int = -1,
    val entityType: String = "clients",
)


fun NavGraphBuilder.createClientAddDocumentDestination(
    navController: NavController
){

    composable<ClientAddDocumentRoute>{
        ClientAddDocumentsScreen(

        )
    }

}


fun NavController.navigateToClientAddDocumentRoute(
    clientId: Int,
    documentId: Int,
    entityType: String,
){
    this.navigate(ClientAddDocumentRoute(clientId, documentId, entityType))
}
