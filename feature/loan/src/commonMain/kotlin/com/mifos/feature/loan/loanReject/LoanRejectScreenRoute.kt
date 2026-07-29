/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanReject

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class LoanRejectRoute(val loanId: Int)

fun NavGraphBuilder.loanRejectScreen(
    onNavigateBack: () -> Unit,
    onRejectSuccess: (loanId: Int) -> Unit,
) {
    composable<LoanRejectRoute> {
        LoanRejectScreen(
            onNavigateBack = onNavigateBack,
            onRejectSuccess = onRejectSuccess,
        )
    }
}

fun NavController.navigateToLoanRejectScreen(loanId: Int) {
    navigate(LoanRejectRoute(loanId))
}
