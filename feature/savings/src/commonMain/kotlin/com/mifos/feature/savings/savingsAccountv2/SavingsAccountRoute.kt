/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.savings.savingsAccountv2

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class SavingsAccountRoute(
    val clientId: Int = -1,
)

fun NavGraphBuilder.savingsAccountDestination(
    navController: NavController,
    onNavigateBack: () -> Unit,
    onFinish: () -> Unit,
) {
    composable<SavingsAccountRoute> {
        SavingsAccountScreen(
            onNavigateBack = onNavigateBack,
            onFinish = onFinish,
            navController = navController,
        )
    }
}

fun NavController.navigateToSavingsAccountRoute(clientId: Int) {
    this.navigate(
        SavingsAccountRoute(clientId = clientId),
    )
}
