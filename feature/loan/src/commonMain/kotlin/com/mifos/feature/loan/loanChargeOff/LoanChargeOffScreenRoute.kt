/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanChargeOff

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class LoanChargeOffRoute(val loanId: Int)

fun NavGraphBuilder.loanChargeOffScreen(
    onNavigateBack: () -> Unit,
    onChargeOffSuccess: (loanId: Int) -> Unit,
) {
    composable<LoanChargeOffRoute> {
        LoanChargeOffScreen(
            onNavigateBack = onNavigateBack,
            onChargeOffSuccess = onChargeOffSuccess,
        )
    }
}

fun NavController.navigateToLoanChargeOffScreen(loanId: Int) {
    navigate(LoanChargeOffRoute(loanId))
}
