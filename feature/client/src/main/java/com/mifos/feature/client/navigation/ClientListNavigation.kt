package com.mifos.feature.client.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.core.objects.client.Client
import com.mifos.feature.client.clientList.presentation.ClientListScreen

/**
 * Created by Pronay Sarker on 10/08/2024 (6:47 AM)
 */
const val CLIENT_LIST_SCREEN_ROUTE = "client_list_screen"

fun NavController.navigateToClientListScreen() {
    this.navigate(CLIENT_LIST_SCREEN_ROUTE)
}

fun NavGraphBuilder.clientListScreen(
    paddingValues: PaddingValues,
    createNewClient : () -> Unit,
    syncClicked : () -> Unit,
    onClientSelect : (Int) -> Unit
) {
    composable(CLIENT_LIST_SCREEN_ROUTE) {
        ClientListScreen(
            paddingValues = paddingValues,
            createNewClient = { },
            syncClicked = { },
            onClientSelect = onClientSelect
        )
    }
}