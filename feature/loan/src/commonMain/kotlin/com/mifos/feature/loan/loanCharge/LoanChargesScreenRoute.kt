/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanCharge

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class LoanChargesScreenRoute(
    val loanAccountNumber: Int,
)

fun NavController.navigateToLoanChargesScreen(loanAccountNumber: Int) {
    navigate(LoanChargesScreenRoute(loanAccountNumber))
}

fun NavGraphBuilder.loanChargeScreen(
    onBackPressed: () -> Unit,
) {
    composable<LoanChargesScreenRoute> {
        LoanChargeScreen(
            onBackPressed = onBackPressed,
        )
    }
}
