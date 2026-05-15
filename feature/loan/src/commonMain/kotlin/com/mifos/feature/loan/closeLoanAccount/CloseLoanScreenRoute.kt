/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.closeLoanAccount

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

/**
 * Navigation route for the Close Loan Account screen.
 *
 * @property loanId The unique identifier of the loan account being closed.
 */
@Serializable
data class CloseLoanScreenRoute(
    val loanId: Int,
)

/**
 * Registers the Close Loan Account destination.
 *
 * @param navController The navigation controller used to pop the stack on success.
 * @param onBackPressed The callback invoked when the user cancels the operation.
 */
fun NavGraphBuilder.closeLoanAccountScreen(
    navController: NavController,
    onBackPressed: () -> Unit,
) {
    composable<CloseLoanScreenRoute> {
        CloseLoanScreen(
            onBackPressed = onBackPressed,
            onCloseSuccess = { navController.popBackStack() },
        )
    }
}

/**
 * Navigates to the Close Loan Account screen for a specific loan.
 *
 * @param loanId The unique identifier of the loan account.
 */
fun NavController.navigateToCloseLoanScreen(loanId: Int) {
    navigate(CloseLoanScreenRoute(loanId = loanId))
}
