/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanAccountGeneral

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class LoanAccountGeneralRoute(
    val loanId: Int = -1,
)

fun NavGraphBuilder.loanAccountGeneralDestination(
    navController: NavController,
) {
    composable<LoanAccountGeneralRoute> {
        LoanAccountGeneralScreen(
            navController = navController,
        )
    }
}

fun NavController.navigateToLoanAccountGeneralScreen(loanId: Int) {
    this.navigate(LoanAccountGeneralRoute(loanId = loanId))
}
