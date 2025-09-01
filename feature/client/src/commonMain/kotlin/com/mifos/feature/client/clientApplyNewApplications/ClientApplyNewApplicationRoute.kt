/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientApplyNewApplications

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object ClientApplyNewApplicationRoute

fun NavGraphBuilder.clientApplyNewApplicationRoute(
    onNavigateBack: () -> Unit,
    onNavigateApplyLoanAccount: () -> Unit,
    onNavigateApplySavingsAccount: () -> Unit,
    onNavigateApplyShareAccount: () -> Unit,
    onNavigateApplyRecurringAccount: () -> Unit,
    onNavigateApplyFixedAccount: () -> Unit,
) {
    composable<ClientApplyNewApplicationRoute> {
        ClientApplyNewApplicationsScreen(
            onNavigateBack = onNavigateBack,
            onNavigateApplyLoanAccount = onNavigateApplyLoanAccount,
            onNavigateApplySavingsAccount = onNavigateApplySavingsAccount,
            onNavigateApplyShareAccount = onNavigateApplyShareAccount,
            onNavigateApplyRecurringAccount = onNavigateApplyRecurringAccount,
            onNavigateApplyFixedAccount = onNavigateApplyFixedAccount,
        )
    }
}

fun NavController.navigateToClientApplyNewApplicationScreen() {
    this.navigate(ClientApplyNewApplicationRoute)
}
