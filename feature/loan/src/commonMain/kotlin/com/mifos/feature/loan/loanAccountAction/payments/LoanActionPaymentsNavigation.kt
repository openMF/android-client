/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanAccountAction.payments

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object LoanPaymentsActionRoute

fun NavGraphBuilder.loanPaymentsActionDestination(
    navController: NavController,
    onNavigateBack: () -> Unit,
    onGoodwillCreditClick: () -> Unit,
    onInterestPaymentWaiverClick: () -> Unit,
    onPaymentRefundClick: () -> Unit,
    onMerchantIssuedRefundClick: () -> Unit,
) {
    composable<LoanPaymentsActionRoute> {
        LoanPaymentsActionScreen(
            navController = navController,
            onNavigateBack = onNavigateBack,
            onGoodwillCreditClick = onGoodwillCreditClick,
            onInterestPaymentWaiverClick = onInterestPaymentWaiverClick,
            onPaymentRefundClick = onPaymentRefundClick,
            onMerchantIssuedRefundClick = onMerchantIssuedRefundClick,
        )
    }
}

fun NavController.navigateToLoanPaymentsAction() {
    this.navigate(LoanPaymentsActionRoute)
}
