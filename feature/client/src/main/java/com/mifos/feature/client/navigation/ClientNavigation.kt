package com.mifos.feature.client.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.mifos.core.common.utils.Constants
import com.mifos.core.objects.accounts.savings.DepositType
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.survey.Survey
import com.mifos.feature.client.clientCharges.ClientChargesScreen
import com.mifos.feature.client.clientDetails.ui.ClientDetailsScreen
import com.mifos.feature.client.clientIdentifiers.ClientIdentifiersScreen
import com.mifos.feature.client.clientList.presentation.ClientListScreen
import com.mifos.feature.client.clientPinpoint.PinpointClientScreen
import com.mifos.feature.client.clientSignature.SignatureScreen
import com.mifos.feature.client.clientSurveyList.SurveyListScreen
import com.mifos.feature.client.clientSurveyQuestion.SurveyQuestionScreen

fun NavGraphBuilder.clientNavGraph(
    navController: NavController,
    paddingValues: PaddingValues,
    addLoanAccount: (Int) -> Unit,
    addSavingsAccount: (Int) -> Unit,
    documents: (Int) -> Unit,
    moreClientInfo: (Int) -> Unit,
    notes: (Int) -> Unit,
    loanAccountSelected: (Int) -> Unit,
    savingsAccountSelected: (Int, DepositType) -> Unit,
    activateClient: (Int) -> Unit
) {
    navigation(
        startDestination = ClientScreens.ClientListScreen.route,
        route = "client_screen_route"
    ) {
        clientListScreenRoute(
            paddingValues = paddingValues,
            onClientSelect = navController::navigateClientDetailsScreen,
            createNewClient = {},
            syncClicked = {}
        )
        clientDetailRoute(
            onBackPressed = navController::popBackStack,
            addLoanAccount = addLoanAccount,
            addSavingsAccount = addSavingsAccount,
            charges = navController::navigateClientChargesScreen,
            documents = documents,
            identifiers = navController::navigateClientIdentifierScreen,
            moreClientInfo = moreClientInfo,
            notes = notes,
            pinpointLocation = navController::navigateClientPinPointScreen,
            survey = navController::navigateClientSurveyListScreen,
            uploadSignature = navController::navigateClientSignatureScreen,
            loanAccountSelected = loanAccountSelected,
            savingsAccountSelected = savingsAccountSelected,
            activateClient = activateClient
        )
        clientChargesRoute(
            onBackPressed = navController::popBackStack
        )
        clientIdentifierRoute(
            onDocumentClicked = {},
            onBackPressed = navController::popBackStack
        )
        clientPinPointRoute(
            onBackPressed = navController::popBackStack
        )
        clientSignatureRoute(
            onBackPressed = navController::popBackStack
        )
        clientSurveyListRoute(
            onBackPressed = navController::popBackStack,
            onCardClicked = { _, _ -> }
        )
        clientSurveyQuestionRoute(
            onBackPressed = navController::popBackStack
        )
    }
}

fun NavGraphBuilder.clientListScreenRoute(
    paddingValues: PaddingValues,
    onClientSelect: (Int) -> Unit,
    createNewClient: () -> Unit,
    syncClicked: (List<Client>) -> Unit
) {
    composable(
        route = ClientScreens.ClientListScreen.route
    ) {
        ClientListScreen(
            paddingValues = paddingValues,
            createNewClient = createNewClient,
            syncClicked = syncClicked,
            onClientSelect = onClientSelect
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

fun NavGraphBuilder.clientIdentifierRoute(
    onDocumentClicked: (Int) -> Unit,
    onBackPressed: () -> Unit
) {
    composable(
        route = ClientScreens.ClientIdentifierScreen.route,
        arguments = listOf(navArgument(Constants.CLIENT_ID, builder = { type = NavType.IntType }))
    ) {
        ClientIdentifiersScreen(
            onBackPressed = onBackPressed,
            onDocumentClicked = onDocumentClicked
        )
    }
}

fun NavGraphBuilder.clientChargesRoute(
    onBackPressed: () -> Unit
) {
    composable(
        route = ClientScreens.ClientChargesScreen.route,
        arguments = listOf(navArgument(Constants.CLIENT_ID, builder = { type = NavType.IntType }))
    ) {
        ClientChargesScreen(
            onBackPressed = onBackPressed
        )
    }
}

fun NavGraphBuilder.clientPinPointRoute(
    onBackPressed: () -> Unit
) {
    composable(
        route = ClientScreens.ClientPinPointScreen.route,
        arguments = listOf(navArgument(Constants.CLIENT_ID, builder = { type = NavType.IntType }))
    ) {
        PinpointClientScreen(
            onBackPressed = onBackPressed
        )
    }
}

fun NavGraphBuilder.clientSignatureRoute(
    onBackPressed: () -> Unit
) {
    composable(
        route = ClientScreens.ClientSignatureScreen.route,
        arguments = listOf(navArgument(Constants.CLIENT_ID, builder = { type = NavType.IntType }))
    ) {
        SignatureScreen(
            onBackPressed = onBackPressed
        )
    }
}

fun NavGraphBuilder.clientSurveyListRoute(
    onBackPressed: () -> Unit,
    onCardClicked: (Int, List<Survey>) -> Unit
) {
    composable(
        route = ClientScreens.ClientSurveyListScreen.route,
        arguments = listOf(navArgument(Constants.CLIENT_ID, builder = { type = NavType.IntType }))
    ) {
        SurveyListScreen(
            navigateBack = onBackPressed,
            onCardClicked = onCardClicked
        )
    }
}

fun NavGraphBuilder.clientSurveyQuestionRoute(
    onBackPressed: () -> Unit
) {
    composable(
        route = ClientScreens.ClientSurveyQuestionScreen.route,
        arguments = listOf(navArgument(Constants.CLIENT_ID, builder = { type = NavType.IntType }))
    ) {
        SurveyQuestionScreen(
            navigateBack = onBackPressed,
            survey = Survey()
        )
    }
}

fun NavController.navigateClientDetailsScreen(clientId: Int) {
    navigate(ClientScreens.ClientDetailScreen.argument(clientId))
}

fun NavController.navigateClientIdentifierScreen(clientId: Int) {
    navigate(ClientScreens.ClientIdentifierScreen.argument(clientId))
}

fun NavController.navigateClientChargesScreen(clientId: Int) {
    navigate(ClientScreens.ClientChargesScreen.argument(clientId))
}

fun NavController.navigateClientPinPointScreen(clientId: Int) {
    navigate(ClientScreens.ClientPinPointScreen.argument(clientId))
}

fun NavController.navigateClientSignatureScreen(clientId: Int) {
    navigate(ClientScreens.ClientSignatureScreen.argument(clientId))
}

fun NavController.navigateClientSurveyListScreen(clientId: Int) {
    navigate(ClientScreens.ClientSurveyListScreen.argument(clientId))
}