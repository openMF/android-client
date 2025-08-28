/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanDisbursement

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class LoanDisbursementScreenRoute(
    val loanAccountNumber: Int,
)

fun NavController.navigateToLoanDisbursementScreen(loanAccountNumber: Int) {
    navigate(LoanDisbursementScreenRoute(loanAccountNumber))
}

fun NavGraphBuilder.loanDisbursementScreen(
    onBackPressed: () -> Unit,
) {
    composable<LoanDisbursementScreenRoute> {
        LoanAccountDisbursementScreen(
            navigateBack = onBackPressed,
        )
    }
}
