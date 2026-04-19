/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package cmp.navigation.authenticated

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import com.mifos.feature.about.aboutDestination
import com.mifos.feature.activate.activateDestination
import com.mifos.feature.checker.inbox.task.navigation.checkerInboxTaskNavGraph
import com.mifos.feature.client.navigation.navigateClientDetailsScreen
import com.mifos.feature.dataTable.navigation.dataTableNavGraph
import com.mifos.feature.dataTable.navigation.navigateToDataTable
import com.mifos.feature.document.navigation.documentListScreen
import com.mifos.feature.document.navigation.navigateToDocumentListScreen
import com.mifos.feature.individualCollectionSheet.navigation.generateCollectionSheetScreen
import com.mifos.feature.individualCollectionSheet.navigation.individualCollectionSheetNavGraph
import com.mifos.feature.loan.groupLoanAccount.groupLoanScreen
import com.mifos.feature.loan.loanAccount.addLoanAccountScreen
import com.mifos.feature.offline.navigation.offlineNavGraph
import com.mifos.feature.passcode.mifosPasscode.internalMifosPasscodeScreen
import com.mifos.feature.passcode.mifosPasscode.navigateToInternalMifosPasscodeScreen
import com.mifos.feature.path.tracking.navigation.pathTrackingRoute
import com.mifos.feature.report.navigation.reportNavGraph
import com.mifos.feature.settings.navigation.navigateToServerConfigGraph
import com.mifos.feature.settings.navigation.serverConfigGraph
import com.mifos.feature.settings.navigation.settingsScreen
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
internal data object AuthenticatedGraph

internal fun NavController.navigateToAuthenticatedGraph(navOptions: NavOptions? = null) {
    navigate(route = AuthenticatedGraph, navOptions = navOptions)
}

@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
internal fun NavGraphBuilder.authenticatedGraph(
    navController: NavController,
    onClickLogout: () -> Unit,
) {
    navigation<AuthenticatedGraph>(
        startDestination = AuthenticatedNavbar,
    ) {
        // Internal passcode re-verification destination. Used by in-app flows
        // that need a fresh passcode entry (change-passcode, disable-biometrics).
        // On result, writes the verification result (true/false) into the
        // PREVIOUS back-stack entry's SavedStateHandle under the caller's
        // verificationKey, then pops back. Callers (e.g. SettingsScreen)
        // observe that key to react to the outcome.
        internalMifosPasscodeScreen(
            navigateToLogin = onClickLogout,
            onAuthenticationSuccess = { verificationKey ->
                verificationKey?.let {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(it, true)
                }
                navController.popBackStack()
            },
            onAuthenticationFailed = { verificationKey ->
                verificationKey?.let {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(it, false)
                }
                navController.popBackStack()
            },
            onPasscodeChanged = {
                navController.popBackStack()
            },
            onBackNavigation = { verificationKey ->
                verificationKey?.let {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(it, false)
                }
                navController.popBackStack()
            },
        )

        authenticatedNavbarGraph(
            onDrawerItemClick = {
                navController.navigate(it) {
                    launchSingleTop = true
                }
            },
            navigateToDocumentScreen = navController::navigateToDocumentListScreen,
            onMoreInfoClicked = navController::navigateToDataTable,
        )

        checkerInboxTaskNavGraph(navController)

        documentListScreen(onBackPressed = navController::popBackStack)

        dataTableNavGraph(
            navController = navController,
            clientCreated = { client, userStatus ->
                navController.popBackStack()

                if (!userStatus) {
                    client.id?.let { navController.navigateClientDetailsScreen(it) }
                }
            },
        )

        aboutDestination(onBackPressed = navController::popBackStack)

        offlineNavGraph(navController = navController)

        activateDestination(onBackPressed = navController::popBackStack)

        settingsScreen(
            navigateBack = navController::popBackStack,
            navigateToLoginScreen = {},
            // Change passcode: `allowBiometricAuth = false` suppresses the
            // biometric button so the user must re-enter the existing passcode
            // before mutating it. Defense-in-depth; the library's step gate
            // already hides the button during ChangeVerify step.
            changePasscode = {
                navController.navigateToInternalMifosPasscodeScreen(allowBiometricAuth = false)
            },
            onClickUpdateConfig = {
                navController.navigateToServerConfigGraph()
            },
            // Disable biometrics: carries a verificationKey so the internal
            // passcode screen mints a short-lived verification token on
            // success; `allowBiometricAuth = false` ensures the user must
            // re-enter the passcode (biometric bypass would defeat the check).
            // Settings consumes the token in viewModel.disableBiometrics().
            disableBiometrics = { key ->
                navController.navigateToInternalMifosPasscodeScreen(
                    verificationKey = key,
                    allowBiometricAuth = false,
                )
            },
        )

        serverConfigGraph(
            navigateBack = navController::popBackStack,
        )

        individualCollectionSheetNavGraph(
            navController = navController,
            onBackPressed = navController::popBackStack,
        )

        pathTrackingRoute(navController::popBackStack)

        reportNavGraph(navController = navController)

        groupLoanScreen { navController.popBackStack() }

        addLoanAccountScreen(
            onBackPressed = navController::popBackStack,
            dataTable = { _, _ ->
//                navController.navigateDataTableList(dataTable, payload, Constants.CLIENT_LOAN)
//                TODO()
            },
        )

        generateCollectionSheetScreen(navController::popBackStack)
    }
}
