package com.mifos.feature.client.clientDocuments

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

data class ClientDocumentRoute(
    val id: Int= -1
)


@Composable
fun NavGraphBuilder.clientDocumentsDestination(
    navigateBack: () -> Unit,
    navigateToAddDocuments: () -> Unit,
) {

    composable<ClientDocumentRoute>{

    }
}

fun NavController.navigateClientDocumentsRoute(
    clientId: Int,
) {
    this.navigate(ClientDocumentRoute(id = clientId))
}

