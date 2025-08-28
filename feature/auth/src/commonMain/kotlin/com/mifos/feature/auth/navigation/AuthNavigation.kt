/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mifos.feature.auth.login.LoginScreen
import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

fun NavGraphBuilder.authNavGraph(
    navigateHome: () -> Unit,
    navigatePasscode: () -> Unit,
    updateServerConfig: () -> Unit,
) {
    composable<LoginRoute> {
        LoginScreen(
            homeIntent = navigateHome,
            passcodeIntent = navigatePasscode,
            onClickToUpdateServerConfig = updateServerConfig,
        )
    }
}

fun NavController.navigateToLogin() {
    this.navigate(LoginRoute)
}
