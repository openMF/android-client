/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanAccountDetails

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class LoanAccountDetailsRoute(
    val loanId: Int,
)

fun NavController.navigateToLoanAccountDetailsScreen(loanId: Int) {
    navigate(LoanAccountDetailsRoute(loanId = loanId))
}

fun NavGraphBuilder.loanAccountDetails(
    onBackPressed: () -> Unit,
    navController: NavController,
) {
    composable<LoanAccountDetailsRoute> {
        LoanAccountDetailsScreenRoute(
            onNavigateBack = onBackPressed,
            navController = navController,
        )
    }
}
