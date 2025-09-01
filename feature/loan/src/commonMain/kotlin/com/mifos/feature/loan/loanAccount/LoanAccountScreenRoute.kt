/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanAccount

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mifos.core.network.model.LoansPayload
import com.mifos.room.entities.noncore.DataTableEntity
import kotlinx.serialization.Serializable

@Serializable
data class LoanAccountScreenRoute(
    val clientId: Int,
)

fun NavController.navigateToLoanAccountScreen(clientId: Int) {
    navigate(LoanAccountScreenRoute(clientId))
}

fun NavGraphBuilder.addLoanAccountScreen(
    onBackPressed: () -> Unit,
    dataTable: (List<DataTableEntity>, LoansPayload) -> Unit,
) {
    composable<LoanAccountScreenRoute> {
        LoanAccountScreen(
            onBackPressed = onBackPressed,
            dataTable = dataTable,
        )
    }
}
