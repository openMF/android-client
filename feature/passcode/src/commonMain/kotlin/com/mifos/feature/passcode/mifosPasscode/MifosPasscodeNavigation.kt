/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package com.mifos.feature.passcode.mifosPasscode

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import template.core.base.ui.composableWithSlideTransitions
import template.core.base.ui.composableWithStayTransitions


@Serializable
data object RootPasscodeRoute

@Serializable
data object ReAuthPasscodeRoute

@Serializable
data class InternalPasscodeRoute(val verificationKey: String? = null)

fun NavController.navigateToRootMifosPasscodeScreen(navOptions: NavOptions? = null) =
    navigate(RootPasscodeRoute, navOptions)

fun NavController.navigateToReAuthMifosPasscodeScreen(navOptions: NavOptions? = null) =
    navigate(ReAuthPasscodeRoute, navOptions)

fun NavController.navigateToInternalMifosPasscodeScreen(
    verificationKey: String? = null,
    navOptions: NavOptions? = null,
) = navigate(InternalPasscodeRoute(verificationKey), navOptions)


@OptIn(ExperimentalComposeUiApi::class)
fun NavGraphBuilder.rootMifosPasscodeScreen(
    onForgotButton: () -> Unit,
    onAuthenticationSuccess: () -> Unit,
    onPasscodeCreation: () -> Unit = {},
    onAuthenticationFailed: () -> Unit = {},
    onPasscodeChanged: () -> Unit = {},
    onDisableBiometrics: () -> Unit = {},
) {
    composableWithStayTransitions<RootPasscodeRoute> {
        MifosPasscode(
            onForgotButton = onForgotButton,
            onAuthenticationSuccess = onAuthenticationSuccess,
            onPasscodeCreation = onPasscodeCreation,
            onAuthenticationFailed = onAuthenticationFailed,
            onPasscodeChanged = onPasscodeChanged,
            onBiometricsDisabled = onDisableBiometrics,
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun NavGraphBuilder.reAuthMifosPasscodeScreen(
    onForgotButton: () -> Unit,
    onAuthenticationSuccess: () -> Unit,
    onAuthenticationFailed: () -> Unit = {},
    onPasscodeChanged: () -> Unit = {},
    onDisableBiometrics: () -> Unit = {},
) {
    composableWithSlideTransitions<ReAuthPasscodeRoute> {
        MifosPasscode(
            onForgotButton = onForgotButton,
            onAuthenticationSuccess = onAuthenticationSuccess,
            onPasscodeCreation = {},
            onAuthenticationFailed = onAuthenticationFailed,
            onPasscodeChanged = onPasscodeChanged,
            onBiometricsDisabled = onDisableBiometrics,
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun NavGraphBuilder.internalMifosPasscodeScreen(
    onForgotButton: () -> Unit,
    onAuthenticationSuccess: (String?) -> Unit,
    onAuthenticationFailed: (String?) -> Unit = {},
    onPasscodeChanged: () -> Unit = {},
    onDisableBiometrics: () -> Unit = {},
    onBackNavigation: (String?) -> Unit = {},
) {
    composableWithSlideTransitions<InternalPasscodeRoute> { backStackEntry ->
        val verificationKey = backStackEntry.toRoute<InternalPasscodeRoute>().verificationKey
        MifosPasscode(
            onForgotButton = onForgotButton,
            onAuthenticationSuccess = { onAuthenticationSuccess(verificationKey) },
            onPasscodeCreation = {},
            onAuthenticationFailed = { onAuthenticationFailed(verificationKey) },
            onPasscodeChanged = onPasscodeChanged,
            onBiometricsDisabled = onDisableBiometrics,
            allowBackNavigation = true,
            onBackPress = { onBackNavigation(verificationKey) },
        )
    }
}
