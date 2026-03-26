/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanReschedules

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class LoanReschedulesRoute(
    val loanId: Int,
)

fun NavController.navigateToLoanReschedulesScreen(loanId: Int) {
    navigate(LoanReschedulesRoute(loanId = loanId))
}

fun NavGraphBuilder.loanReschedulesScreen(
    navController: NavController,
    onBackPressed: () -> Unit,
) {
    composable<LoanReschedulesRoute> {
        LoanReschedulesScreenRoute(
            navController = navController,
            navigateBack = onBackPressed,
        )
    }
}
