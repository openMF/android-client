/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanDisburse

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class LoanDisburseRoute(
    val loanId: Int,
)

fun NavGraphBuilder.loanDisburseScreen(
    onNavigateBack: () -> Unit,
    onDisburseSuccess: (loanId: Int) -> Unit,
) {
    composable<LoanDisburseRoute> {
        LoanDisburseScreen(
            onNavigateBack = onNavigateBack,
            onDisburseSuccess = onDisburseSuccess,
        )
    }
}

fun NavController.navigateToLoanDisburseScreen(loanId: Int) {
    this.navigate(
        LoanDisburseRoute(loanId = loanId),
    )
}
