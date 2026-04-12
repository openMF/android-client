/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.passcode.biometricsSetup

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import kotlinx.serialization.Serializable
import template.core.base.ui.composableWithSlideTransitions

@Serializable
data object BiometricsSetupRoute

fun NavController.navigateToBiometricSetupScreen(navOptions: NavOptions? = null) =
    navigate(BiometricsSetupRoute, navOptions)

@OptIn(ExperimentalComposeUiApi::class)
fun NavGraphBuilder.biometricSetupScreen(
    onBiometricsRegistrationSuccess: () -> Unit,
    onSkipBiometricSetup: () -> Unit,
) {
    composableWithSlideTransitions<BiometricsSetupRoute> {
        BiometricSetupScreen(
            onBiometricsRegistrationSuccess,
            onSkipBiometricSetup,
        )
    }
}
