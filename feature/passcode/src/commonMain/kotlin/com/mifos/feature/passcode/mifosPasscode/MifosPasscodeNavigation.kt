/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.passcode.mifosPasscode

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.toRoute
import com.mifos.core.data.repository.UserVerificationRepository
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject
import template.core.base.ui.composableWithSlideTransitions

/**
 * Root passcode destination — shown when the app launches with an existing
 * passcode set. Exit is gated by successful entry or biometric unlock.
 */
@Serializable
data object RootPasscodeRoute

/**
 * Re-auth passcode destination — shown when the app has been backgrounded past
 * the lock timeout. Functionally identical to [RootPasscodeRoute] but pops
 * back on success rather than replacing the graph.
 */
@Serializable
data object ReAuthPasscodeRoute

/**
 * Internal passcode destination — used by in-app flows that need the user to
 * re-enter their passcode (e.g., before disabling biometrics or changing the
 * passcode).
 *
 * @property verificationKey When non-null, successful entry records a short-
 *           lived verification token via [UserVerificationRepository] and
 *           writes the key into the caller's `SavedStateHandle`. Null means
 *           "no token requested" — no verification is minted.
 * @property allowBiometricAuth When `false`, suppresses the biometric button
 *           and auto-auth-on-resume. Set this for flows where biometric bypass
 *           would defeat the security check (e.g., disable biometrics,
 *           change passcode).
 */
@Serializable
data class InternalPasscodeRoute(
    val verificationKey: String? = null,
    val allowBiometricAuth: Boolean = true,
)

/** Navigates to [RootPasscodeRoute]. */
fun NavController.navigateToRootMifosPasscodeScreen(navOptions: NavOptions? = null) =
    navigate(RootPasscodeRoute, navOptions)

/** Navigates to [ReAuthPasscodeRoute]. */
fun NavController.navigateToReAuthMifosPasscodeScreen(navOptions: NavOptions? = null) =
    navigate(ReAuthPasscodeRoute, navOptions)

/**
 * Navigates to [InternalPasscodeRoute].
 *
 * @param verificationKey See [InternalPasscodeRoute.verificationKey].
 * @param allowBiometricAuth See [InternalPasscodeRoute.allowBiometricAuth].
 */
fun NavController.navigateToInternalMifosPasscodeScreen(
    verificationKey: String? = null,
    allowBiometricAuth: Boolean = true,
    navOptions: NavOptions? = null,
) = navigate(InternalPasscodeRoute(verificationKey, allowBiometricAuth), navOptions)

/**
 * Registers the root passcode screen inside a [NavGraphBuilder].
 *
 * @param navigateToLogin Fired on `Forgotten` — passcode cleared; route back to login.
 * @param onAuthenticationSuccess Fired on `Verified` (passcode or biometric).
 * @param onPasscodeCreation Fired on `Created` — first-time passcode set.
 * @param onAuthenticationFailed Fired on `Rejected` — incorrect passcode entered.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun NavGraphBuilder.rootMifosPasscodeScreen(
    navigateToLogin: () -> Unit,
    onAuthenticationSuccess: () -> Unit,
    onPasscodeCreation: () -> Unit,
    onAuthenticationFailed: () -> Unit = {},
) {
    composableWithSlideTransitions<RootPasscodeRoute> {
        MifosPasscode(
            navigateToLogin = navigateToLogin,
            onAuthenticationSuccess = onAuthenticationSuccess,
            onPasscodeCreation = onPasscodeCreation,
            onAuthenticationFailed = onAuthenticationFailed,
            onPasscodeChanged = {},
        )
    }
}

/**
 * Registers the re-auth passcode screen inside a [NavGraphBuilder]. Used when
 * the app returns from background past the lock timeout.
 *
 * @param navigateToLogin Fired on `Forgotten`.
 * @param onAuthenticationSuccess Fired on `Verified`; typically `popBackStack()`.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun NavGraphBuilder.reAuthMifosPasscodeScreen(
    navigateToLogin: () -> Unit,
    onAuthenticationSuccess: () -> Unit,
) {
    composableWithSlideTransitions<ReAuthPasscodeRoute> {
        MifosPasscode(
            navigateToLogin = navigateToLogin,
            onAuthenticationSuccess = onAuthenticationSuccess,
        )
    }
}

/**
 * Registers the internal passcode screen inside a [NavGraphBuilder]. Used by
 * in-app flows that need fresh passcode verification.
 *
 * On `Verified`, records a short-lived token via [UserVerificationRepository]
 * if (and only if) the route carries a non-null `verificationKey`. Callers
 * typically observe their `SavedStateHandle` for the key to react to the
 * result.
 *
 * @param navigateToLogin Fired on `Forgotten`.
 * @param onAuthenticationSuccess `(verificationKey) -> Unit`. Fired on
 *        `Verified`.
 * @param onAuthenticationFailed `(verificationKey) -> Unit`. Fired on
 *        `Rejected`.
 * @param onPasscodeChanged Fired on `Changed` (rare for this route; defensive).
 * @param onBackNavigation `(verificationKey) -> Unit`. Fired when the user
 *        backs out without completing the verification.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun NavGraphBuilder.internalMifosPasscodeScreen(
    navigateToLogin: () -> Unit,
    onAuthenticationSuccess: (String?) -> Unit,
    onAuthenticationFailed: (String?) -> Unit = {},
    onPasscodeChanged: () -> Unit = {},
    onBackNavigation: (String?) -> Unit = {},
) {
    composableWithSlideTransitions<InternalPasscodeRoute> { backStackEntry ->
        val userVerificationRepository = koinInject<UserVerificationRepository>()
        val route = backStackEntry.toRoute<InternalPasscodeRoute>()
        val verificationKey = route.verificationKey

        MifosPasscode(
            navigateToLogin = navigateToLogin,
            onAuthenticationSuccess = {
                verificationKey?.let { userVerificationRepository.recordVerification() }
                onAuthenticationSuccess(verificationKey)
            },
            onPasscodeCreation = {},
            onAuthenticationFailed = { onAuthenticationFailed(verificationKey) },
            onPasscodeChanged = onPasscodeChanged,
            allowBackNavigation = true,
            allowBiometricAuth = route.allowBiometricAuth,
            onBackPress = { onBackNavigation(verificationKey) },
        )
    }
}
