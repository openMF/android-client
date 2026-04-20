/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.newLoanAccount

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class NewLoanAccountRoute(
    val clientId: Int = -1,
    val accountNo: String,
)

fun NavGraphBuilder.newLoanAccountDestination(
    navController: NavController,
    onNavigateBack: () -> Unit,
    onFinish: () -> Unit,
) {
    composable<NewLoanAccountRoute> {
        NewLoanAccountScreen(
            onNavigateBack = onNavigateBack,
            onFinish = onFinish,
            navController = navController,
        )
    }
}

fun NavController.navigateToNewLoanAccountRoute(clientId: Int, accountNo: String) {
    this.navigate(
        NewLoanAccountRoute(clientId, accountNo),
    )
}
