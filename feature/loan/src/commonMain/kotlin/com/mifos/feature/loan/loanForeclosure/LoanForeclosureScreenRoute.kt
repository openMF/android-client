/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanForeclosure

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class LoanForeclosureScreenRoute(
    val loanAccountNumber: Int,
)

fun NavController.navigateToLoanForeclosureScreen(loanAccountNumber: Int) {
    navigate(LoanForeclosureScreenRoute(loanAccountNumber))
}

fun NavGraphBuilder.loanForeclosureScreen(
    navController: NavController,
    onBackPressed: () -> Unit,
) {
    composable<LoanForeclosureScreenRoute> {
        LoanForeclosureScreen(
            navController = navController,
            onNavigateBack = onBackPressed,
            onSubmitted = onBackPressed,
        )
    }
}
