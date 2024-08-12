package com.mifos.feature.client.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.mifos.core.common.utils.Constants
import com.mifos.core.objects.accounts.savings.DepositType
import com.mifos.feature.client.clientDetails.ui.ClientDetailsScreen

fun NavController.navigateClientDetailsScreen(clientId: Int) {
    navigate(ClientScreens.ClientDetailScreen.argument(clientId))
}


fun NavGraphBuilder.clientNavGraph(
    navController: NavController
) {
    navigation(
        startDestination = ClientScreens.ClientListScreen.route,
        route = ""
    ) {
        clientDetailRoute(
            onBackPressed = navController::popBackStack,
            addLoanAccount = {},
            addSavingsAccount = {},
            charges = {},
            documents = {},
            identifiers = {},
            moreClientInfo = {},
            notes = {},
            pinpointLocation = {},
            survey = {},
            uploadSignature = {},
            loanAccountSelected = {},
            savingsAccountSelected = { _, _ -> },
            activateClient = {}
        )
    }
}

fun NavGraphBuilder.clientDetailRoute(
    onBackPressed: () -> Unit,
    addLoanAccount: (Int) -> Unit,
    addSavingsAccount: (Int) -> Unit,
    charges: (Int) -> Unit,
    documents: (Int) -> Unit,
    identifiers: (Int) -> Unit,
    moreClientInfo: (Int) -> Unit,
    notes: (Int) -> Unit,
    pinpointLocation: (Int) -> Unit,
    survey: (Int) -> Unit,
    uploadSignature: (Int) -> Unit,
    loanAccountSelected: (Int) -> Unit,
    savingsAccountSelected: (Int, DepositType) -> Unit,
    activateClient: (Int) -> Unit
) {
    composable(
        route = ClientScreens.ClientDetailScreen.route,
        arguments = listOf(navArgument(Constants.CLIENT_ID, builder = { type = NavType.IntType }))
    ) {
        ClientDetailsScreen(
            onBackPressed = onBackPressed,
            addLoanAccount = addLoanAccount,
            addSavingsAccount = addSavingsAccount,
            charges = charges,
            documents = documents,
            identifiers = identifiers,
            moreClientInfo = moreClientInfo,
            notes = notes,
            pinpointLocation = pinpointLocation,
            survey = survey,
            uploadSignature = uploadSignature,
            loanAccountSelected = loanAccountSelected,
            savingsAccountSelected = savingsAccountSelected,
            activateClient = activateClient
        )
    }
}