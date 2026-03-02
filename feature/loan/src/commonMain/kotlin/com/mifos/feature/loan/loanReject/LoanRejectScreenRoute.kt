/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanReject

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

internal const val LOAN_REJECT_SUCCESS_RESULT_KEY = "loan_reject_success_result"

@Serializable
internal data class LoanRejectScreenRoute(
    val loanId: Int,
)

internal fun NavController.navigateToLoanRejectScreen(loanId: Int) {
    navigate(LoanRejectScreenRoute(loanId))
}

internal fun NavGraphBuilder.loanRejectScreen(
    onBackPressed: () -> Unit,
    onRejectSuccess: () -> Unit,
) {
    composable<LoanRejectScreenRoute> {
        RejectLoanScreen(
            navigateBack = onBackPressed,
            onRejectSuccess = onRejectSuccess,
        )
    }
}
