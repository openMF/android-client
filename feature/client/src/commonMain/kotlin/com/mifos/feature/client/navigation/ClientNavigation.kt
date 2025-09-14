/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.navigation

import FormWidgetDTO
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.mifos.core.common.utils.Constants
import com.mifos.feature.client.clientAddDocuments.AddDocumentRoute
import com.mifos.feature.client.clientAddDocuments.clientAddDocumentGraphRoute
import com.mifos.feature.client.clientAddDocuments.navigateToClientAddDocumentRoute
import com.mifos.feature.client.clientAddress.addAddress.clientAddAddressRoute
import com.mifos.feature.client.clientAddress.addAddress.navigateToClientAddAddressRoute
import com.mifos.feature.client.clientAddress.clientAddressNavigation
import com.mifos.feature.client.clientAddress.navigateToClientAddressRoute
import com.mifos.feature.client.clientAddress.navigateToClientAddressRouteOnStatus
import com.mifos.feature.client.clientApplyNewApplications.clientApplyNewApplicationRoute
import com.mifos.feature.client.clientApplyNewApplications.navigateToClientApplyNewApplicationScreen
import com.mifos.feature.client.clientCharges.ClientChargesScreen
import com.mifos.feature.client.clientClosure.clientClosureDestination
import com.mifos.feature.client.clientClosure.navigateToClientClosureRoute
import com.mifos.feature.client.clientCollateral.clientCollateralDestination
import com.mifos.feature.client.clientCollateral.navigateToClientCollateralRoute
import com.mifos.feature.client.clientDetails.ClientDetailsScreen
import com.mifos.feature.client.clientDetailsProfile.clientProfileDetailsDestination
import com.mifos.feature.client.clientDetailsProfile.navigateToClientDetailsProfileRoute
import com.mifos.feature.client.clientDetailsProfile.navigateToClientDetailsProfileRouteOnStatus
import com.mifos.feature.client.clientDocuments.ClientDocumentsRoute
import com.mifos.feature.client.clientDocuments.clientDocumentsDestination
import com.mifos.feature.client.clientDocuments.navigateToClientDocumentsRoute
import com.mifos.feature.client.clientEditDetails.clientEditDetailsDestination
import com.mifos.feature.client.clientEditDetails.navigateToClientEditDetailsRoute
import com.mifos.feature.client.clientEditProfile.clientEditProfileDestination
import com.mifos.feature.client.clientEditProfile.navigateToClientProfileEditProfileRoute
import com.mifos.feature.client.clientGeneral.clientProfileGeneralDestination
import com.mifos.feature.client.clientGeneral.navigateToClientProfileGeneralRoute
import com.mifos.feature.client.clientIdentifiersAddUpdate.clientIdentifiersAddUpdateDestination
import com.mifos.feature.client.clientIdentifiersAddUpdate.onNavigateToClientIdentifiersAddUpdateScreen
import com.mifos.feature.client.clientIdentifiersList.clientIdentifiersListDestination
import com.mifos.feature.client.clientIdentifiersList.navigateBackToUpdateClientIdentifiersListScreen
import com.mifos.feature.client.clientIdentifiersList.navigateToClientIdentifiersListScreen
import com.mifos.feature.client.clientLoanAccounts.clientLoanAccountsDestination
import com.mifos.feature.client.clientLoanAccounts.navigateToClientLoanAccountsRoute
import com.mifos.feature.client.clientPinpoint.PinpointClientScreen
import com.mifos.feature.client.clientProfile.clientProfileDestination
import com.mifos.feature.client.clientProfile.navigateToClientProfileRoute
import com.mifos.feature.client.clientSignature.clientSignatureDestination
import com.mifos.feature.client.clientSignature.navigateToClientSignatureScreen
import com.mifos.feature.client.clientStaff.clientStaffDestination
import com.mifos.feature.client.clientStaff.navigateToClientStaffRoute
import com.mifos.feature.client.clientSurveyList.SurveyListScreen
import com.mifos.feature.client.clientSurveyQuestion.SurveyQuestionScreen
import com.mifos.feature.client.clientTransfer.clientTransferDestination
import com.mifos.feature.client.clientTransfer.navigateToClientTransferRoute
import com.mifos.feature.client.clientUpcomingCharges.clientUpcomingChargesDestination
import com.mifos.feature.client.clientUpcomingCharges.navigateToClientUpcomingChargesRoute
import com.mifos.feature.client.clientUpdateDefaultAccount.navigateToUpdateDefaultAccountRoute
import com.mifos.feature.client.clientUpdateDefaultAccount.updateDefaultAccountDestination
import com.mifos.feature.client.clientsList.ClientListScreen
import com.mifos.feature.client.createNewClient.CreateNewClientScreen
import com.mifos.feature.client.documentPreviewScreen.createDocumentPreviewRoute
import com.mifos.feature.client.documentPreviewScreen.navigateToDocumentPreviewRoute
import com.mifos.feature.client.fixedDepositAccount.clientFixedDepositAccountDestination
import com.mifos.feature.client.fixedDepositAccount.navigateToFixedDepositAccountRoute
import com.mifos.feature.client.recurringDepositAccount.clientRecurringDepositAccountDestination
import com.mifos.feature.client.recurringDepositAccount.navigateToRecurringDepositAccountRoute
import com.mifos.feature.client.savingsAccounts.navigateToClientSavingsAccountsRoute
import com.mifos.feature.client.savingsAccounts.savingsAccountsDestination
import com.mifos.feature.client.shareAccounts.navigateToShareAccountsScreen
import com.mifos.feature.client.shareAccounts.shareAccountsDestination
import com.mifos.room.entities.accounts.savings.SavingAccountDepositTypeEntity
import com.mifos.room.entities.noncore.DataTableEntity
import com.mifos.room.entities.survey.SurveyEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.reflect.KFunction4

@Serializable
data object ClientNavGraph

fun NavGraphBuilder.clientNavGraph(
    navController: NavController,
    addLoanAccount: (Int) -> Unit,
    addSavingsAccount: (Int) -> Unit,
    documents: (Int) -> Unit,
    moreClientInfo: (Int) -> Unit,
    notes: (Int) -> Unit,
    loanAccountSelected: (Int) -> Unit,
    savingsAccountSelected: (Int, SavingAccountDepositTypeEntity) -> Unit,
    activateClient: (Int) -> Unit,
    hasDatatables: KFunction4<List<DataTableEntity>, Any?, Int, MutableList<List<FormWidgetDTO>>, Unit>,
    onDocumentClicked: (Int, String) -> Unit,
    navigateToNewLoanAccount: (Int) -> Unit,
    navigateToNewSavingsAccount: (Int) -> Unit,
) {
    navigation<ClientNavGraph>(
        startDestination = ClientListScreenRoute,
    ) {
        clientListScreenRoute(
            onClientSelect = navController::navigateToClientProfileRoute,
            createNewClient = navController::navigateCreateClientScreen,
        )
        clientDetailRoute(
            onBackPressed = navController::popBackStack,
            addLoanAccount = addLoanAccount,
            addSavingsAccount = addSavingsAccount,
            charges = navController::navigateClientChargesScreen,
            documents = documents,
            identifiers = navController::navigateToClientIdentifiersListScreen,
            moreClientInfo = moreClientInfo,
            notes = notes,
            pinpointLocation = navController::navigateClientPinPointScreen,
            survey = navController::navigateClientSurveyListScreen,
            uploadSignature = navController::navigateToClientSignatureScreen,
            loanAccountSelected = loanAccountSelected,
            savingsAccountSelected = savingsAccountSelected,
            activateClient = activateClient,
        )
        clientChargesRoute(
            onBackPressed = navController::popBackStack,
        )
        clientIdentifiersAddUpdateDestination(
            onBackPressed = navController::popBackStack,
            onUpdatedListBack = navController::navigateBackToUpdateClientIdentifiersListScreen,
            navController = navController,
        )
        clientPinPointRoute(
            onBackPressed = navController::popBackStack,
        )
        clientSignatureDestination(
            onNavigateBack = navController::popBackStack,
            navController = navController,
        )
        clientSurveyListRoute(
            onBackPressed = navController::popBackStack,
            onCardClicked = { clientId, list ->
                navController.navigateToClientSurveyQuestionScreen(clientId, list)
            },
        )
        clientSurveyQuestionRoute(
            onBackPressed = navController::popBackStack,
        )
        createClientRoute(
            onBackPressed = navController::popBackStack,
            hasDatatables = hasDatatables,
        )
        clientProfileDestination(
            onNavigateBack = navController::popBackStack,
            notes = notes,
            documents = navController::navigateToClientDocumentsRoute,
            identifiers = navController::navigateToClientIdentifiersListScreen,
            navigateToClientDetailsScreen = navController::navigateToClientDetailsProfileRoute,
            viewAddress = navController::navigateToClientAddressRoute,
            viewAssociatedAccounts = navController::navigateToClientProfileGeneralRoute,
            navController = navController,
        )

        clientAddressNavigation(
            onNavigateBack = navController::popBackStack,
            navigateToAddAddressForm = navController::navigateToClientAddAddressRoute,
            navController = navController,
        )

        clientAddAddressRoute(
            onNavigateBack = navController::popBackStack,
            navController = navController,
            onNavigateNext = navController::navigateToClientAddressRouteOnStatus,
        )

        clientDocumentsDestination(
            navController = navController,
            navigateBack = navController::popBackStack,
            navigateToAddDocuments = navController::navigateToClientAddDocumentRoute,
            onViewDocument = navController::navigateToDocumentPreviewRoute,
        )

        clientAddDocumentGraphRoute(
            navController = navController,
            navigateBack = {
                navController.popBackStack<AddDocumentRoute>(inclusive = true)
            },
            navigateToDocumentPreview = navController::navigateToDocumentPreviewRoute,
        )

        createDocumentPreviewRoute(
            navigateBack = navController::popBackStack,
            documentRejected = {
                navController.popBackStack<ClientDocumentsRoute>(inclusive = false)
            },
        )

        clientProfileGeneralDestination(
            onNavigateBack = navController::popBackStack,
            navController = navController,
            savingAccounts = navController::navigateToClientSavingsAccountsRoute,
            loanAccounts = navController::navigateToClientLoanAccountsRoute,
            recurringDepositAccounts = navController::navigateToRecurringDepositAccountRoute,
            collateralData = {},
            sharesAccounts = navController::navigateToShareAccountsScreen,
            fixedDepositAccounts = navController::navigateToFixedDepositAccountRoute,
            upcomingCharges = navController::navigateToClientUpcomingChargesRoute,
        )

        clientRecurringDepositAccountDestination(
            navController = navController,
            navigateBack = navController::popBackStack,
            {},
            {},
        )
        clientFixedDepositAccountDestination(
            navController = navController,
            navigateBack = navController::popBackStack,
            onApproveAccount = {},
            onViewAccount = {},
        )

        clientProfileDetailsDestination(
            navController = navController,
            onNavigateBack = navController::popBackStack,
            navigateToUpdatePhoto = navController::navigateToClientProfileEditProfileRoute,
            navigateToAssignStaff = navController::navigateToClientStaffRoute,
            navigateToUpdateDetails = navController::navigateToClientEditDetailsRoute,
            navigateToClientTransfer = navController::navigateToClientTransferRoute,
            navigateToUpdateDefaultAccount = navController::navigateToUpdateDefaultAccountRoute,
            navigateToClientClosure = navController::navigateToClientClosureRoute,
            navigateToCollateral = navController::navigateToClientCollateralRoute,
            navigateToApplyNewApplication = navController::navigateToClientApplyNewApplicationScreen,
            navigateToUpdateSignature = navController::navigateToClientSignatureScreen,

        )
        clientEditProfileDestination(
            onNavigateBack = navController::popBackStack,
            navController = navController,
        )
        clientEditDetailsDestination(
            onNavigateBack = navController::popBackStack,
            onNavigateNext = navController::navigateToClientDetailsProfileRouteOnStatus,
            navController = navController,
        )
        clientStaffDestination(
            onNavigateBack = navController::popBackStack,
            onNavigateNext = navController::navigateToClientDetailsProfileRouteOnStatus,
            navController = navController,
        )
        clientTransferDestination(
            onNavigateBack = navController::popBackStack,
            onNavigateNext = navController::navigateToClientDetailsProfileRouteOnStatus,
            navController = navController,
        )
        updateDefaultAccountDestination(
            onNavigateBack = navController::popBackStack,
            onNavigateNext = navController::navigateToClientDetailsProfileRouteOnStatus,
            navController = navController,
        )
        clientClosureDestination(
            onNavigateBack = navController::popBackStack,
            onNavigateNext = navController::navigateToClientDetailsProfileRouteOnStatus,
            navController = navController,
        )
        savingsAccountsDestination(
            navigateBack = navController::popBackStack,
            navigateToViewAccount = { },
            navController = navController,
        )
        clientCollateralDestination(
            onNavigateBack = navController::popBackStack,
            onNavigateNext = navController::navigateToClientDetailsProfileRouteOnStatus,
            navController = navController,
        )
        clientLoanAccountsDestination(
            navigateBack = navController::popBackStack,
            navigateToViewAccount = {},
            navigateToMakeRepayment = {},
        )
        clientIdentifiersListDestination(
            addNewClientIdentity = navController::onNavigateToClientIdentifiersAddUpdateScreen,
            onBackPress = navController::popBackStack,
            navController = navController,
        )
        clientApplyNewApplicationRoute(
            onNavigateBack = navController::popBackStack,
            onNavigateApplyLoanAccount = navigateToNewLoanAccount,
            onNavigateApplySavingsAccount = navigateToNewSavingsAccount,
            onNavigateApplyShareAccount = { },
            onNavigateApplyRecurringAccount = { },
            onNavigateApplyFixedAccount = { },
            navController = navController,
        )
        clientUpcomingChargesDestination(
            navController = navController,
            payOutstandingAmount = {},

        )
        shareAccountsDestination(
            navController = navController,
            navigateToViewAccount = {},
        )
    }
}

fun NavGraphBuilder.clientListScreenRoute(
    onClientSelect: (Int) -> Unit,
    createNewClient: () -> Unit,
) {
    composable<ClientListScreenRoute> {
        ClientListScreen(
            createNewClient = createNewClient,
            onClientClick = onClientSelect,
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
    uploadSignature: (Int, String, String) -> Unit,
    loanAccountSelected: (Int) -> Unit,
    savingsAccountSelected: (Int, SavingAccountDepositTypeEntity) -> Unit,
    activateClient: (Int) -> Unit,
) {
    composable(
        route = ClientScreens.ClientDetailScreen.route,
        arguments = listOf(navArgument(Constants.CLIENT_ID, builder = { type = NavType.IntType })),
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
            activateClient = activateClient,
        )
    }
}

fun NavGraphBuilder.clientChargesRoute(
    onBackPressed: () -> Unit,
) {
    composable(
        route = ClientScreens.ClientChargesScreen.route,
        arguments = listOf(navArgument(Constants.CLIENT_ID, builder = { type = NavType.IntType })),
    ) {
        ClientChargesScreen(
            onBackPressed = onBackPressed,
        )
    }
}

fun NavGraphBuilder.clientPinPointRoute(
    onBackPressed: () -> Unit,
) {
    composable(
        route = ClientScreens.ClientPinPointScreen.route,
        arguments = listOf(navArgument(Constants.CLIENT_ID, builder = { type = NavType.IntType })),
    ) {
        PinpointClientScreen(
            onBackPressed = onBackPressed,
        )
    }
}
fun NavGraphBuilder.clientSurveyListRoute(
    onBackPressed: () -> Unit,
    onCardClicked: (Int, SurveyEntity) -> Unit,
) {
    composable(
        route = ClientScreens.ClientSurveyListScreen.route,
        arguments = listOf(navArgument(Constants.CLIENT_ID, builder = { type = NavType.IntType })),
    ) {
        SurveyListScreen(
            navigateBack = onBackPressed,
            onCardClicked = onCardClicked,
        )
    }
}

fun NavGraphBuilder.clientSurveyQuestionRoute(
    onBackPressed: () -> Unit,
) {
    composable(
        route = ClientScreens.ClientSurveyQuestionScreen.route,
        arguments = listOf(navArgument(Constants.CLIENT_ID, builder = { type = NavType.IntType })),
    ) {
        SurveyQuestionScreen(
            navigateBack = onBackPressed,
        )
    }
}

fun NavGraphBuilder.createClientRoute(
    onBackPressed: () -> Unit,
    hasDatatables: KFunction4<List<DataTableEntity>, Any?, Int, MutableList<List<FormWidgetDTO>>, Unit>,
) {
    composable(
        route = ClientScreens.CreateClientScreen.route,
    ) {
        CreateNewClientScreen(
            navigateBack = onBackPressed,
            hasDatatables = { datatables, clientPayload ->
                hasDatatables(datatables, clientPayload, Constants.CREATE_CLIENT, mutableListOf())
            },
        )
    }
}

fun NavController.navigateClientDetailsScreen(clientId: Int) {
    navigate(ClientScreens.ClientDetailScreen.argument(clientId))
}

fun NavController.navigateClientChargesScreen(clientId: Int) {
    navigate(ClientScreens.ClientChargesScreen.argument(clientId))
}

fun NavController.navigateClientPinPointScreen(clientId: Int) {
    navigate(ClientScreens.ClientPinPointScreen.argument(clientId))
}

fun NavController.navigateClientSurveyListScreen(clientId: Int) {
    navigate(ClientScreens.ClientSurveyListScreen.argument(clientId))
}

fun NavController.navigateToClientSurveyQuestionScreen(clientId: Int, survey: SurveyEntity) {
    val arg = Json.encodeToString(survey)
    navigate(ClientScreens.ClientSurveyQuestionScreen.argument(clientId, arg))
}

fun NavController.navigateCreateClientScreen() {
    navigate(ClientScreens.CreateClientScreen.route)
}

@Serializable
data object ClientListScreenRoute

fun NavController.navigateToClientListScreen() {
    navigate(ClientListScreenRoute)
}
